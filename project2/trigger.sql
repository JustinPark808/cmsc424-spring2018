-- Create table airline_with_min_sales to keep track of which airlineid
-- has minimum total_ticket_sales
DROP TABLE IF EXISTS airline_with_min_sales;
CREATE TABLE airline_with_min_sales (airlineid char(2));
INSERT INTO airline_with_min_sales VALUES ('  ');


-- Processes the insertion or deletion of a ticket sale. Appropriately
-- increments or decrements total_ticket_sales in airlinesales for the
-- airlineid listed on the ticket. If the airline has the new minimum
-- total_ticket_sales as a result, add the airlineid and ticket salesdate to
-- reportmin.
CREATE OR REPLACE FUNCTION process_ticket_sale() RETURNS TRIGGER AS $$
    
    DECLARE
        ticket_airlineid        char(2);
        ticket_salesdate        date;
        airline_total_sales     integer;
        min_total_sales         integer;
        airline_with_min_sales  char(2);
    
    BEGIN
        -- INSERT trigger
        IF (TG_OP = 'INSERT') THEN
            -- Set ticket's airlineid
            ticket_airlineid = substring(NEW.flightid from 1 for 2);
            ticket_salesdate = NEW.salesdate;

            -- If the ticket's airlineid exists in airlinesales, increment
            -- total_ticket_sales by 1. Else, add ticket_airlineid with a
            -- total_ticket_sales of 1 to airlinesales
            IF (ticket_airlineid IN (SELECT airlineid from airlinesales)) THEN
                UPDATE  airlinesales
                    SET     total_ticket_sales = total_ticket_sales + 1
                    WHERE   ticket_airlineid = airlineid;
            ELSE
                INSERT INTO airlinesales (airlineid,total_ticket_sales)
                    VALUES  (ticket_airlineid, 1);
            END IF;

        -- DELETE trigger
        ELSIF (TG_OP = 'DELETE') THEN
            -- Set ticket's airlineid
            ticket_airlineid = substring(OLD.flightid from 1 for 2);
            ticket_salesdate = OLD.salesdate;

            -- Decrement total_ticket_sales by 1
            UPDATE  airlinesales
                SET     total_ticket_sales = total_ticket_sales - 1
                WHERE   ticket_airlineid = airlineid;

        END IF;

        -- Set the total_ticket_sales for ticket's airlineid, the new minimum
        -- total_ticket_sales, and the airlineid with current minimum 
        -- total_ticket_sales
        airline_total_sales =   (SELECT total_ticket_sales FROM airlinesales
                                    WHERE ticket_airlineid = airlineid);
        min_total_sales = (SELECT min(total_ticket_sales) FROM airlinesales);
        airline_with_min_sales = (SELECT airlineid FROM airline_with_min_sales);

        -- If incrementing total_ticket_sales causes the airline to now hold
        -- minimum total_ticket_sales and the airline is different from the 
        -- previous airline with minimum total_ticket_sales, update
        -- airline_with_min_sales and add the airlineid and the ticket
        -- salesdate to reportmin
        IF (airline_total_sales = min_total_sales AND
            ticket_airlineid != airline_with_min_sales) THEN
            UPDATE  airline_with_min_sales
                SET airlineid = ticket_airlineid;
            INSERT INTO reportmin
                VALUES  (ticket_airlineid, ticket_salesdate);
        END IF;

        RETURN NULL;
    END;

$$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS process_ticket_sale ON ticketsales;
CREATE TRIGGER process_ticket_sale
AFTER INSERT OR DELETE ON ticketsales
    FOR EACH ROW EXECUTE PROCEDURE process_ticket_sale();
