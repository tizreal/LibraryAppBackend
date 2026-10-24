package com.tca.libraryApp.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tca.libraryApp.support.PostgresTestBase;

/** With LIBRARY_DEMO_RESET=true the sample data is in place as soon as the app is ready. */
@SpringBootTest(properties = "library.demo.reset-enabled=true")
class DemoResetOnStartupIT extends PostgresTestBase {

	@Autowired JdbcTemplate jdbc;

	@Test
	void sampleDataIsLoadedOnStartup() {
		assertThat(jdbc.queryForObject("SELECT count(*) FROM books", Long.class)).isEqualTo(DemoDataset.BOOKS.size());
		assertThat(jdbc.queryForObject("SELECT count(*) FROM users", Long.class)).isEqualTo(DemoDataset.MEMBERS.size());
	}
}
