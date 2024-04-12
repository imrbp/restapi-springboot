CREATE TABLE users(
    username VARCHAR(100) not null unique PRIMARY KEY ,
    password VARCHAR(200) not null ,
    name VARCHAR(100) not null,
    token VARCHAR(100) ,
    token_expired_at BIGINT
);

CREATE TABLE contacts(
    id VARCHAR(100) not null primary key,
    username VARCHAR(100) not null ,
    first_name VARCHAR(100) not null,
    last_name VARCHAR(100),
    phone VARCHAR(100),
    email VARCHAR(100),
    CONSTRAINT fk_users_contact FOREIGN KEY (username) REFERENCES users (username)
);

CREATE TABLE addresses(
    id VARCHAR(100) not null  primary key ,
    contact_id VARCHAR(100) not null,
    country VARCHAR(100) not null,
    province VARCHAR(100) not null,
    city VARCHAR(100) not null,
    street VARCHAR(100),
    postal_code VARCHAR(100),
    CONSTRAINT fk_contact_addresses FOREIGN KEY (contact_id) REFERENCES contacts (id)
)
