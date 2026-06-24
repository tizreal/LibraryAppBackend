package com.tca.libraryApp.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tca.libraryApp.entity.Book;
import com.tca.libraryApp.service.BookService;

//@CrossOrigin allows a browser-based frontend (e.g. React on localhost:5173) 
// to call this API without being blocked by CORS security policy. 
// Without this annotation, the browser will refuse to show the API response. 
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
		RequestMethod.PUT, RequestMethod.DELETE })

@RestController
@RequestMapping("/api/books")
public class BookController {

	private final BookService bookService;

	@Autowired
	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	// POST /api/books
	@PostMapping
	public ResponseEntity<Book> createBook(@RequestBody Book book) {
		return ResponseEntity.ok(bookService.createBook(book));
	}

	// GET /api/books
	@GetMapping
	public ResponseEntity<List<Book>> getAllBooks() {
		return ResponseEntity.ok(bookService.getAllBooks());
	}

	// GET /api/books/{bookId}
	@GetMapping("/{bookId}")
	public ResponseEntity<Book> getBookById(@PathVariable Integer bookId) {
		Optional<Book> book = bookService.getBookById(bookId);
		return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// PUT /api/books/{bookId}
	@PutMapping("/{bookId}")
	public ResponseEntity<Book> updateBook(@PathVariable Integer bookId, @RequestBody Book book) {
		return ResponseEntity.ok(bookService.updateBook(bookId, book));
	}

	// DELETE /api/books/{bookId}
	@DeleteMapping("/{bookId}")
	public ResponseEntity<Void> deleteBook(@PathVariable Integer bookId) {
		bookService.deleteBook(bookId);
		return ResponseEntity.noContent().build();
	}

	/*
	 * @RestController — combines @Controller and @ResponseBody. Every return value
	 * is automatically serialised to JSON and written into the HTTP response body.
	 * 
	 * @RequestMapping('/api/users') — all methods in this class share this base URL
	 * path.
	 * 
	 * @PostMapping / @GetMapping / @PutMapping / @DeleteMapping — map a method to a
	 * specific HTTP verb.
	 * 
	 * @PathVariable — extracts a value from the URL path, e.g. /api/users/5 →
	 * userId = 5.
	 * 
	 * @RequestBody — Spring reads the JSON request body and deserialises it into
	 * the User/Book object. ResponseEntity<T> — wraps the response with an HTTP
	 * status code. ResponseEntity.ok() = 200. notFound().build() = 404.
	 * noContent().build() = 204.
	 * 
	 * @CrossOrigin on BookController — allows a browser-based React frontend
	 * (running on a different port) to call this API without being blocked by the
	 * browser's CORS security policy.
	 */

}
