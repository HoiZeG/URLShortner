CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY
);

CREATE TABLE url (
    hash VARCHAR(6) PRIMARY KEY,
    url VARCHAR(255),
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT url_hash_fk
                 FOREIGN KEY (hash) REFERENCES hash(hash)
);

CREATE SEQUENCE unique_number_seq;
