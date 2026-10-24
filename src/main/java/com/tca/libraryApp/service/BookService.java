package com.tca.libraryApp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tca.libraryApp.entity.Book;
import com.tca.libraryApp.repository.BookRepository;

@Service
public class BookService {

	private final BookRepository bookRepository;

	@Autowired
	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	// CREATE — books have no timestamps, so we save directly.
	public Book createBook(Book book) {
		return bookRepository.save(book);
	}

	// READ ALL
	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	// READ ONE
	public Optional<Book> getBookById(Integer bookId) {
		return bookRepository.findById(bookId);
	}

	// UPDATE — load first, patch changed fields, save.
	public Book updateBook(Integer bookId, Book updatedBook) {
		Optional<Book> existingBook = bookRepository.findById(bookId);
		if (existingBook.isPresent()) {
			Book book = existingBook.get();
			book.setTitle(updatedBook.getTitle());
			book.setAuthor(updatedBook.getAuthor());
			book.setIsbn(updatedBook.getIsbn());
			book.setPublicationYear(updatedBook.getPublicationYear());
			return bookRepository.save(book);
		} else {
			throw notFound(bookId);
		}
	}

	private static ResponseStatusException notFound(Integer bookId) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with ID: " + bookId);
	}

	// DELETE
	public void deleteBook(Integer bookId) {
		if (bookRepository.existsById(bookId)) {
			bookRepository.deleteById(bookId);
		} else {
			throw notFound(bookId);
		}
	}

	/*
	 * @Service — registers this class with Spring's IoC container. Spring creates
	 * one shared instance (a 'bean') automatically.
	 * 
	 * @Autowired constructor injection — rather than creating the repository with
	 * 'new', we declare it as a dependency and Spring injects an instance. This is
	 * Dependency Injection, a core Spring principle.
	 * 
	 * Update pattern — we load the existing record first, copy only the changed
	 * fields, then save. This preserves fields we are not updating (e.g. createdAt)
	 * instead of wiping them.
	 * 
	 * A missing id answers 404 Not Found (ResponseStatusException).
	 */

}
