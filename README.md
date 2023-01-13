# PAS
Project for Lodz University of Technology Web Applications Basics course.
### :construction_worker: Contributors
|Name |Github|
|-|-|
|Rafał|[rstrzalkowski](https://github.com/rstrzalkowski)|
|Łukasz|[Lukasz0104](https://github.com/Lukasz0104)|
|Kamil|[St0n3k](https://github.com/St0n3k)|
### 🛠 Technologies, languages and tools
- Java (JDK-17)
- Maven
- Jakarta EE
- Payara Application Server
- Spring Boot
- PostgreSQL
- Hibernate ORM
- Java Server Faces
- Angular
- Rest-assured
- JWT
- Bootstrap
- Capacitor
### Description
#### General description
Guesthouse is a web app intended for managing a guesthouse. 
It allows guesthouse employees to manage rooms and rents. 
Clients can rent a chosen room for a specific period of time that isn't yet taken.
Administrators can manage the same things as employees, but they can also manage other users.
#### Description for each directory
- `Guesthouse-REST` - Jakarta EE REST app with JWT based security deployed on Payara Application Server. Uses Hibernate with PostgreSQL.
- `Guesthouse-SPRINGBOOT` - Implementation of `Guesthouse-REST` app without security converted to Spring Boot application.
- `Guesthouse-API-TEST` - Integration tests for `Guesthouse-SPRINGBOOT` app made with Rest-assured.
- `Guesthouse-MVC` - Java Server Faces web client app for `Guesthouse-REST`. Styled with Bootstrap. Uses security based on JWT authentication from REST API.
- `Guesthouse-MODEL` - Maven module with shared model classes for `Guesthouse-REST`, `Guesthouse-SPRINGBOOT`, `Guesthouse-MVC`, `Guesthouse-API-TESTS`.
- `Guesthouse-SPA` - web client app for `Guesthouse-REST` made with Angular. Uses security based on JWT authentication from REST API. Has a responsive web design made with Bootstrap. App at the moment is made only for clients of the guesthouse.
- `Guesthouse-MOBILE` - mobile client app  for `Guesthouse-REST`. Made from `Guesthouse-SPA` app using Capacitor.
