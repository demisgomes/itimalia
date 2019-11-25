# itimalia
Backend application for an animal shelter

### Operations

- Create an user in database
- Grant/remove admin permissions
- Modify an user
- Delete an user
- Get an user by id
- Add an animal in shelter (cat or dog)
- Modify the status of the animal (gone, dead, adopted)
- Remove an animal in shelter
- Search an animal by name, status, or specie
- adopt an animal

### Stack

- Javalin
- Exposed
- Hikari
- H2 (tests)
- Jackson
- Swagger
- Koin
- Mockk (tests)

### Usage

`./gradle run`

### Application deployed
The application was deployed in Heroku and can be accessed in https://afternoon-caverns-61373.herokuapp.com/

### API documentation

- Local
    http://localhost:7000/swagger

- Heroku Example
    https://afternoon-caverns-61373.herokuapp.com/swagger
