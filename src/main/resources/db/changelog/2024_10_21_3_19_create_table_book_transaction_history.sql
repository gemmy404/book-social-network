CREATE TABLE book_transaction_history
(
    id                 INT AUTO_INCREMENT
        PRIMARY KEY,
    created_by         INT NOT NULL,
    created_date       DATETIME(6) NOT NULL,
    last_modified_by   INT null,
    last_modified_date datetime(6) null,
    return_approved    BIT NOT NULL,
    returned           BIT NOT NULL,
    book_id            INT NULL,
    user_id            INT NULL,
    CONSTRAINT FKetks95hi6ay47e16sj6vdv9g9
        FOREIGN KEY (book_id) REFERENCES book (id),
    CONSTRAINT FKh081geal7xoydl9vyh7cbf4wc
        FOREIGN KEY (user_id) REFERENCES _user (id)
);

