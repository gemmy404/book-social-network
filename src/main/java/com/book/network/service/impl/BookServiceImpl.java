package com.book.network.service.impl;

import com.book.network.dto.BookRequest;
import com.book.network.dto.BookResponse;
import com.book.network.dto.BorrowedBookResponse;
import com.book.network.dto.PageResponse;
import com.book.network.entity.Book;
import com.book.network.entity.BookTransactionHistory;
import com.book.network.entity.User;
import com.book.network.exception.OperationNotPermittedException;
import com.book.network.mapper.BookMapper;
import com.book.network.repository.BookRepo;
import com.book.network.repository.TransactionHistoryRepo;
import com.book.network.service.BookService;
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

import static com.book.network.repository.specification.BookSpecification.exceptOwnerId;
import static com.book.network.repository.specification.BookSpecification.withOwnerId;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;
    private final TransactionHistoryRepo historyRepo;
    private final BookMapper bookMapper;
    private final FileStorageUtil fileStorageUtil;

    @Override
    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        System.out.println("User ID: " + user.getId());
        Book book = bookMapper.toBook(request);
        book.setArchived(false);
        book.setOwner(user);
        return bookRepo.save(book).getId();
    }

    @Override
    public BookResponse findById(Integer bookId) {
        Optional<Book> book = bookRepo.findById(bookId);
        return book.map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + bookId));
    }

    @Override
    public PageResponse<BookResponse> findAllBooks(Integer page, Integer size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "createdDate"));
        Page<Book> books = bookRepo.findAll(exceptOwnerId(user.getId()), pageable);
        return bookMapper.toBookResponsePageResponse(books);
//        List<BookResponse> bookResponses = books.stream()
//                .map(bookMapper::toBookResponse)
//                .toList();
//        return new PageResponse<>(
//                bookResponses,
//                books.getNumber(),
//                books.getSize(),
//                books.getTotalElements(),
//                books.getTotalPages(),
//                books.isFirst(),
//                books.isLast()
//        );
    }

    @Override
    public PageResponse<BookResponse> findAllBooksByOwner(Integer page, Integer size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "createdDate"));
        Page<Book> books = bookRepo.findAll(withOwnerId(user.getId()), pageable);
        return bookMapper.toBookResponsePageResponse(books);
//        List<BookResponse> bookResponses = books.stream()
//                .map(bookMapper::toBookResponse)
//                .toList();
//        return new PageResponse<>(
//                bookResponses,
//                books.getNumber(),
//                books.getSize(),
//                books.getTotalElements(),
//                books.getTotalPages(),
//                books.isFirst(),
//                books.isLast()
//        );
    }

    @Override
    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(Integer page, Integer size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "createdDate"));
        Page<BookTransactionHistory> allBorrowedBooks = historyRepo.findAllBorrowedBooks(pageable, user.getId());
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
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "createdDate"));
        Page<BookTransactionHistory> allReturnedBooks = historyRepo.findAllReturnedBooks(pageable, user.getId());
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
        User user = (User) connectedUser.getPrincipal();
        if (!book.getOwner().getId().equals(user.getId())) {
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
        User user = (User) connectedUser.getPrincipal();
        if (!book.getOwner().getId().equals(user.getId())) {
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
        User user = (User) connectedUser.getPrincipal();
        if (book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You can't borrow you own books.");
        }
        final boolean isAlreadyBorrowed = historyRepo.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("You can't borrow the book, it has already been borrowed" +
                    " or you have already borrowed the book before.");
        }
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();

        return historyRepo.save(bookTransactionHistory).getId();
    }

    @Override
    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new
                EntityNotFoundException("No book found with ID: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You can't borrow books that aren't sharable.");
        }
        User user = (User) connectedUser.getPrincipal();
        if (book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You can't borrow or return you own books.");
        }
        BookTransactionHistory bookTransactionHistory = historyRepo.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You didn't borrow this book."));
        bookTransactionHistory.setReturned(true);
        return historyRepo.save(bookTransactionHistory).getId();
    }

    @Override
    public Integer approveReturnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new
                EntityNotFoundException("No book found with ID: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You can't borrow books that aren't sharable.");
        }
        User user = (User) connectedUser.getPrincipal();
        if (!book.getCreatedBy().equals(user.getId())) {
            throw new OperationNotPermittedException("You cannot approve the return of a book you do not own");
        }
        BookTransactionHistory bookTransactionHistory = historyRepo.findByBookIdAndOwnerId(bookId, book.getOwner().getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book isn't returned yet." +
                        " You can't approve its return."));
        bookTransactionHistory.setReturnApproved(true);
        return historyRepo.save(bookTransactionHistory).getId();
    }

    @Override
    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new
                EntityNotFoundException("No book found with ID: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You can't upload cover images for books other than your own.");
        }
        String bookCover = fileStorageUtil.saveFile(file, user.getId());
        book.setBookCover(bookCover);
        bookRepo.save(book);
    }

}
