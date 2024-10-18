package com.book.network.repository;

import com.book.network.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepo extends JpaRepository<Feedback, Integer> {

    @Query("SELECT feedback FROM Feedback feedback WHERE feedback.book.id = :bookId")
    Page<Feedback> findAllFeedbacksByBook(Integer bookId, Pageable pageable);

}
