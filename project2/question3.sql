-- ******************** PART 1 ******************** 
CREATE OR REPLACE FUNCTION function1() RETURNS VOID AS $$
    DECLARE
        rc          record;
        prev_tcount int;

    BEGIN
        -- Create finaltab table
        DROP TABLE IF EXISTS finaltab;
        CREATE TABLE finaltab(transid int, tcount int);

        -- Initialize prev_tcount
        prev_tcount = 0;
    
        FOR rc IN SELECT * FROM inittab LOOP
            INSERT INTO finaltab VALUES (rc.transid, rc.tcount + prev_tcount);
            prev_tcount = rc.tcount;
        END LOOP;
    END;
$$ LANGUAGE plpgsql;

-- ******************** PART 2 ********************
CREATE OR REPLACE FUNCTION function2() RETURNS VOID AS $$
    DECLARE
        rc1     record;
        rc2     record;
        temp    int;
        transid int;
        tcount  int;
        counter int;
        diff    int;

    BEGIN
        -- Create finaltab2 table
        DROP TABLE IF EXISTS finaltab2;
        CREATE TABLE finaltab2(transid int, tcount int);
        
        -- Initialize values
        transid = NULL;
        tcount = 0;
        counter = 0;
        diff = 0;
    
        FOR rc1 IN SELECT * FROM inittab LOOP
            diff = counter - rc1.transid;
            
            IF transid IS NULL THEN
                tcount = rc1.tcount;
            ELSE
                tcount = rc1.tcount + tcount;
            END IF;

            IF diff > 0 THEN
                FOR rc2 IN SELECT * FROM inittab LOOP
                    IF diff > 0 THEN
                        tcount = tcount - rc2.tcount;
                        diff = diff - 1;
                    END IF;
                END LOOP;
            END IF;

            transid = rc1.transid;
            INSERT INTO finaltab2(transid, tcount) VALUES (transid, tcount);
            counter = counter + 1;
        END LOOP;
    END;
$$ LANGUAGE plpgsql;
