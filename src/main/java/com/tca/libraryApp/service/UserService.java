package com.tca.libraryApp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tca.libraryApp.entity.User;
import com.tca.libraryApp.repository.UserRepository;

@Service
public class UserService {

	// We declare the dependency but let Spring create and supply the instance.
	// This is Dependency Injection (DI) — a core Spring concept.
	private final UserRepository userRepository;

	// @Autowired on the constructor tells Spring to inject UserRepository here.
	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// ── CREATE ─────────────────────────────────────────────────────────────
	// Sets both timestamps to 'now' before persisting to the database.
	public User createUser(User user) {
		user.setCreatedAt(LocalDateTime.now());
		user.setUpdatedAt(LocalDateTime.now());
		return userRepository.save(user); // save() returns the entity with its generated ID
	}

	// ── READ ALL ───────────────────────────────────────────────────────────
	public List<User> getAllUsers() {
		return userRepository.findAll(); // SELECT * FROM users
	}

	// ── READ ONE ───────────────────────────────────────────────────────────
	public Optional<User> getUserById(Integer userId) {
		return userRepository.findById(userId);
	}

	// ── UPDATE ─────────────────────────────────────────────────────────────
	// Load the existing user first so we preserve fields we are NOT updating ]
	// (e.g., createdAt). Then overwrite only the fields that changed.
	public User updateUser(Integer userId, User updatedUser) {
		Optional<User> existingUser = userRepository.findById(userId);
		if (existingUser.isPresent()) {
			User user = existingUser.get();
			user.setFirstName(updatedUser.getFirstName());
			user.setLastName(updatedUser.getLastName());
			user.setUserNameString(updatedUser.getUserNameString());
			user.setUpdatedAt(LocalDateTime.now()); // stamp the update time
			return userRepository.save(user); // save() on existing entity = UPDATE
		} else {
			throw notFound(userId);
		}
	}

	private static ResponseStatusException notFound(Integer userId) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId);
	}

	// ── DELETE ─────────────────────────────────────────────────────────────
	public void deleteUser(Integer userId) {
		if (userRepository.existsById(userId)) {
			userRepository.deleteById(userId);
		} else {
			throw notFound(userId);
		}
	}

}
