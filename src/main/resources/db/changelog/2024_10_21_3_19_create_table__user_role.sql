CREATE TABLE _user_roles
(
    users_id INT NOT NULL ,
    roles_id int NOT NULL ,
    CONSTRAINT FKkna43mk14wb08rt62w1982ki6
        FOREIGN KEY (users_id) REFERENCES _user (id),
    CONSTRAINT FKtq7v0vo9kka3qeaw2alou2j8p
        FOREIGN KEY (roles_id) REFERENCES role (id)
);