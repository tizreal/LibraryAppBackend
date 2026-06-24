package com.tca.libraryApp.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.persistence.*;

//@Entity tells JPA: 'this Java class represents a database table'. 
//@Table(name = 'users') maps it to the exact table we created in PostgreSQL.
@Entity
@Table(name = "users")
public class User implements Serializable {

	// serialVersionUID is required by Serializable — it is a version stamp.
	private static final long serialVersionUID = 1L;

	// @Id marks this field as the primary key.
	// @GeneratedValue(IDENTITY) delegates ID generation to PostgreSQL (maps to
	// SERIAL).
	// @Column(name = 'user_id') links this Java field to the database column.

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	// nullable = false enforces NOT NULL at the application level before any SQL
	// runs.
	// length = 50 matches the VARCHAR(50) we defined in the SQL CREATE TABLE.
	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	@Column(name = "user_name", nullable = false, length = 50)
	private String userNameString;

	// updatable = false means Hibernate will never include created_at in an UPDATE.
	// It is written once on INSERT and never touched again.
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt = LocalDateTime.now();

	// JPA REQUIRES a no-argument constructor.
	// Hibernate uses it when reading rows back from the database into Java objects.
	public User() {
	}

	// Convenience constructor for creating users in code.
	public User(String firstName, String lastName, String userNameString) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.userNameString= userNameString;
	}

	public String getUserNameString() {
		return userNameString;
	}

	public void setUserNameString(String userNameString) {
		this.userNameString = userNameString;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	// toString() gives a readable description of the object — useful in logs.
	@Override
	public String toString() {
		return "User [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", userNameString="
				+ userNameString + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

	// equals() and hashCode() let Java correctly compare User objects.
	// Two User objects are equal if they share the same userId.
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(userId, other.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId);
	}

	/*
	 * @Entity — registers this class with JPA as a mapped database entity.
	 * 
	 * @Table(name = 'users') — links this class to the 'users' table in PostgreSQL.
	 * 
	 * @Id — marks userId as the primary key of this table.
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY) — PostgreSQL
	 * auto-increments the ID (maps to SERIAL). You never set this manually.
	 * 
	 * @Column(nullable = false) — enforces NOT NULL at the Java layer before the
	 * SQL even runs.
	 * 
	 * @Column(updatable = false) on createdAt — Hibernate will never include this
	 * column in UPDATE statements. Written once; never changed. implements
	 * Serializable — required for JPA entities that might be serialised (e.g.
	 * cached). The serialVersionUID is a required version stamp.
	 */

}
