CREATE TABLE current_min(airlineid varchar(2));

CREATE OR REPLACE FUNCTION triggered() RETURNS trigger AS $triggered$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- If the airlineid already exists in the table, increment total_ticket_sales
        IF substring(NEW.flightid from 1 for 2) IN (SELECT airlineid from airlinesales) THEN
            UPDATE airlinesales
            set total_ticket_sales = total_ticket_sales + 1
            WHERE substring(NEW.flightid from 1 for 2) = airlineid;
        -- If the airlineid does not exist in the table, add it to the tbale
        ELSE
            INSERT INTO airlinesales
            VALUES (substring(NEW.flightid from 1 for 2), 1);
        END IF;

        -- If incrementing the total_ticket_sales causes the airline to become
        -- the new airline with minimum total_ticket_sales, add the airlineid
        -- and the ticket salesdate to reportmin
        IF (SELECT total_ticket_sales FROM airlinesales WHERE substring(NEW.flightid from 1 for 2) = airlineid) = (SELECT min(total_ticket_sales) FROM airlinesales) THEN
            -- If the airlineid is not the same as the current airline id with
            -- minimum total_ticket_sales, update the current airline id and
            -- add the airlineid and the ticket salesdate to reportmin
            IF (SELECT airlineid FROM current_min) NOT LIKE substring(NEW.flightid from 1 for 2) THEN
                UPDATE current_min
                set airlineid = substring(NEW.flightid from 1 for 2);
                INSERT INTO reportmin
                VALUES (substring(NEW.flightid from 1 for 2), NEW.salesdate);
            END IF;
        END IF;

    END IF;

    IF TG_OP = 'DELETE' THEN
        -- Decrement the total_ticket_sales
        UPDATE airlinesales
        set total_ticket_sales = total_ticket_sales - 1
        WHERE airlineid = substring(OLD.flightid from 1 for 2);

        -- If decrementing the total_ticket_sales causes the airline to become
        -- the new airline with minimum total_ticket_sales, add the airlineid
        -- and the ticket salesdate to reportmin
        IF (SELECT total_ticket_sales FROM airlinesales WHERE substring(NEW.flightid from 1 for 2) = airlineid) = (SELECT min(total_ticket_sales) FROM airlinesales) THEN
            -- If the airline is not the same as the current airline id with
            -- minimum total_ticket_sales, update the current airline id and
            -- add the airlineid and the ticket salesdate to reportmin
            IF (SELECT airlineid FROM current_min) NOT LIKE substring(OLD.flightid from 1 for 2) THEN
                UPDATE current_min
                set airlineid = substring(OLD.flightid from 1 for 2);
                INSERT INTO reportmin
                VALUES ((SELECT airlineid FROM current_min), OLD.salesdate);
            END IF;
        END IF;

    END IF;

    RETURN NULL;
END;

$triggered$ LANGUAGE plpgsql;
