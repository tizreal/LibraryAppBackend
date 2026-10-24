package com.tca.libraryApp.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import com.tca.libraryApp.demo.DemoDataset.SampleBook;
import com.tca.libraryApp.demo.DemoResetService.Outcome;
import com.tca.libraryApp.support.PostgresTestBase;

/** The demo reset against a real PostgreSQL: exact, repeatable, atomic, guarded. */
@SpringBootTest
class DemoResetServiceIT extends PostgresTestBase {

	@Autowired JdbcTemplate jdbc;
	@Autowired TransactionTemplate tx;

	private DemoResetService enabled() {
		return new DemoResetService(jdbc, tx, true, 1000);
	}

	@BeforeEach
	void visitorData() {
		jdbc.execute("TRUNCATE books, users RESTART IDENTITY");
		jdbc.update("INSERT INTO books (title, author, isbn, publication_year) VALUES ('Visitor book', 'Someone', '1234567890123', 2020)");
		jdbc.update("INSERT INTO users (first_name, last_name, user_name) VALUES ('Visitor', 'Edit', 'visitor')");
	}

	@AfterEach
	void dropForeignColumn() {
		jdbc.execute("ALTER TABLE users DROP COLUMN IF EXISTS email");
	}

	private List<String> books() {
		return jdbc.queryForList("SELECT book_id || '|' || title || '|' || author || '|' || isbn || '|' || coalesce(publication_year, 0) FROM books ORDER BY book_id", String.class);
	}

	private List<String> members() {
		return jdbc.queryForList("SELECT user_id || '|' || first_name || '|' || last_name || '|' || user_name FROM users ORDER BY user_id", String.class);
	}

	@Test
	void resetReplacesEverythingWithTheExactSampleData() {
		assertThat(enabled().reset()).isEqualTo(Outcome.RESET);

		List<String> expectedBooks = new ArrayList<>();
		for (int i = 0; i < DemoDataset.BOOKS.size(); i++) {
			SampleBook b = DemoDataset.BOOKS.get(i);
			expectedBooks.add((i + 1) + "|" + b.title() + "|" + b.author() + "|" + b.isbn() + "|" + b.publicationYear());
		}
		assertThat(books()).containsExactlyElementsOf(expectedBooks);
		assertThat(members()).hasSize(DemoDataset.MEMBERS.size()).first().isEqualTo("1|Maya|Chen|mchen");
		assertThat(jdbc.queryForObject("SELECT count(*) FROM books WHERE title = 'Visitor book'", Long.class)).isZero();
	}

	@Test
	void resettingTwiceGivesTheSameResult() {
		enabled().reset();
		List<String> firstBooks = books();
		List<String> firstMembers = members();

		jdbc.update("DELETE FROM books WHERE book_id = 3");
		jdbc.update("UPDATE users SET first_name = 'Changed' WHERE user_id = 1");
		enabled().reset();

		assertThat(books()).isEqualTo(firstBooks);
		assertThat(members()).isEqualTo(firstMembers);
	}

	@Test
	void aFailedResetLeavesThePreviousDataIntact() {
		List<String> before = books();
		List<SampleBook> brokenDataset = List.of(
				new SampleBook("A", "Author", "9780141439518", 2000),
				new SampleBook("B", "Author", "9780141439518", 2001)); // duplicate ISBN fails the insert

		assertThatThrownBy(() -> enabled().resetTo(brokenDataset, DemoDataset.MEMBERS)).isInstanceOf(RuntimeException.class);

		assertThat(books()).isEqualTo(before);
		assertThat(members()).containsExactly("1|Visitor|Edit|visitor");
	}

	@Test
	void concurrentResetsEndInTheSampleData() throws Exception {
		ExecutorService pool = Executors.newFixedThreadPool(4);
		List<Outcome> outcomes = new ArrayList<>();
		try {
			CountDownLatch start = new CountDownLatch(1);
			List<Future<Outcome>> runs = new ArrayList<>();
			for (int i = 0; i < 4; i++) {
				runs.add(pool.submit(() -> {
					start.await();
					return enabled().reset();
				}));
			}
			start.countDown();
			for (Future<Outcome> run : runs) {
				outcomes.add(run.get());
			}
		} finally {
			pool.shutdown();
		}

		assertThat(outcomes).contains(Outcome.RESET);
		assertThat(outcomes).allMatch(o -> o == Outcome.RESET || o == Outcome.SKIPPED_ALREADY_RUNNING);
		assertThat(books()).hasSize(DemoDataset.BOOKS.size());
		assertThat(members()).hasSize(DemoDataset.MEMBERS.size());
	}

	@Test
	void doesNothingWhenDisabled() {
		assertThat(new DemoResetService(jdbc, tx, false, 1000).reset()).isEqualTo(Outcome.DISABLED);
		assertThat(books()).containsExactly("1|Visitor book|Someone|1234567890123|2020");
	}

	@Test
	void refusesADatabaseWhoseUsersTableIsNotThisApps() {
		jdbc.execute("ALTER TABLE users ADD COLUMN email VARCHAR(255)");

		assertThat(enabled().reset()).isEqualTo(Outcome.REFUSED);
		assertThat(books()).containsExactly("1|Visitor book|Someone|1234567890123|2020");
	}

	@Test
	void refusesWhenTheDataIsLargerThanTheSafetyLimit() {
		assertThat(new DemoResetService(jdbc, tx, true, 1).reset()).isEqualTo(Outcome.REFUSED);
		assertThat(members()).containsExactly("1|Visitor|Edit|visitor");
	}

	@Test
	void sampleIsbnsAreValidIsbn13() {
		for (SampleBook b : DemoDataset.BOOKS) {
			int sum = 0;
			for (int i = 0; i < 12; i++) {
				sum += (b.isbn().charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);
			}
			assertThat((10 - sum % 10) % 10).as(b.title()).isEqualTo(b.isbn().charAt(12) - '0');
		}
	}
}
