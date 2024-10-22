package com.book.network.repository;

import com.book.network.entity.BookTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionHistoryRepo extends JpaRepository<BookTransactionHistory, Integer>,
        JpaSpecificationExecutor<BookTransactionHistory> {

}
