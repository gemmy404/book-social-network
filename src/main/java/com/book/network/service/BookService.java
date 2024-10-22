package com.book.network.service;

import com.book.network.dto.BookRequest;
import com.book.network.dto.BookResponse;
import com.book.network.dto.BorrowedBookResponse;
import com.book.network.dto.PageResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {

    Integer save(BookRequest request, Authentication connectedUser);

    Integer update(BookRequest request, Authentication connectedUser);

    BookResponse findById(Integer bookId);

    PageResponse<BookResponse> findAllBooks(Integer page, Integer size, Authentication connectedUser);

    PageResponse<BookResponse> findAllBooksByOwner(Integer page, Integer size, Authentication connectedUser);

    PageResponse<BorrowedBookResponse> findAllBorrowedBooks(Integer page, Integer size, Authentication connectedUser);

    PageResponse<BorrowedBookResponse> findAllReturnedBooks(Integer page, Integer size, Authentication connectedUser);

    Integer updateShareableStatus(Integer bookId, Authentication connectedUser);

    Integer updateArchivedStatus(Integer bookId, Authentication connectedUser);

    Integer borrowBook(Integer bookId, Authentication connectedUser);

    Integer returnBorrowedBook(Integer bookId, Authentication connectedUser);

    Integer approveReturnBorrowBook(Integer bookId, Authentication connectedUser);

    void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId);

}
