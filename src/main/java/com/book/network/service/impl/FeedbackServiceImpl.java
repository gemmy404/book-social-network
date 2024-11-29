package com.book.network.service.impl;

import com.book.network.dto.FeedbackRequest;
import com.book.network.dto.FeedbackResponse;
import com.book.network.dto.PageResponse;
import com.book.network.entity.Book;
import com.book.network.entity.Feedback;
import com.book.network.entity.User;
import com.book.network.exception.OperationNotPermittedException;
import com.book.network.mapper.FeedbackMapper;
import com.book.network.repository.BookRepo;
import com.book.network.repository.FeedbackRepo;
import com.book.network.service.FeedbackService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.book.network.repository.specification.FeedbackSpecification.withBookId;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepo feedbackRepo;
    private final BookRepo bookRepo;
    private final FeedbackMapper feedbackMapper;

    @Override
    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepo.findById(request.bookId()).orElseThrow(() -> new
                EntityNotFoundException("Book not found book with the ID: " + request.bookId()));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You can't give a feedback for an archived or not shareable book.");
        }
//        User user = (User) connectedUser.getPrincipal();
        if (book.getCreatedBy().equals(connectedUser.getName())) {
            throw new OperationNotPermittedException("You are not allowed to give a feedback to your own book.");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedbackRepo.save(feedback).getId();
    }

    @Override
    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, Integer page, Integer size,
                                                                 Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "createdDate"));
//        User user = (User) connectedUser.getPrincipal();
        Page<Feedback> feedbacks = feedbackRepo.findAll(withBookId(bookId), pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(feedback -> feedbackMapper.toFeedbackResponse(feedback, connectedUser.getName()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }

}
