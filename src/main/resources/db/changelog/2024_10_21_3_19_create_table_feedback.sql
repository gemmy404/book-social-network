CREATE TABLE feedback
(
    id                 INT AUTO_INCREMENT
        PRIMARY KEY,
    created_by         INT NOT NULL,
    created_date       DATETIME(6)  NOT NULL,
    last_modified_by   INT NULL,
    last_modified_date DATETIME(6)  NULL,
    comment            VARCHAR(255) NOT NULL ,
    note Double NOT NULL,
    book_id            INT NOT NULL ,
    CONSTRAINT FKgclyi456gw0lcd6xcfj2l7r6s
        FOREIGN KEY (book_id) REFERENCES book (id)
);

