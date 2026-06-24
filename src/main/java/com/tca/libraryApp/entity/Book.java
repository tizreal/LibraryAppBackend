package com.tca.libraryApp.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;

//@Entity + @Table links this class to the 'books' table in PostgreSQL. 
@Entity
@Table(name = "books")
public class Book implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_id")
	private Integer bookId;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(name = "author", nullable = false, length = 100)
	private String author;

	// unique = true mirrors the UNIQUE constraint we defined in SQL.
	// Hibernate enforces this at the entity level before sending the INSERT.
	@Column(name = "isbn", nullable = false, length = 13, unique = true)
	private String isbn;

	// No nullable = false here — publication_year is optional.
	@Column(name = "publication_year")
	private Integer publicationYear;

	public Book() {
		super();
	}

	public Book(Integer bookId, String title, String author, String isbn, Integer publicationYear) {
		super();
		this.bookId = bookId;
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		this.publicationYear = publicationYear;
	}

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Integer getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(Integer publicationYear) {
		this.publicationYear = publicationYear;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return Objects.equals(bookId, other.bookId);
	}

	@Override
	public String toString() {
		return "Book [bookId=" + bookId + ", title=" + title + ", author=" + author + ", isbn=" + isbn
				+ ", publicationYear=" + publicationYear + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(bookId);
	}

	/*
	 * Validate your entities Restart the Spring Boot application. Because
	 * ddl-auto=validate is set, Hibernate compares your entity classes to the live
	 * database schema on startup. If everything matches, you will see 'Started
	 * LibraryAppApplication'. If a column name or type is wrong, the Console will
	 * tell you exactly which field does not match.
	 */

}
