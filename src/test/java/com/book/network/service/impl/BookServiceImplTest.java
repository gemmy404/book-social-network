package com.book.network.service.impl;

import com.book.network.dto.BookRequest;
import com.book.network.entity.Book;
import com.book.network.entity.BookTransactionHistory;
import com.book.network.entity.User;
import com.book.network.mapper.BookMapper;
import com.book.network.repository.BookRepo;
import com.book.network.repository.TransactionHistoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static com.book.network.repository.specification.TransactionHisSpecification.isBorrowed;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepo bookRepo;
    @Mock
    private TransactionHistoryRepo historyRepo;
    @Mock
    private BookMapper bookMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_successfully_create_a_book() {

        Authentication authentication = Mockito.mock(Authentication.class);

        BookRequest request = new BookRequest(
                null,
                "Spring Security in Action",
                "Laurentiu Spilca",
                "978-1633437975",
                "Spring Security in Action, Second Edition.",
                true
        );

        Book book = Book.builder()
                .title("Spring Security in Action")
                .authorName("Laurentiu Spilca")
                .isbn("978-1633437975")
                .synopsis("Spring Security in Action, Second Edition.")
                .shareable(true)
                .build();

        Book savedBook = Book.builder()
                .id(1)
                .title("Spring Security in Action")
                .authorName("Laurentiu Spilca")
                .isbn("978-1633437975")
                .synopsis("Spring Security in Action, Second Edition.")
                .shareable(true)
                .build();

        when(bookMapper.toBook(request)).thenReturn(book);
        when(bookRepo.save(book)).thenReturn(savedBook);

        Integer response = bookService.save(request, authentication);

        assertNotNull(response);
        assertEquals(savedBook.getId(), response);
        assertEquals(savedBook.getTitle(), book.getTitle());

    }

    @Test
    public void should_successfully_borrow_a_book() {
        Authentication authentication = Mockito.mock(Authentication.class);
        User user = User.builder().id(1).email("user1").build();
        when(authentication.getPrincipal()).thenReturn(user);
        Integer bookId = 1;
        Book book = Book.builder()
                .id(bookId)
                .title("Spring Security in Action")
                .authorName("Laurentiu Spilca")
                .isbn("978-1633437975")
                .synopsis("Spring Security in Action, Second Edition.")
                .shareable(true)
                .owner(User.builder().id(3).email("ownerUser").build())
                .build();

        BookTransactionHistory requestHis = BookTransactionHistory.builder()
//                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();

        BookTransactionHistory savedHis = BookTransactionHistory.builder()
                .id(1)
//                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();

        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
//        when(historyRepo.exists(isBorrowed(bookId, user.getId()))).thenReturn(false);
        when(historyRepo.save(any(BookTransactionHistory.class))).thenReturn(savedHis);

        Integer response = bookService.borrowBook(bookId, authentication);

        assertNotNull(response);
        assertEquals(savedHis.getId(), response);

    }

}