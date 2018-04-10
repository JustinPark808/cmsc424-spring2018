/* Create person table */
DROP TABLE IF EXISTS person CASCADE;
CREATE TABLE person (
    id                          SERIAL          PRIMARY KEY,
    first_name                  VARCHAR(12),
    last_name                   VARCHAR(18),
    age                         INT             CHECK(age >= 18),
    major                       VARCHAR(20),
    gender                      INT             NOT NULL,
    seeking_relationship_type   INT             CHECK(seeking_relationship_type BETWEEN 1 AND 3),
    seeking_gender              INT             NOT NULL,
    language                    VARCHAR(3),
    county                      VARCHAR(20),
    approval_rating             NUMERIC(3, 2)   CHECK(approval_rating BETWEEN 0.00 AND 9.99)        DEFAULT 1.00,
    foo                         VARCHAR(20)
);
