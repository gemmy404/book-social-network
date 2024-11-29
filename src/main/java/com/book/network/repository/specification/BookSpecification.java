package com.book.network.repository.specification;

import com.book.network.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> withOwnerId(String ownerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("createdBy"), ownerId);
    }

    public static Specification<Book> exceptOwnerId(String ownerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.notEqual(root.get("createdBy"), ownerId),
                        criteriaBuilder.isFalse(root.get("archived")),
                        criteriaBuilder.isTrue(root.get("shareable"))
                );
    }

}
