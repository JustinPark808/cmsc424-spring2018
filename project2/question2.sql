create view customer_flight as
select distinct c.customerid as cid, flightid
from customers c, flewon f
where c.customerid = f.customerid and extract(year from birthdate) >= 1996
order by cid, flightid;
	
create view flight_ORD as
select flightid from flights
where source = 'ORD' or dest = 'ORD'
order by flightid;

WITH
    temp AS (
        SELECT DISTINCT
            cid,
            v2.flightid AS afid
        FROM
            customer_flight AS v1
        LEFT OUTER JOIN
            flight_ORD AS v2
        ON
            v1.flightid = v2.flightid
        ORDER BY
            cid)
SELECT
    cid
FROM
    temp
GROUP BY
    cid
HAVING
    count(*)=1
AND
    exists(
        SELECT
            afid
        FROM
            temp
        WHERE
            afid IS NULL)
ORDER BY
    cid;
