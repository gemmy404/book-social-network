package com.book.network.controller;

import com.book.network.dto.FeedbackRequest;
import com.book.network.dto.FeedbackResponse;
import com.book.network.dto.PageResponse;
import com.book.network.service.FeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(@RequestBody FeedbackRequest request,
                                                Authentication connectedUser) {
        return ResponseEntity.ok(feedbackService.save(request, connectedUser));
    }

    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            Authentication connectedUser
            ) {
        return ResponseEntity.ok(feedbackService.findAllFeedbacksByBook(bookId, page, size, connectedUser));
    }

}
