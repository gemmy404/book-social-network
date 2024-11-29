package com.book.network.mapper;

import com.book.network.dto.BookRequest;
import com.book.network.dto.BookResponse;
import com.book.network.dto.BorrowedBookResponse;
import com.book.network.dto.PageResponse;
import com.book.network.entity.Book;
import com.book.network.entity.BookTransactionHistory;
import com.book.network.util.FileStorageUtil;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper
public interface BookMapper {

    Book toBook(BookRequest request);

    default BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
//                .owner(book.getOwner().fullName())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .cover(FileStorageUtil.readFileFromLocation(book.getBookCover()))
                .build();
    }

    PageResponse<BookResponse> toBookResponsePageResponse(Page<Book> books);

    default BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnedApproved(history.isReturnApproved())
                .build();
    }

}
