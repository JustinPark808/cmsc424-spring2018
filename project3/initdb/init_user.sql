/* Create new user and grant all permissions to access tables */
DROP USER IF EXISTS matchmaker;
CREATE USER matchmaker WITH PASSWORD 'kingofthenorth';
GRANT ALL ON person TO matchmaker;
GRANT ALL ON person_id_seq TO matchmaker;
GRANT ALL ON match TO matchmaker;
