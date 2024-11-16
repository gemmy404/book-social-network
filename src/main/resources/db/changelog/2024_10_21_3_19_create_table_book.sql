CREATE TABLE book
(
    id                 INT AUTO_INCREMENT
        PRIMARY KEY,
    created_by         VARCHAR(255) NOT NULL ,
    created_date       DATETIME(6)  NOT NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date DATETIME(6)  NULL,
    archived           BIT NOT NULL ,
    author_name        VARCHAR(255) NOT NULL,
    book_cover         VARCHAR(255) NULL,
    isbn               VARCHAR(255) NOT NULL,
    shareable          BIT NOT NULL ,
    synopsis           VARCHAR(255) NOT NULL,
    title              VARCHAR(255) NOT NULL
--     owner_id           INT NOT NULL,
--     CONSTRAINT FK61m8am98w4y4vgpl82sojy8bh
--         FOREIGN KEY (owner_id) REFERENCES _user (id)
);