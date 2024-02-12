# Implementation details

Ideally, the application should utilize a No-SQL database due to the absence of relational data. However, given that Postgres offers an atomic function for incrementing IDs, it has been selected for use in this application.

The IDs are generated incrementally, and the Base62 representation of each ID is then derived, from which a 7-character substring is extracted for use in creating the tiny URL.

Redis cache is used to improve the performance.

## Endpoints

Here is the Swagger documentation:

Swagger endpoint: http://localhost:8080/swagger-ui/index.html

## Tests

As many tests as possible have been tried to be put, however most important aspects of the code are being covered by the tests, and the coverage is not 100% due to time constraint.

Integration tests have the naming convention to end with `*IT.java` and unit tests have the naming convention to end with `*Test.java`.

## Starting the application

Initially, execute `./start-deps.sh` to initiate the dependencies. Subsequently, invoke `StartApplication` in the **test** package to launch the application with the `local` profile.

When running tests, ensure that the docker dependencies are active by executing `./start-deps.sh`.

To terminate the dependencies, use the command `./stop-deps.sh`.

## Tech Stack

- Spring boot 3.x
- Java 17
- Postgres
- Redis

## Future Development

- Sending metrics of failures/successes
- More test coverage
- Workflow diagram
- Use of a No-SQL database and an ID generator service which all instances can use to get the next available ID.


