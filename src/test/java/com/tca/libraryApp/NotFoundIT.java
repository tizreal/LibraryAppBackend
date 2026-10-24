package com.tca.libraryApp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.tca.libraryApp.support.PostgresTestBase;

/** Updating, deleting or reading a record that doesn't exist answers 404, never 500. */
@SpringBootTest
@AutoConfigureMockMvc
class NotFoundIT extends PostgresTestBase {

	@Autowired MockMvc mvc;

	@Test
	void missingMemberIs404() throws Exception {
		mvc.perform(put("/api/users/999999").contentType(MediaType.APPLICATION_JSON)
				.content("{\"first_name\":\"a\",\"last_name\":\"b\",\"user_name_string\":\"c\"}"))
				.andExpect(status().isNotFound());
		mvc.perform(delete("/api/users/999999")).andExpect(status().isNotFound());
		mvc.perform(get("/api/users/999999")).andExpect(status().isNotFound());
	}

	@Test
	void missingBookIs404() throws Exception {
		mvc.perform(put("/api/books/999999").contentType(MediaType.APPLICATION_JSON)
				.content("{\"title\":\"t\",\"author\":\"a\",\"isbn\":\"9780141439518\",\"publication_year\":2000}"))
				.andExpect(status().isNotFound());
		mvc.perform(delete("/api/books/999999")).andExpect(status().isNotFound());
		mvc.perform(get("/api/books/999999")).andExpect(status().isNotFound());
	}
}
