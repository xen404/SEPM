# Backend for SEPM Group Phase

## How to run it


### Install postgreSQL Database

---
**Windows**

https://www.enterprisedb.com/downloads/postgres-postgresql-downloads

_Start database:_
Open SQL Shell (psql)

---
**Linux**

`sudo apt-get install postgresql`

_Start database:_
`sudo psql`

#####Setup

Port: 5432
Username: postgres
Password: password

---

### Start the backed
`mvn spring-boot:run`

### Start the backed with test data
If the database is not clean, the test data won't be inserted

`mvn spring-boot:run -Dspring-boot.run.profiles=generateData`
