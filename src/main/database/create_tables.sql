CREATE TABLE customer
(
    id  SERIAL PRIMARY KEY,
    first_name   varchar(30) NOT NULL,
    last_name    varchar(30) NOT NULL
);

CREATE TABLE product
(
    id  SERIAL PRIMARY KEY,
    product_name  varchar(100) NOT NULL,
    product_price  numeric(8, 2) NOT NULL,
    customer_id  integer REFERENCES customer(id)
);