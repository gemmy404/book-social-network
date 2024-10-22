package com.book.network.repository;

import com.book.network.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeedbackRepo extends JpaRepository<Feedback, Integer>, JpaSpecificationExecutor<Feedback> {

}
