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
    birthdate >= date '1990-01-01' 
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
SELECT
    DISTINCT(customerid),
    name,
    birthdate,
    frequentflieron
FROM
    customers
NATURAL JOIN
    flewon
WHERE
    to_date(concat(2016, ' ', extract(month from birthdate), ' ',
    extract(day from birthdate)), 'YYYY MM DD')
BETWEEN
    flightdate + interval '1 day'
AND
    flightdate + interval '1 week'
ORDER BY
    name
;
"""

### 3. Write a query to find number of inbound flights by each airline to any airport 
### Output: (airport_city, airline, inbound_flights) 
### Order: first by airport_city increasingly, then inbound_flights decreasingly, then airline increasingly.
### Note: You must generate the airport city names instead of airport codes.
queries[3] = """
SELECT
    city AS airport_city,
    airlineid AS airline_id,
    count(*) AS inbound_flights
FROM
    airports,
    flights
WHERE
    airports.airportid = flights.dest
GROUP BY
    city,
    airline_id
ORDER BY
    city ASC,
    inbound_flights DESC,
    airline_id ASC
;
"""

### 4. Find the name of the customer who flew the most times with his/her frequent flier airline. For example, if customer X flew Delta (which is listed as X's frequent flier airline in the customers table) 100 times, and no other customer flew their frequent flyer airline more than 99 times, the only thing returned for this query is X's name.
### Hint: use `with clause` and nested queries 
### Output: only the name of the customer. If multiple answers, return them all.
### Order: order by name.
queries[4] = """
WITH
    customer_flight_count_by_airline AS (
        SELECT
            customerid,
            substring(flightid from 1 for 2) AS airline_id,
            count(*) AS flight_count
        FROM
            flewon
        GROUP BY
            customerid,
            airline_id
),  customer_frequentflier_flight_count AS (
        SELECT
            b.name,
            a.flight_count
        FROM
            customer_flight_count_by_airline AS a,
            customers AS b
        WHERE
            (a.customerid, a.airline_id) = (b.customerid, b.frequentflieron)
)
SELECT
    name
FROM
    customer_frequentflier_flight_count AS a
WHERE
    flight_count = (
        SELECT
            max(flight_count)
        FROM
            customer_frequentflier_flight_count
    )
ORDER BY
    name
;
"""

### 5. Find all 1-stop flights from JFK to LAX having layover duration greater than or equal to 1 hour. 
### Output: (1st flight id, 2nd flight id, connection airport id, layover duration).
### Order: by the duration hours.
queries[5] = """
SELECT
    a.flightid AS flight1,
    b.flightid AS flight2,
    b.source AS connection_airport,
    b.local_departing_time - a.local_arrival_time AS duration
FROM
    flights AS a,
    flights AS b
WHERE
    (a.source, b.dest, a.dest) = ('JFK', 'LAX', b.source)
AND
    b.local_departing_time - a.local_arrival_time >= interval '1 hour'
ORDER BY
    duration
;
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
    flewon_plf_0801 AS (
        SELECT
            flightid,
            round(count(*) / 120.0, 2) AS plf
        FROM
            flewon
        WHERE
            flightdate = DATE '2016-08-01'
        GROUP BY
            flightid
    ),
    empty_flights_0801 AS (
        SELECT
            flightid,
            round(0.0, 2) AS plf
        FROM
            flights
        WHERE
            flightid NOT IN (
                SELECT
                    flightid
                FROM
                    flewon
                WHERE
                    flightdate = DATE '2016-08-01'
            )
    )
SELECT
    flightid,
    plf
FROM
    (
        SELECT
            *
        FROM
            empty_flights_0801
        UNION
        SELECT
            *
        FROM
            flewon_plf_0801
        WHERE
            plf <= 0.01
    ) AS combined_plf_0801
ORDER BY
    plf DESC,
    flightid
;
"""

### 7. Write a query to find the customers who used their frequent flier airline the least when compared to all the airlines that this customer as flown on. For example, if customer X has Delta as X's frequent flyer airline in the customer table, but flew on Delta only 1 time, and every other airline at least 1 time, then X's id and name would be returned as part of this query.
### Output: (customerid, customer_name) 
### Order: by customerid
### Note: a customer may have never flown on their frequent flier airlines.
queries[7] = """
WITH
    customer_flight_counts_init AS (
        SELECT
            customerid,
            airlineid,
            0 AS flight_count
        FROM
            customers,
            airlines
    ),
    customer_existing_flight_counts AS (
        SELECT
            customerid,
            airlineid,
            count(*) AS flight_count
        FROM
            flewon
        NATURAL JOIN
            flights
        GROUP BY
            customerid,
            airlineid
    ),
    customer_flight_counts AS (
        SELECT
            customerid,
            airlineid,
            sum(flight_count) AS flight_count
        FROM
            (
            SELECT
                *
            FROM
                customer_flight_counts_init
            UNION
            SELECT
                *
            FROM
                customer_existing_flight_counts
            ) AS customer_flight_counts_combined
        GROUP BY
            customerid,
            airlineid
    )
SELECT
    customerid,
    name
FROM
    customer_flight_counts AS c1
NATURAL JOIN
    customers
WHERE
    airlineid = frequentflieron
AND
    flight_count = (
        SELECT
            min(flight_count)
        FROM
            customer_flight_counts AS c2
        WHERE
            c1.customerid = c2.customerid
    )
ORDER BY
    customerid
;
"""

### 8. Write a query to find the flights which are empty on three consecutive days, but not empty on the other days. Return the flight, and the start and end dates of those three days.  
### Hint: postgres window functions may be useful
### Output: flightid, start_date, end_date 
### Order: by start_date, then flightid 
queries[8] = """
WITH
    empty_flights AS (
        SELECT
            flightid,
            flightdate
        FROM
            flights,
            (
                SELECT DISTINCT
                    flightdate
                FROM
                    flewon
            ) AS all_dates
        EXCEPT
        SELECT
            flightid,
            flightdate
        FROM
            flewon
    ),
    empty_for_3_flights AS (
        SELECT
            *
        FROM
            empty_flights
        WHERE
            flightid IN (
                SELECT
                    flightid
                FROM
                    empty_flights
                GROUP BY
                    flightid
                HAVING
                    count(*) = 3
            )
    )
SELECT
    flightid,
    min(flightdate) AS start_date,
    max(flightdate) AS end_date
FROM
    empty_for_3_flights
GROUP BY
    flightid
HAVING
    max(flightdate) - min(flightdate) = 2
ORDER BY
    flightid
;
"""

### 9. Write a query to find the city name(s) which have the strongest connection with OAK. We define "strongest connection" as the city with the total number of customers who took a flight that departs from that city to fly to OAK, or arrives at the city from OAK.  
### Output columns: city name
### Order by: city name
### Note: a) You can assume there is only one airport in a city.
###       b) If there are ties, return all tied cities 
queries[9] = """
WITH
    oak_connections AS (
        SELECT
            airportid,
            sum(strength) AS strength
        FROM
            (
                SELECT
                    dest AS airportid,
                    count(*) AS strength
                FROM
                    flights
                NATURAL JOIN
                    flewon
                WHERE
                    source = 'OAK'
                GROUP BY
                    dest
                UNION ALL
                SELECT
                    source AS airportid,
                    count(*) AS strength
                FROM
                    flights
                NATURAL JOIN
                    flewon
                WHERE
                    dest = 'OAK'
                GROUP BY
                    source
            ) AS oak_connections
        GROUP BY
            airportid
    )
SELECT
    city
FROM
    oak_connections
NATURAL JOIN
    airports
WHERE
    strength = (
        SELECT
            max(strength)
        FROM
            oak_connections
    )
ORDER BY
    city
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
    flight_customers_per_day AS (
        SELECT
            flightid,
            flightdate,
            count(customerid) AS onboard_count
        FROM
            flewon
        GROUP BY
            flightid,
            flightdate
    ),
    flight_avg_customers AS (
        SELECT
            flightid,
            sum(onboard_count) / (
                SELECT
                    max(flightdate) - min(flightdate) + 1.0
                FROM
                    flewon
            ) AS onboard_avg
        FROM
            flight_customers_per_day
        GROUP BY
            flightid
    ),
    ranked_flights AS (
        SELECT
            flightid,
            onboard_avg,
            (
                SELECT
                    count(*)
                FROM
                    flight_avg_customers AS b
                WHERE
                    b.onboard_avg > a.onboard_avg
            ) + 1 AS flight_rank
        FROM
            flight_avg_customers AS a
    )
SELECT
    flightid,
    flight_rank
FROM
    ranked_flights
WHERE
    flight_rank <= 20
ORDER BY
    flight_rank,
    flightid
;
"""
