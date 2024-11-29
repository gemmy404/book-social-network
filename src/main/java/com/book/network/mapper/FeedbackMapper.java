package com.book.network.mapper;

import com.book.network.dto.FeedbackRequest;
import com.book.network.dto.FeedbackResponse;
import com.book.network.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface FeedbackMapper {

    @Mapping(target = "book.id", source = "bookId")
    Feedback toFeedback(FeedbackRequest feedBackRequest);

    default FeedbackResponse toFeedbackResponse(Feedback feedback, String userId) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(feedback.getCreatedBy().equals(userId))
                .build();
    }

}
