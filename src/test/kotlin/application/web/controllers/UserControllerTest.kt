package application.web.controllers

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import domain.entities.*
import domain.exceptions.*
import domain.jwt.JWTAccessManager
import domain.services.UserService
import io.javalin.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import org.junit.Before
import org.junit.Test
import org.omg.CORBA.DynAnyPackage.Invalid
import java.text.DateFormat
import java.text.SimpleDateFormat
import kotlin.test.assertEquals

class UserControllerTest{
    lateinit var userServiceMock: UserService
    lateinit var contextMock: Context
    lateinit var returnedUser:UserDTO
    lateinit var returnedAdminUser:UserDTO
    lateinit var newUser:NewUser
    lateinit var newAdminUser:NewUser
    lateinit var newLoginUser: UserLogin
    lateinit var jwtAccessManagerMock: JWTAccessManager

    @Before
    fun setup(){
        userServiceMock = mockk(relaxed=true)
        contextMock = mockk(relaxed = true)
        jwtAccessManagerMock=mockk(relaxed = true)

        val formatter: DateFormat = SimpleDateFormat("dd/mm/yyyy")
        val date=formatter.parse("01/01/1990")
        returnedUser= UserDTO(
            null,
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.USER,
            null,
            null,
            null)

        returnedAdminUser= UserDTO(
            null,
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.ADMIN,
            null,
            null,
            null)

        newUser = NewUser(
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.USER
        )

        newAdminUser = NewUser(
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.ADMIN
        )

        newLoginUser = UserLogin(
            "newUser@domain.com",
            "password"
        )
    }

    @Test
    fun `when request a user with a valid id, return it`(){
        every{ userServiceMock.get(1)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        UserController(userServiceMock, jwtAccessManagerMock).findUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request a user with an invalid id, return not found`(){
        val userNotFoundException=UserNotFoundException()
        every{ userServiceMock.get(1)}.throws(userNotFoundException)

        every { contextMock.pathParam("id") }.returns("1")

        UserController(userServiceMock, jwtAccessManagerMock).findUser(contextMock)

        verify { contextMock.json(userNotFoundException.createErrorResponse()).status(HttpStatus.NOT_FOUND_404) }
    }

    @Test
    fun `when add a valid user should return the user with status 201`(){
        every{ userServiceMock.add(newUser)}.returns(returnedUser)

        every { contextMock.body<NewUser>() }.returns(newUser)

        UserController(userServiceMock, jwtAccessManagerMock).addUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when add a invalid user with less than 13 years old should return validation error with status 400`(){
        val validationException=ValidationException(hashMapOf("birthDate" to mutableListOf("Only accept users with 13 years old or more")))

        every{ userServiceMock.add(newUser)}.throws(validationException)

        every { contextMock.body<NewUser>() }.returns(newUser)

        UserController(userServiceMock, jwtAccessManagerMock).addUser(contextMock)

        verify { contextMock.json(validationException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }
    }


    @Test
    fun `when modify a valid user should return the user modified with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when modify a valid user but without modifications should return an unmodified exception with status 406`(){
        val unmodifiedUserException=UnmodifiedUserException()
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)



        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email)}.throws(unmodifiedUserException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(unmodifiedUserException.createErrorResponse()).status(HttpStatus.NOT_ACCEPTABLE_406) }
    }

    @Test
    fun `when modify a valid user but with a non-existent id should return an user not found exception with status 404`(){
        val userNotFoundException=UserNotFoundException()

        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)


        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email)}.throws(userNotFoundException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(userNotFoundException.createErrorResponse()).status(HttpStatus.NOT_FOUND_404) }
    }


    @Test
    fun `when modify a invalid user that exists should return a validation exception with status 400`(){
        val validationException=ValidationException(hashMapOf("birthDate" to mutableListOf("Only accept users with 13 years old or more")))
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email)}.throws(validationException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(validationException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }
    }

    @Test
    fun `when modify an existent user with valid token without admin permissions and different email between modified and modifier should return Unauthorized Different Change User exception with 403`(){
        val unauthorizedDifferentUserChangeException=UnauthorizedDifferentUserChangeException()
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email+"A")

        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email+"A")}.throws(unauthorizedDifferentUserChangeException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(unauthorizedDifferentUserChangeException.createErrorResponse()).status(HttpStatus.FORBIDDEN_403) }


    }

    @Test
    fun `when modify an existent user with valid token without admin permissions and equal email between modified and modifier and change its role should return Unauthorized Role Change exception with 403`(){
        val unauthorizedRoleChangeException = UnauthorizedRoleChangeException()
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email+"A")

        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email+"A")}.throws(unauthorizedRoleChangeException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(unauthorizedRoleChangeException.createErrorResponse()).status(HttpStatus.FORBIDDEN_403) }


    }

    @Test
    fun `when modify an existent user with valid token with admin permissions and different email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email+"A")

        every{ userServiceMock.update(1, returnedUser, Roles.ADMIN, returnedUser.email+"A")}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

        //every { jwtAccessManagerMock.manage(contextMock) }

    }


    @Test
    fun `when modify an existent user with valid token with admin permissions and equal email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.update(1, returnedUser, Roles.ADMIN, returnedUser.email)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

        //every { jwtAccessManagerMock.manage(contextMock) }

    }

    @Test
    fun `when modify an existent user with valid token without admin permissions and equal email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.USER)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.update(1, returnedUser, Roles.USER, returnedUser.email)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

        //every { jwtAccessManagerMock.manage(contextMock) }

    }


    @Test
    fun `when delete a user by id and it exists, return 200 with user deleted`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.delete(1, returnedUser.role, returnedUser.email)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }
    }

    @Test
    fun `when delete a user by id and it does not exists, should return not found with status 404 `(){
        val userNotFoundException=UserNotFoundException()
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.delete(1, returnedUser.role, returnedUser.email)}.throws(userNotFoundException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).deleteUser(contextMock)

        verify { contextMock.json(userNotFoundException.createErrorResponse()).status(HttpStatus.NOT_FOUND_404) }
    }

    @Test
    fun `when delete an existent user with valid token without admin permissions and different email between modified and modifier should return Unauthorized Different User Change exception with 403`(){
        val unauthorizedDifferentUserChangeException=UnauthorizedDifferentUserChangeException()
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.delete(1, returnedUser.role, returnedUser.email)}.throws(unauthorizedDifferentUserChangeException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).deleteUser(contextMock)

        verify { contextMock.json(unauthorizedDifferentUserChangeException.createErrorResponse()).status(HttpStatus.FORBIDDEN_403) }


    }

    @Test
    fun `when delete an existent user with valid token with admin permissions and different email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email+"A")

        every{ userServiceMock.delete(1, Roles.ADMIN, returnedUser.email+"A")}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }

        //every { jwtAccessManagerMock.manage(contextMock) }

    }


    @Test
    fun `when delete an existent user with valid token with admin permissions and equal email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.delete(1, Roles.ADMIN, returnedUser.email)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }

        //every { jwtAccessManagerMock.manage(contextMock) }

    }

    @Test
    fun `when delete an existent user with valid token without admin permissions and equal email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.USER)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.delete(1, Roles.ADMIN, returnedUser.email)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }

        //every { jwtAccessManagerMock.manage(contextMock) }

    }


//    @Test
//    fun `when a user has a invalid gender, should return bad request with status 400`(){
//        val invalidGenderException=InvalidGenderException()
//        val invalidFormatException=InvalidFormatException.from(contextMock.body<NewUser>())
//
//        every { contextMock.body<NewUser>() }.throws(InvalidFormatException())
//
//        UserController(userServiceMock, jwtAccessManagerMock).addUser(contextMock)
//
//        verify { contextMock.json(invalidGenderException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }
//    }

    @Test
    fun `when add a valid user with admin permissions should return the user with status 201`(){

        every{ userServiceMock.add(newAdminUser)}.returns(returnedAdminUser)

        every { contextMock.body<NewUser>() }.returns(newAdminUser)

        UserController(userServiceMock, jwtAccessManagerMock).addUser(contextMock)

        verify { contextMock.json(returnedAdminUser).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when a user with valid credentials log in, should return the logged user with status 200`(){
        every { userServiceMock.login(newLoginUser) }.returns(returnedUser)

        every { contextMock.body<UserLogin>() }.returns(newLoginUser)

        UserController(userServiceMock, jwtAccessManagerMock).loginUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

    }
    @Test
    fun `when a user with non matching credentials log in, should return unauthorized with status 401`(){
        val invalidCredentialsException=InvalidCredentialsException()
        every { userServiceMock.login(newLoginUser) }.throws(invalidCredentialsException)

        every { contextMock.body<UserLogin>() }.returns(newLoginUser)

        UserController(userServiceMock, jwtAccessManagerMock).loginUser(contextMock)

        verify { contextMock.json(invalidCredentialsException.createErrorResponse()).status(HttpStatus.UNAUTHORIZED_401) }

    }


    @Test
    fun `mocking a relaxed sum`(){
        val sumMock=mockk<Sum>(relaxed = true)

        //every { sumMock.sum(1,2) }.returns(3)

        assertEquals("", sumMock.sum(1,2).philo)
    }

//    @Test
//    fun `mocking a non-relaxed sum`(){
//        val sumMock=mockk<Sum>()
//
//        every { sumMock.sum(1,2) }.returns(Animal(null,null))
//
//        assertEquals(Animal("A1","B2"), sumMock.sum(1,2))
//    }

    @Test
    fun `mocking a spy sum`(){
        val sumMock=spyk<Sum>()

        //every { sumMock.sum(1,2) }.returns(0)

        assertEquals("A1", sumMock.sum(1,2).name)
    }

//    @Test
//    fun `mocking a relaxed animal`(){
//        val animalMock=mockk<Animal>(relaxed = true)
//
//        animalMock.name="cat"
//        animalMock.philo="chordata"
//
//        animalMock.print()
//        verify { animalMock.changeName() }
//
//        assertEquals("", animalMock.print())
//    }

//    @Test
//    fun `mocking an animal`(){
//        val animalMock=mockk<Animal>()
//        animalMock.name="cat"
//        animalMock.philo="chordata"
//
//        animalMock.print()
//        verify { animalMock.changeName() }
//
//
//        //assertEquals(" and ", animalMock.print())
//    }

}

class Sum{
    fun sum(a:Int, b:Int):Animal {
        return Animal("A"+a.toString(),"B"+b.toString())
    }
}

class Animal(var name:String?=null, var philo:String?=null){
    fun print():String{
        //changeName()
        return "$name and $philo"
    }

    fun changeName():String{
        return name+"L"
    }
}