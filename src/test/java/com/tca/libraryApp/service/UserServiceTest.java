package com.tca.libraryApp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.tca.libraryApp.entity.User;
import com.tca.libraryApp.repository.UserRepository;

class UserServiceTest {

	@Test
	void updateUserPersistsFirstNameLastNameAndUsername() {
		UserRepository repository = mock(UserRepository.class);
		User existing = new User();
		existing.setFirstName("Ada");
		existing.setLastName("Lovelace");
		existing.setUserNameString("ada");
		when(repository.findById(1)).thenReturn(Optional.of(existing));
		when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		User changes = new User();
		changes.setFirstName("Augusta");
		changes.setLastName("King");
		changes.setUserNameString("augusta");

		User saved = new UserService(repository).updateUser(1, changes);

		assertThat(saved.getFirstName()).isEqualTo("Augusta");
		assertThat(saved.getLastName()).isEqualTo("King");
		assertThat(saved.getUserNameString()).isEqualTo("augusta");
	}
}
