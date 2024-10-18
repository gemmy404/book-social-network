package com.book.network.repository;

import com.book.network.entity.BookTransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TransactionHistoryRepo extends JpaRepository<BookTransactionHistory, Integer> {

    @Query("SELECT history FROM BookTransactionHistory history WHERE history.user.id = :userId")
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

    @Query("SELECT history FROM BookTransactionHistory history WHERE history.book.owner.id = :userId")
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

    @Query("SELECT (COUNT(*) > 0) AS isBorrowed " +
            "FROM BookTransactionHistory bookTransactionHistory " +
            "WHERE bookTransactionHistory.book.id = :bookId " +
            "AND (bookTransactionHistory.returnApproved = false OR bookTransactionHistory.user.id = :userId)")
    boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);

    @Query("SELECT transaction FROM BookTransactionHistory transaction " +
            "WHERE transaction.book.id = :bookId " +
            "AND transaction.user.id = :userId " +
            "AND transaction.returned = false " +
            "AND transaction.returnApproved = false")
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);

    @Query("SELECT transaction FROM BookTransactionHistory transaction " +
            "WHERE transaction.book.id = :bookId " +
            "AND transaction.book.owner.id = :ownerId " +
            "AND transaction.returned = true " +
            "AND transaction.returnApproved = false")
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer ownerId);
}
