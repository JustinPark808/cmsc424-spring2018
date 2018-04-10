#!/bin/bash
path="initdb/"
psql matchapp -qf initdb/init_person_table.sql
psql matchapp -qf initdb/init_match_table.sql
psql matchapp -qf initdb/init_user.sql
psql matchapp -qf initdb/person.sql
