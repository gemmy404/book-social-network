package com.book.network.repository.specification;

import com.book.network.entity.Feedback;
import org.springframework.data.jpa.domain.Specification;

public class FeedbackSpecification {

    public static Specification<Feedback> withBookId(Integer bookId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("book").get("id"), bookId);
    }

}
