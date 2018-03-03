queries = ["" for i in range(0, 11)]

### 1.
queries[1] = """
SELECT DISTINCT 
    flights.flightid
FROM
    flights
RIGHT OUTER JOIN
    flewon
ON
    flights.flightid NOT IN (
        SELECT
            flightid
        FROM
            flewon
        WHERE
            flightdate = date '2016-08-05'
    )
;
"""

