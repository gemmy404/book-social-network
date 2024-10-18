package com.book.network.repository.specification;

import com.book.network.entity.Book;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }

    public static Specification<Book> exceptOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.notEqual(root.get("owner").get("id"), ownerId),
                        criteriaBuilder.isFalse(root.get("archived")),
                        criteriaBuilder.isTrue(root.get("shareable"))
                );
    }

    public static Specification<Book> borrowedBooks(Integer userId) {
        return (root, query, criteriaBuilder) -> {
            Join<Object, Object> transactionHistoryJoin = root.join("transactionHistories");
            return criteriaBuilder.equal(transactionHistoryJoin.get("user").get("id"), userId);
        };
    }

}
