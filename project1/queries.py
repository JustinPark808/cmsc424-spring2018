### Justin Park (112184933)
### CMSC424 Sec. 0201

queries = ["" for i in range(0, 11)]

### 0. List all airport codes and their cities. Order by the city name in increasing order. 
### Output column order: airportid, city

queries[0] = """
SELECT
    airportid,
    city 
FROM
    airports
ORDER BY
    city;
"""

### 1. Write a query to find the names of the customers who were born after 1990-01-01, and the family name starts with 'G'
### Hint: See postgresql date operators and string functions
### Order: by name
### Output columns: name 
queries[1] = """
SELECT
    name
FROM
    customers
WHERE
    birthdate > date '1990-01-01' 
AND
    name ~ ' G'
ORDER BY
    name
;
"""

### 2. Write a query to find unique customers who flew on the dates within one week before their birthday.
### Hint: See postgresql date functions and distinct operator
### Order: by name 
### Output columns: all columns from customers
queries[2] = """
WITH
    customers_flewon AS (
        SELECT
            *,
            age(flightdate + time '00:00', birthdate + time '00:00') AS flightage
        FROM
            customers
        NATURAL JOIN
            flewon
)
SELECT DISTINCT
    customerid,
    name,
    birthdate,
    frequentflieron
FROM
    customers_flewon
WHERE
    EXTRACT(MONTH FROM flightage) = 11
AND
    EXTRACT(DAY FROM flightage) >= 24
ORDER BY
    name
;
"""

### 3. Write a query to find number of inbound flights by each airline to any airport 
### Output: (airport_city, airline_id, inbound_flights) 
### Order: first by airport_city increasingly, then inbound_flights decreasingly, then airline_id increasingly.
### Note: You must generate the airport city names instead of airport codes.
queries[3] = """
WITH
    airports_flights AS (
        SELECT
            city, 
            airlineid, 
            flightid
        FROM
            airports
        INNER JOIN
            flights
        ON
            airports.airportid = flights.dest
)
SELECT
    city AS airport_city,
    airlineid AS airline_id,
    count(*) AS inbound_flights
FROM
    airports_flights
GROUP BY
    city,
    airlineid
ORDER BY
    city ASC,
    inbound_flights DESC,
    airlineid ASC
;
"""

### 4. Find the name of the customer who flew the most times with his/her frequent flier airline. For example, if customer X flew Delta (which is listed as X's frequent flier airline in the customers table) 100 times, and no other customer flew their frequent flyer airline more than 99 times, the only thing returned for this query is X's name.
### Hint: use `with clause` and nested queries 
### Output: only the name of the customer. If multiple answers, return them all.
### Order: order by name.
queries[4] = """
WITH
    customers_flewon_flights AS (
        WITH
            customers_flewon AS (
                SELECT
                    *
                FROM
                    customers
                NATURAL JOIN
                    flewon
        )
        SELECT
            *
        FROM
            customers_flewon
        NATURAL JOIN
            flights
),  frequentflier_count AS (
        SELECT
            name,
            count(*) AS count
        FROM
            customers_flewon_flights
        WHERE
            frequentflieron = airlineid
        GROUP BY
            name
),  max_count AS (
        SELECT
            max(count) AS max_count
        FROM
            frequentflier_count
)
SELECT
    name
FROM
    frequentflier_count, max_count
WHERE
    count = max_count;
"""

### 5. Find all 1-stop flights from JFK to LAX having layover duration greater than or equal to 1 hour. 
### Output: (1st flight id, 2nd flight id, connection airport id, layover duration).
### Order: by the duration hours.
queries[5] = """
WITH
    onestop_flights AS (
        SELECT
            A.flightid AS flightid1,
            B.flightid AS flightid2,
            A.dest AS connectionairportid,
            B.local_departing_time - A.local_arrival_time AS layoverduration
        FROM    
            flights as A,
            flights as B
        WHERE
            (A.dest, A.source, B.dest) = (B.source, 'JFK', 'LAX')
)
SELECT
    *
FROM
    onestop_flights
WHERE
    layoverduration >= interval '01:00:00'
ORDER BY
    layoverduration;
"""

### 6. Assuming each flight has 120 seats, from flewon, find all flights with passenger load factor (PLF) less than or equal to 1% on Aug 1st 2016. Note, PLF is defined as number of customers on-board divided by total number of available seats.  
### Output: (flightid, PLF). 
### Order: first by PLF descreasing order, then flightid 
### Note: a) Each flight flew daily between Aug1 and Aug9, 2016. 
###          There may be empty flights which are not in the flewon table (i.e. PLF=0). 
###          Please include those.
###       b) PLF should be rounded to 2 decimal places, e.g., 10% should be 0.10.
### Hint: SQL set operators union/except/intersect may be useful.
queries[6] = """
WITH
    flewon_all AS (
        (
            SELECT
                flightid,
                NULL AS customerid,
                NULL AS flightdate
            FROM
                flights
        ) UNION (
            SELECT
                *
            FROM
                flewon
            WHERE
                flightdate = date '2016-08-01'
        )
),  flight_loads AS (
        SELECT
            flightid,
            round(count(customerid) / 120.0, 2) AS PLF
        FROM
            flewon_all
        GROUP BY
            flightid
)
SELECT
    *
FROM
    flight_loads
WHERE
    PLF <= 0.01
ORDER BY
    PLF DESC,
    flightid ASC
;
"""

### 7. Write a query to find the customers who used their frequent flier airline the least when compared to all the airlines that this customer as flown on. For example, if customer X has Delta as X's frequent flyer airline in the customer table, but flew on Delta only 1 time, and every other airline at least 1 time, then X's id and name would be returned as part of this query.
### Output: (customerid, customer_name) 
### Order: by customerid
### Note: a customer may have never flown on their frequent flier airlines.
queries[7] = """
WITH
    customer_airlines AS (
        SELECT
            customerid,
            customers.name,
            frequentflieron,
            airlineid
        FROM
            customers,
            airlines
        GROUP BY
            customerid,
            customers.name,
            frequentflieron,
            airlineid
),  customer_flights AS (
        SELECT
            customerid,
            airlineid,
            flightid
        FROM
            flewon
        NATURAL INNER JOIN
            flights
),  customer_joined AS (
        SELECT
            customerid,
            airlineid,
            name,
            frequentflieron,
            count(flightid)
        FROM
            customer_airlines
        NATURAL LEFT OUTER JOIN
            customer_flights
        GROUP BY
            customerid,
            airlineid,
            name,
            frequentflieron
)
SELECT
    customerid,
    name
FROM
    customer_joined AS S
WHERE
    airlineid = frequentflieron
AND
    count <= ALL (
        SELECT
            count
        FROM
            customer_joined AS T
        WHERE
            S.customerid = T.customerid
)
ORDER BY
    customerid ASC
;
"""

### 8. Write a query to find the flights which are empty on three consecutive days, but not empty on the other days. Return the flight, and the start and end dates of those three days.  
### Hint: postgres window functions may be useful
### Output: flightid, start_date, end_date 
### Order: by start_date, then flightid 
queries[8] = """
WITH
    flightdates AS (
        SELECT
            flightid,
            flightdate
        FROM
            flewon
        GROUP BY
            flightid,
            flightdate
    ),
    flightdate_leads AS (
        SELECT
            flightid,
            flightdate,
            lead(flightdate) OVER (
                PARTITION BY
                    flightid
                ORDER BY
                    flightdate
            )
        FROM
            flightdates
    ),
    nonempty_date_counts AS (
        SELECT
            flightid,
            count(flightdate)
        FROM
            flightdates
        GROUP BY
            flightid
    )
SELECT
    flightid,
    flightdate + 1 AS start_date,
    lead - 1 AS end_date
FROM
    flightdate_leads
WHERE
    flightid IN (
        SELECT  flightid
        FROM    nonempty_date_counts
        WHERE   count = 6
    )
AND 
    lead - flightdate = 4
;
"""

### 9. Write a query to find the city name(s) which have the strongest connection with OAK. We define "strongest connection" as the city with the total number of customers who took a flight that departs from that city to fly to OAK, or arrives at the city from OAK.  
### Output columns: city name
### Order by: city name
### Note: a) You can assume there is only one airport in a city.
###       b) If there are ties, return all tied cities 
queries[9] = """
WITH
    oak_arrivals AS (
        SELECT
            flightid,
            source AS airportid,
            city
        FROM
            flights
        INNER JOIN
            airports
        ON
            source = airportid
        WHERE
            dest = 'OAK'
    ),
    oak_departures AS (
        SELECT
            flightid,
            dest AS airportid,
            city
        FROM
            flights
        INNER JOIN
            airports
        ON
            dest = airportid
        WHERE
            source = 'OAK'
    ),
    oak_flights AS (
        SELECT
            *
        FROM
            oak_arrivals
        UNION
        SELECT
            *
        FROM
            oak_departures
    ),
    flight_counts AS (
        SELECT
            city,
            count(flightid)
        FROM
            oak_flights
        GROUP BY
            city
    ),
    max_count AS (
        SELECT
            max(count)
        FROM
            flight_counts
    )
SELECT
    city
FROM
    flight_counts, max_count
WHERE
    count = max_count.max
ORDER BY
    city ASC
;
"""

### 10. Write a query that outputs the ranking of the top 20 busiest flights. We rank the flights by their average number of on-board customers, so the flight with the highest average number of customers gets rank 1, and so on. 
### Output: (flightid, flight_rank)
### Order: by the rank, then by flightid 
### Note: a) If two flights tie, then they should both get the same rank, and the next rank should be skipped. For example, if the top two flights have the same average number of customers, then there should be no rank 2, e.g., 1, 1, 3 ...   
###       b) There may be empty flights.
###       c) There may be tied flights at rank 20, if so, all flights ranked 20 need to be returned
queries[10] = """
WITH
    all_flewon AS (
        SELECT
            *
        FROM
            flewon
        UNION
        SELECT
            flightid,
            NULL AS customerid,
            NULL AS flightdate
        FROM
            flights
    ),
    averages AS (
        SELECT
            flightid,
            count(customerid) / 9.0 AS avg
        FROM
            all_flewon
        GROUP BY
            flightid
    ),
    ranks AS (
        SELECT
            flightid,
            rank() OVER (
                ORDER BY avg DESC
            )
        FROM
            averages
    )
    SELECT
        *
    FROM
        ranks
    WHERE
        rank <= 20
    ORDER BY
        rank ASC,
        flightid ASC
;
"""
