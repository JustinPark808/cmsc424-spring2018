DELETE FROM ticketsales WHERE TRUE;
DELETE FROM airlinesales WHERE TRUE;
DELETE FROM reportmin WHERE TRUE;

INSERT INTO ticketsales VALUES ('T1', 'AA101', 'cust0', date '2016-08-09');
INSERT INTO ticketsales VALUES ('T2', 'AA101', 'cust0', date '2016-08-10');
INSERT INTO ticketsales VALUES ('T3', 'UA101', 'cust2', date '2016-08-08');
INSERT INTO ticketsales VALUES ('T4', 'SW102', 'cust1', date '2016-08-08');
INSERT INTO ticketsales VALUES ('T5', 'UA101', 'cust1', date '2016-08-09');

DELETE FROM ticketsales WHERE ticketid = 'T2';

SELECT * FROM ticketsales;
SELECT * FROM airlinesales;
SELECT * FROM reportmin;

-- ****** reportmin ******
-- airlineid | salesdate  
-------------+------------
-- AA        | 2016-08-09
-- UA        | 2016-08-08
-- SW        | 2016-08-08
-- AA        | 2016-08-10
--(4 rows)

