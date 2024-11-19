package com.book.network.service.impl;

import com.book.network.dto.BookRequest;
import com.book.network.dto.BookResponse;
import com.book.network.dto.BorrowedBookResponse;
import com.book.network.dto.PageResponse;
import com.book.network.entity.Book;
import com.book.network.entity.BookTransactionHistory;
import com.book.network.entity.Notification;
import com.book.network.entity.User;
import com.book.network.enums.NotificationStatus;
import com.book.network.exception.OperationNotPermittedException;
import com.book.network.mapper.BookMapper;
import com.book.network.repository.BookRepo;
import com.book.network.repository.TransactionHistoryRepo;
import com.book.network.service.BookService;
import com.book.network.service.NotificationService;
import com.book.network.util.FileStorageUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.book.network.enums.NotificationStatus.*;
import static com.book.network.repository.specification.BookSpecification.exceptOwnerId;
import static com.book.network.repository.specification.BookSpecification.withOwnerId;
import static com.book.network.repository.specification.TransactionHisSpecification.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;
    private final TransactionHistoryRepo historyRepo;
    private final BookMapper bookMapper;
    private final NotificationService notificationService;
    private final FileStorageUtil fileStorageUtil;

    @Override
    public Integer save(BookRequest request, Authentication connectedUser) {
//        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(request);
        book.setArchived(false);
//        book.setOwner(user);
        return bookRepo.save(book).getId();
    }

    @Override
    public Integer update(BookRequest request, Authentication connectedUser) {
        if (request.id() == null) {
            throw new OperationNotPermittedException("Which book you want to update?!");
        }
        Book book = bookRepo.findById(request.id()).orElseThrow(() -> new
                EntityNotFoundException("Book not found with id: " + request.id()));
//        User user = (User) connectedUser.getPrincipal();
        if (!book.getCreatedBy().equals(connectedUser.getName())) {
            throw new OperationNotPermittedException("Can't update book if you are not owner it!");
        }
        Book updatedBook = bookMapper.toBook(request);
        updatedBook.setBookCover(book.getBookCover());
        updatedBook.setArchived(false);

        return bookRepo.save(updatedBook).getId();
    }

    @Override
    public BookResponse findById(Integer bookId) {
        Optional<Book> book = bookRepo.findById(bookId);
        return book.map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + bookId));
    }

    @Override
    public PageResponse<BookResponse> findAllBooks(Integer page, Integer size, Authentication connectedUser) {
//        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "createdDate"));
        Page<Book> books = bookRepo.findAll(exceptOwnerId(connectedUser.getName()), pageable);
        return bookMapper.toBookResponsePageResponse(books);
    }

    @Override
    public PageResponse<BookResponse> findAllBooksByOwner(Integer page, Integer size, Authentication connectedUser) {
//        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "createdDate"));
        Page<Book> books = bookRepo.findAll(withOwnerId(connectedUser.getName()), pageable);
        return bookMapper.toBookResponsePageResponse(books);
    }

    @Override
    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(Integer page, Integer size, Authentication connectedUser) {
//        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "createdDate"));
        Page<BookTransactionHistory> allBorrowedBooks = historyRepo.findAll(borrowedBooks(connectedUser.getName()), pageable);
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    @Override
    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(Integer page, Integer size, Authentication connectedUser) {
//        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "createdDate"));
        Page<BookTransactionHistory> allReturnedBooks = historyRepo.findAll(returnedBooks(connectedUser.getName()), pageable);
        List<BorrowedBookResponse> bookResponses = allReturnedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allReturnedBooks.getNumber(),
                allReturnedBooks.getSize(),
                allReturnedBooks.getTotalElements(),
                allReturnedBooks.getTotalPages(),
                allReturnedBooks.isFirst(),
                allReturnedBooks.isLast()
        );
    }

    @Override
    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepo.findById(bookId).orElseThrow(() ->
                new EntityNotFoundException("No book found with ID: " + bookId));
//        User user = (User) connectedUser.getPrincipal();
        if (!book.getCreatedBy().equals(connectedUser.getName())) {
            throw new OperationNotPermittedException("You can't update others books shareable status.");
        }
        book.setShareable(!book.isShareable());
        bookRepo.save(book);
        return bookId;
    }

    @Override
    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepo.findById(bookId).orElseThrow(() ->
                new EntityNotFoundException("No book found with ID: " + bookId));
//        User user = (User) connectedUser.getPrincipal();
        if (!book.getCreatedBy().equals(connectedUser.getName())) {
            throw new OperationNotPermittedException("You can't update others books archived status.");
        }
        book.setArchived(!book.isArchived());
        bookRepo.save(book);
        return bookId;
    }

    @Override
    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new
                EntityNotFoundException("No book found with ID: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You can't borrow books that aren't sharable.");
        }
//        User user = (User) connectedUser.getPrincipal();
        if (book.getCreatedBy().equals(connectedUser.getName())) {
            throw new OperationNotPermittedException("You can't borrow you own books.");
        }
        final boolean isAlreadyBorrowed = historyRepo.exists(isBorrowed(bookId, connectedUser.getName()));
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("You can't borrow the book, it has already been borrowed" +
                    " or you have already borrowed the book before.");
        }
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .userId(connectedUser.getName())
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        BookTransactionHistory saved = historyRepo.save(bookTransactionHistory);
        Notification notification = Notification.builder()
                .status(BORROWED)
                .message("Your book has been borrowed")
                .bookTitle(book.getTitle())
                .build();
        notificationService.sendNotification(book.getCreatedBy(), notification);
        return saved.getId();
    }

    @Override
    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new
                EntityNotFoundException("No book found with ID: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You can't borrow books that aren't sharable.");
        }
//        User user = (User) connectedUser.getPrincipal();
        if (book.getCreatedBy().equals(connectedUser.getName())) {
            throw new OperationNotPermittedException("You can't borrow or return you own books.");
        }
        BookTransactionHistory bookTransactionHistory = historyRepo.findOne(byBookIdAndUserId(bookId, connectedUser.getName()))
                .orElseThrow(() -> new OperationNotPermittedException("You didn't borrow this book."));
        bookTransactionHistory.setReturned(true);
        BookTransactionHistory saved = historyRepo.save(bookTransactionHistory);
        Notification notification = Notification.builder()
                .status(RETURNED)
                .message("Your book has been returned")
                .bookTitle(book.getTitle())
                .build();
        notificationService.sendNotification(book.getCreatedBy(), notification);
        return saved.getId();
    }

    @Override
    public Integer approveReturnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new
                EntityNotFoundException("No book found with ID: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You can't borrow books that aren't sharable.");
        }
//        User user = (User) connectedUser.getPrincipal();
        if (!book.getCreatedBy().equals(connectedUser.getName())) {
            throw new OperationNotPermittedException("You cannot approve the return of a book you do not own");
        }
        BookTransactionHistory bookTransactionHistory = historyRepo.findOne(byBookIdAndOwnerId(bookId, book.getCreatedBy()))
                .orElseThrow(() -> new OperationNotPermittedException("The book isn't returned yet." +
                        " You can't approve its return."));
        bookTransactionHistory.setReturnApproved(true);
        BookTransactionHistory saved = historyRepo.save(bookTransactionHistory);
        Notification notification = Notification.builder()
                .status(RETURNED_APPROVED)
                .message("Your book returned has been approved")
                .bookTitle(book.getTitle())
                .build();
        notificationService.sendNotification(bookTransactionHistory.getCreatedBy(), notification);
        return saved.getId();
    }

    @Override
    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new
                EntityNotFoundException("No book found with ID: " + bookId));
//        User user = (User) connectedUser.getPrincipal();
        if (!book.getCreatedBy().equals(connectedUser.getName())) {
            throw new OperationNotPermittedException("You can't upload cover images for books other than your own.");
        }
        String bookCover = fileStorageUtil.saveFile(file, connectedUser.getName());
        book.setBookCover(bookCover);
        bookRepo.save(book);
    }

}
