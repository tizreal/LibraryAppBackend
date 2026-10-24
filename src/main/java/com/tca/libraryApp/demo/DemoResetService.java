package com.tca.libraryApp.demo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.tca.libraryApp.demo.DemoDataset.SampleBook;
import com.tca.libraryApp.demo.DemoDataset.SampleMember;

/**
 * Resets the public demo to the sample dataset. Off unless LIBRARY_DEMO_RESET=true.
 *
 * One transaction: TRUNCATE (transactional in PostgreSQL) and re-insert, so a failure leaves
 * the previous data untouched. An advisory lock makes overlapping runs skip instead of
 * repeating the work. Guards refuse to run against a database that doesn't look like this
 * app's own demo database.
 */
@Service
public class DemoResetService {

	public enum Outcome { DISABLED, SKIPPED_ALREADY_RUNNING, REFUSED, RESET }

	private static final Logger log = LoggerFactory.getLogger(DemoResetService.class);
	private static final long LOCK_KEY = 0x4C49425245534554L;
	// The library "users" table has exactly these columns. Any other column (e.g. email or
	// password) means this is some other application's database: refuse.
	private static final Set<String> LIBRARY_USER_COLUMNS =
			Set.of("user_id", "first_name", "last_name", "user_name", "created_at", "updated_at");

	private final JdbcTemplate jdbc;
	private final TransactionTemplate tx;
	private final boolean enabled;
	private final long maxRows;

	public DemoResetService(JdbcTemplate jdbc, TransactionTemplate tx,
			@Value("${library.demo.reset-enabled:false}") boolean enabled,
			@Value("${library.demo.max-rows:1000}") long maxRows) {
		this.jdbc = jdbc;
		this.tx = tx;
		this.enabled = enabled;
		this.maxRows = maxRows;
	}

	public Outcome reset() {
		return resetTo(DemoDataset.BOOKS, DemoDataset.MEMBERS);
	}

	Outcome resetTo(List<SampleBook> books, List<SampleMember> members) {
		if (!enabled) {
			return Outcome.DISABLED;
		}
		return tx.execute(status -> {
			Boolean locked = jdbc.queryForObject("SELECT pg_try_advisory_xact_lock(?)", Boolean.class, LOCK_KEY);
			if (!Boolean.TRUE.equals(locked)) {
				return Outcome.SKIPPED_ALREADY_RUNNING;
			}
			String refusal = refusalReason();
			if (refusal != null) {
				log.warn("Demo reset refused: {}", refusal);
				return Outcome.REFUSED;
			}
			jdbc.execute("TRUNCATE books, users RESTART IDENTITY");
			for (SampleBook b : books) {
				jdbc.update("INSERT INTO books (title, author, isbn, publication_year) VALUES (?, ?, ?, ?)",
						b.title(), b.author(), b.isbn(), b.publicationYear());
			}
			for (SampleMember m : members) {
				jdbc.update("INSERT INTO users (first_name, last_name, user_name, created_at, updated_at) "
						+ "VALUES (?, ?, ?, now(), now())", m.firstName(), m.lastName(), m.userName());
			}
			log.info("Demo data reset: {} books, {} members", books.size(), members.size());
			return Outcome.RESET;
		});
	}

	private String refusalReason() {
		Set<String> columns = new HashSet<>(jdbc.queryForList(
				"SELECT column_name FROM information_schema.columns "
						+ "WHERE table_schema = current_schema() AND table_name = 'users'", String.class));
		if (!LIBRARY_USER_COLUMNS.containsAll(columns)) {
			columns.removeAll(LIBRARY_USER_COLUMNS);
			return "the users table has columns this app does not define " + columns;
		}
		Long rows = jdbc.queryForObject("SELECT (SELECT count(*) FROM books) + (SELECT count(*) FROM users)", Long.class);
		if (rows != null && rows > maxRows) {
			return rows + " rows exceed the safety limit of " + maxRows;
		}
		return null;
	}
}
