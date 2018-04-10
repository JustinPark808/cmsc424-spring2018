/* Create match table */
DROP TABLE IF EXISTS match;
CREATE TABLE match (
    id1             INTEGER     references person(id),
    id2             INTEGER     references person(id),
    date_of_match   DATE,
    rating          NUMERIC(5, 2),

    PRIMARY KEY     (id1, id2)
);
