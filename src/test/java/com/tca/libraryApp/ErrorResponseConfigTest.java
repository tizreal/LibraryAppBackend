package com.tca.libraryApp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/** Error responses must not expose stack traces or internal details. */
class ErrorResponseConfigTest {

	@Test
	void errorResponsesHideInternalDetails() throws Exception {
		Properties props = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"));
		assertThat(props.getProperty("spring.web.error.include-stacktrace")).isEqualTo("never");
		assertThat(props.getProperty("spring.web.error.include-exception")).isEqualTo("false");
		assertThat(props.getProperty("spring.web.error.include-message")).isEqualTo("never");
		assertThat(props.getProperty("spring.web.error.include-binding-errors")).isEqualTo("never");
	}
}
