# itimalia
Backend application for an animal shelter

### Operations

#### User
- Create an user in database
- Grant/remove admin permissions
- Modify an user (admin can modify any user)
- Delete an user (admin can delete any user)
- Get an user by id

#### Animal
- Add an animal in shelter by an admin (cat or dog)
- Modify the status of the animal (only admin) (gone, dead, adopted)
- Remove an animal from shelter (only admin)
- Search an animal by name, status, or specie
- Adopt an animal (only user)
- Add photos to animal (default max: 8)

### Stack

- Javalin
- Exposed
- Hikari
- Jackson
- Swagger
- Koin
- Mockk (tests)
- H2 (tests)
- Cloudinary (images)
- ktlint
- BCrypt (hash passwords)

### Usage

Run docker compose (or install postgres in your machine)

`docker-compose up`

Run the application

`./gradle run`

If you want to check lint, run `./gradle ktlintCheck`.

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
The application was deployed in Heroku and can be accessed in https://afternoon-caverns-61373.herokuapp.com/

### API documentation

- Local:
    http://localhost:7000/swagger

- Heroku Example:
    https://afternoon-caverns-61373.herokuapp.com/swagger
