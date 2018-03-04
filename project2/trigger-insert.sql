DELETE FROM ticketsales WHERE TRUE;
DELETE FROM airlinesales WHERE TRUE;
DELETE FROM reportmin WHERE TRUE;

INSERT INTO ticketsales VALUES ('T1', 'AA101', 'cust0', date '2016-08-09');
INSERT INTO ticketsales VALUES ('T2', 'AA101', 'cust1', date '2016-08-08');
INSERT INTO ticketsales VALUES ('T3', 'AA101', 'cust2', date '2016-08-10');
INSERT INTO ticketsales VALUES ('T4', 'UA101', 'cust0', date '2016-08-08');
INSERT INTO ticketsales VALUES ('T5', 'UA101', 'cust1', date '2016-08-10');
INSERT INTO ticketsales VALUES ('T6', 'SW102', 'cust2', date '2016-08-10');
INSERT INTO ticketsales VALUES ('T7', 'SW102', 'cust0', date '2016-08-08');
INSERT INTO ticketsales VALUES ('T8', 'SW102', 'cust1', date '2016-08-07');
INSERT INTO ticketsales VALUES ('T9', 'UA101', 'cust2', date '2016-08-07');

DELETE FROM ticketsales WHERE ticketid = 'T9';
DELETE FROM ticketsales WHERE ticketid = 'T5';
DELETE FROM ticketsales WHERE ticketid = 'T8';
DELETE FROM ticketsales WHERE ticketid = 'T7';

INSERT INTO ticketsales VALUES ('T7', 'SW102', 'cust0', date '2016-08-08');
INSERT INTO ticketsales VALUES ('T5', 'UA101', 'cust1', date '2016-08-10');
INSERT INTO ticketsales VALUES ('T9', 'UA101', 'cust2', date '2016-08-07');
INSERT INTO ticketsales VALUES ('T8', 'SW102', 'cust1', date '2016-08-07');

DELETE FROM ticketsales WHERE ticketid = 'T3';

SELECT * FROM ticketsales;
SELECT * FROM airlinesales;
SELECT * FROM reportmin;

-- ****** reportmin ******
-- airlineid | salesdate  
-------------+------------
-- AA        | 2016-08-09
-- UA        | 2016-08-08
-- SW        | 2016-08-10
-- UA        | 2016-08-07
-- SW        | 2016-08-08
-- UA        | 2016-08-10
-- SW        | 2016-08-07
-- AA        | 2016-08-10
--(8 rows)
