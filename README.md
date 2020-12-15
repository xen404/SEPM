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



# SEPM Group Phase

## First Steps

Navigate to the root folder of the project and execute `npm install`. Based on the *package.json* file, npm will download all required node_modules to run a Angular application.
Afterwards, execute `npm install -g @angular/cli` to install the Angular CLI globally.

## Development

### Development server

Run `ng serve` to start the web application. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

### Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

### Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

### Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

