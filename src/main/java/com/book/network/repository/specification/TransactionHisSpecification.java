package com.book.network.repository.specification;

import com.book.network.entity.BookTransactionHistory;
import org.springframework.data.jpa.domain.Specification;

public class TransactionHisSpecification {

    public static Specification<BookTransactionHistory> borrowedBooks(String userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("userId"), userId);
    }

    public static Specification<BookTransactionHistory> returnedBooks(String userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                criteriaBuilder.equal(root.get("book").get("createdBy"), userId),
                criteriaBuilder.isTrue(root.get("returned"))
                );
    }

    public static Specification<BookTransactionHistory> isBorrowed(Integer bookId, String userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("book").get("id"), bookId),
                        criteriaBuilder.or(
                                criteriaBuilder.equal(root.get("userId"), userId),
                                criteriaBuilder.isFalse(root.get("returnApproved"))
                        )
                );
    }

    public static Specification<BookTransactionHistory> byBookIdAndUserId(Integer bookId, String userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("book").get("id"), bookId),
                        criteriaBuilder.equal(root.get("userId"), userId),
                        criteriaBuilder.isFalse(root.get("returned")),
                        criteriaBuilder.isFalse(root.get("returnApproved"))
                );
    }


    public static Specification<BookTransactionHistory> byBookIdAndOwnerId(Integer bookId, String ownerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("book").get("id"), bookId),
                        criteriaBuilder.equal(root.get("book").get("createdBy"), ownerId),
                        criteriaBuilder.isTrue(root.get("returned")),
                        criteriaBuilder.isFalse(root.get("returnApproved"))
                );
    }

}
