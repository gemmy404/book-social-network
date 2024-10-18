package com.book.network.repository;

import com.book.network.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepo extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

//    @Query("SELECT book FROM Book book WHERE book.archived = false " +
//            "AND book.shareable = true AND book.owner.id != :userId")
//    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);

}
