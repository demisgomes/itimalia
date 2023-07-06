# itimalia
Backend application for an animal shelter

<p align="center">
<img src="https://img.shields.io/github/actions/workflow/status/demisgomes/itimalia/build.yml?branch=master">
<img src="https://img.shields.io/github/deployments/demisgomes/itimalia/web">
<img src="https://img.shields.io/codecov/c/github/demisgomes/itimalia">
</p>

## Topics

- [Introduction](#introduction)
- [Features](#features)
- [Stack](#stack)
- [Usage](#usage)
- [Application Deployed](#application-deployed)
- [API Documentation](#api-documentation)

### Introduction

Itimalia is a personal project that aims to build a backend application with the best practices to study about them. The motivation came from close friends that help animals in shelters. The stack not follows well-known Spring Boot due to need to study new technologies and know backend techniques more in-depth.

### Features

#### User

- Create a user in database
- Grant/remove admin permissions
- Modify a user (admin can modify any user)
- Delete an user (admin can delete any user)
- Get a user by id

#### Animal
- Add an animal in shelter by an admin (cat or dog)
- Modify the status of the animal (only admin) (gone, dead, adopted)
- Remove an animal from shelter (only admin)
- Search an animal and filter by name, sex, age, if is castrated, status
- Adopt an animal (only user)
- Add photos to animal (default max: 8)

#### Technical

- Unit tests with a minimum coverage of 90% (excluding some files)
- Integration tests (WIP)
- Lint check
- Database migration
- Filter animals by name, status, specie, size, sex, or castrated
- Paging animals search
- Order by id, name, or modification date, giving option to order ascending or descending
- Github actions to check build, lint and coverage
- Automatic deploy after successful build in master on Heroku
- Dependency Injection
- Hash passwords


### Stack

- [Javalin](https://javalin.io/) - Web Framework
- [Exposed](https://github.com/JetBrains/Exposed) - ORM
- [Swagger](https://swagger.io/) - Documentation
- [Koin](https://insert-koin.io/) - Dependency Injection
- [Mockk](https://mockk.io/) - Mocking Library
- [Cloudinary](https://cloudinary.com/) - Images Library
- [ktlint](https://ktlint.github.io/) - Lint
- [kover](https://github.com/Kotlin/kotlinx-kover) - Coverage tests
- [Test Containers](https://testcontainers.com/) - Containers for integration tests

### Usage

#### Run locally
Run docker compose

`docker-compose up`

or install a PostgreSQL application in your machine with `DATABASE_USERNAME=user` and `DATABASE_PASSWORD=password`

Load environment variables:

```
JDBC_URL=jdbc:postgresql://localhost:5432/database
DATABASE_USERNAME=user
DATABASE_PASSWORD=password
```

This would run the application without support to save animal images. To save images, make a Cloudinary Account and add your API credentials:

```
CLOUDINARY_API_KEY=<api_key>
CLOUDINARY_API_SECRET=<api_secret>
CLOUDINARY_CLOUD_NAME=<cloud_name>
```

Run the application

`./gradlew run`

#### Tests

To run all tests, including integration:

`./gradlew test`

To only run unit tests:

`./gradlew unitTest`

To run integration tests:

`./gradlew integrationTest`

NOTE: integration tests need a docker environment.

#### Lint and coverage

If you want to check lint, run `./gradlew ktlintCheck`

You can format your code to agree with ktlint rules with `./gradlew ktlintFormat`

To generate coverage reports, run `./gradlew koverMergedReport`
You should see a message like `Kover: merged HTML report file:///<your_file_location>/itimalia/build/reports/kover/html/index.html
`

The application has a default admin user with the following fields:
```json
{
    "id": 1,
    "email": "admin@itimalia.org",
    "birthDate": "Datetime actual date",
    "gender": "undefined",
    "name": "Admin",
    "phone": "8199999999",
    "role": "admin",
    "creationDate": "Datetime actual date"
}
```

### Application deployed
The application was deployed in Northflank and can be accessed in https://site--web--b59kk4m4p49y.code.run/
On the image below, we can see the current architecture. Itimalia has a connection with PostgreSQL database and both them are hosted on Northflank (image will be updated). Itimalia saves the image in Cloudinary that compress images and generate a link for access them.

![Itimalia architecture on Heroku](https://user-images.githubusercontent.com/13547352/181644604-a9a91b06-1b24-4b70-a954-4e7c4aae519b.png)


### API documentation

- Local:
    http://localhost:7000/swagger

- Northflank:
  https://site--web--b59kk4m4p49y.code.run/swagger

You also can see Postman collections in this repository in folder `/postman`.
