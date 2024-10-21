CREATE TABLE _user
(
    id                 INT AUTO_INCREMENT
        PRIMARY KEY,
    account_locked     BIT NOT NULL,
    created_date       DATETIME(6) NOT NULL,
    date_of_birth      DATE NULL,
    email              VARCHAR(255) NULL,
    enabled            BIT NOT NULL,
    first_name         VARCHAR(255) NOT NULL ,
    last_modified_date DATETIME(6)  NULL,
    last_name          VARCHAR(255) NOT NULL,
    password           VARCHAR(255) NOT NULL ,
    CONSTRAINT UKk11y3pdtsrjgy8w9b6q4bjwrx
        UNIQUE (email)
);

