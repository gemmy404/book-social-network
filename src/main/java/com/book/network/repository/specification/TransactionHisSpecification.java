package com.book.network.repository.specification;

import com.book.network.entity.BookTransactionHistory;
import org.springframework.data.jpa.domain.Specification;

public class TransactionHisSpecification {

    public static Specification<BookTransactionHistory> borrowedBooks(Integer userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<BookTransactionHistory> returnedBooks(Integer userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("book").get("createdBy"), userId);
    }

    public static Specification<BookTransactionHistory> isBorrowed(Integer bookId, Integer userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("book").get("id"), bookId),
                        criteriaBuilder.or(
                                criteriaBuilder.equal(root.get("user").get("id"), userId),
                                criteriaBuilder.isFalse(root.get("returnApproved"))
                        )
                );
    }

    public static Specification<BookTransactionHistory> byBookIdAndUserId(Integer bookId, Integer userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("book").get("id"), bookId),
                        criteriaBuilder.equal(root.get("user").get("id"), userId),
                        criteriaBuilder.isFalse(root.get("returned")),
                        criteriaBuilder.isFalse(root.get("returnApproved"))
                );
    }


    public static Specification<BookTransactionHistory> byBookIdAndOwnerId(Integer bookId, Integer ownerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("book").get("id"), bookId),
                        criteriaBuilder.equal(root.get("book").get("owner").get("id"), ownerId),
                        criteriaBuilder.isTrue(root.get("returned")),
                        criteriaBuilder.isFalse(root.get("returnApproved"))
                );
    }

}
