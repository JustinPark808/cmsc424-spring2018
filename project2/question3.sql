-- ******************** PART 1 ******************** 
CREATE OR REPLACE FUNCTION function1() RETURNS VOID AS $$

    DECLARE
        rc          record;
        prev_tcount integer;

    BEGIN
        -- Create finaltab table
        DROP TABLE IF EXISTS finaltab;
        CREATE TABLE finaltab(transid int, tcount int);

        -- Initialize prev_tcount
        prev_tcount = 0;

        -- For each record in inittab, add transid and the sum of the previous
        -- record's tcount and the current record's tcount to finaltab
        FOR rc IN
            SELECT * FROM inittab
        LOOP
            -- Insert current record's transid and computed tcount to finaltab
            INSERT INTO finaltab
                VALUES (rc.transid, rc.tcount + prev_tcount);
            
            -- Update prev_tcount
            prev_tcount = rc.tcount;
        END LOOP;

    END;

$$ LANGUAGE plpgsql;


-- ******************** PART 2 ********************
CREATE OR REPLACE FUNCTION function2() RETURNS VOID AS $$
    
    DECLARE
        rc1         record;
        rc2         record;
        tcount      int;
        curr_row    int;
        curr_row2   int;
        lower_row   int;

    BEGIN
        -- Create finaltab2 table
        DROP TABLE IF EXISTS finaltab2;
        CREATE TABLE finaltab2(transid int, tcount int);
        
        -- Initialize curr_row for loop
        curr_row = 1;

        -- For each record in inittab, insert transid and the sum of the
        -- tcounts from max(1, row_number - transid) to row_number into
        -- finaltab2
        FOR rc1 IN
            SELECT * FROM inittab
        LOOP
            -- Initialize tcount, curr_row2, and lower_row
            tcount = 0;
            curr_row2 = 1;
            lower_row = GREATEST(1, curr_row - rc1.transid);

            -- For each record from lower_row to row_number, add the record's
            -- tcount to tcount
            FOR rc2 IN
                SELECT * FROM inittab
            LOOP
                -- Only include rows between lower_row and curr_row
                IF (curr_row2 >= lower_row) THEN
                    tcount = tcount + rc2.tcount;
                END IF;

                -- Increment curr_row2 and exit when curr_row2 > curr_row
                curr_row2 = curr_row2 + 1;
                EXIT WHEN curr_row2 > curr_row;
            END LOOP;

            -- Insert current record's transid and calculated tcount to
            -- finaltab2
            INSERT INTO finaltab2
                VALUES  (rc1.transid, tcount);

            -- Increment curr_row
            curr_row = curr_row + 1;
        END LOOP;

    END;

$$ LANGUAGE plpgsql;
