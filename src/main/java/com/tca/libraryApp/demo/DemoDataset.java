package com.tca.libraryApp.demo;

import java.util.List;

/** The sample data the public demo is reset to: real, well-known books and fictional members. */
public final class DemoDataset {

	public record SampleBook(String title, String author, String isbn, Integer publicationYear) {
	}

	public record SampleMember(String firstName, String lastName, String userName) {
	}

	public static final List<SampleBook> BOOKS = List.of(
			new SampleBook("To Kill a Mockingbird", "Harper Lee", "9780061120084", 1960),
			new SampleBook("Nineteen Eighty-Four", "George Orwell", "9780451524935", 1949),
			new SampleBook("Pride and Prejudice", "Jane Austen", "9780141439518", 1813),
			new SampleBook("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", 1925),
			new SampleBook("Moby-Dick", "Herman Melville", "9780142437247", 1851),
			new SampleBook("Jane Eyre", "Charlotte Brontë", "9780141441146", 1847),
			new SampleBook("The Hobbit", "J. R. R. Tolkien", "9780547928227", 1937),
			new SampleBook("Brave New World", "Aldous Huxley", "9780060850524", 1932),
			new SampleBook("Fahrenheit 451", "Ray Bradbury", "9781451673319", 1953),
			new SampleBook("The Catcher in the Rye", "J. D. Salinger", "9780316769488", 1951),
			new SampleBook("Wuthering Heights", "Emily Brontë", "9780141439556", 1847),
			new SampleBook("Frankenstein", "Mary Shelley", "9780486282114", 1818));

	public static final List<SampleMember> MEMBERS = List.of(
			new SampleMember("Maya", "Chen", "mchen"),
			new SampleMember("Daniel", "Okafor", "dokafor"),
			new SampleMember("Sofia", "Alvarez", "salvarez"),
			new SampleMember("Liam", "Novak", "lnovak"),
			new SampleMember("Priya", "Raman", "praman"),
			new SampleMember("Jonas", "Weber", "jweber"));

	private DemoDataset() {
	}
}
