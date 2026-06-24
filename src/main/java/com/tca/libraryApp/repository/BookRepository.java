package com.tca.libraryApp.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tca.libraryApp.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
	Optional<Book> findById(Integer bookId);

}
