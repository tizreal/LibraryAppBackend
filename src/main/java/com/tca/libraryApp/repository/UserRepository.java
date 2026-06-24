package com.tca.libraryApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tca.libraryApp.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	// JpaRepository<EntityClass, PrimaryKeyType> provides these for free:
	// save(entity) — INSERT or UPDATE
	// findAll() — SELECT * FROM users
	// findById(id) — SELECT WHERE user_id = ?
	// existsById(id) — SELECT EXISTS ...
	// deleteById(id) — DELETE WHERE user_id = ?
	// Optional<User> forces the caller to handle the 'user not found' case.
	// This prevents NullPointerExceptions — a common Java bug.
	Optional<User> findById(Integer userId);

}
