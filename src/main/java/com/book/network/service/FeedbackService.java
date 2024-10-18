package com.book.network.service;

import com.book.network.dto.FeedbackRequest;
import com.book.network.dto.FeedbackResponse;
import com.book.network.dto.PageResponse;
import org.springframework.security.core.Authentication;

public interface FeedbackService {

    Integer save(FeedbackRequest request, Authentication connectedUser);

    PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, Integer page, Integer size, Authentication connectedUser);

}
