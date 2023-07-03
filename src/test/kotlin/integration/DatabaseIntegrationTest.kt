package integration

import com.abrigo.itimalia.application.config.DatabaseConfig
import com.abrigo.itimalia.application.web.ItimaliaApplication
import com.abrigo.itimalia.commons.koin.*
import com.abrigo.itimalia.domain.entities.user.LoggedUser
import com.abrigo.itimalia.domain.entities.user.UserRole
import com.abrigo.itimalia.domain.exceptions.ApiError
import com.abrigo.itimalia.domain.exceptions.ErrorResponse
import com.abrigo.itimalia.factories.UserFactory
import com.fasterxml.jackson.databind.ObjectMapper
import io.javalin.testtools.JavalinTest
import org.joda.time.DateTime
import org.junit.*
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext
import org.koin.test.KoinTest
import org.testcontainers.containers.PostgreSQLContainer
import uk.org.webcompere.systemstubs.rules.EnvironmentVariablesRule
import kotlin.test.assertEquals


val itimaliaApplication = ItimaliaApplication()

class DatabaseIntegrationTest: KoinTest {
    @get:Rule
    var environmentVariablesRule = EnvironmentVariablesRule()

    private val objectMapper: ObjectMapper by inject()

    private val app = itimaliaApplication.app

    @Before
    fun startRoutes() {
        environmentVariablesRule.set("ISSUER", "Itimalia").set("JWT_SECRET", "fabulous_password")
    }

    companion object {
        @get:ClassRule
        var postgreSQLContainer: PostgreSQLContainer<*> =
            ItimaliaPostgresqlContainer.getInstance() ?: throw IllegalArgumentException()

        @JvmStatic
        @BeforeClass
        fun startApp(){
            GlobalContext.startKoin {
                modules(
                    serviceModule,
                    controllerModule,
                    configModule,
                    JWTModule,
                    accessManagerModule,
                    repositoryModule,
                    imageModule,
                    passwordModule,
                    validationModule
                )
            }

            postgreSQLContainer.start()
            Thread.sleep(2000)

            val databaseConfig = DatabaseConfig(
                postgreSQLContainer.jdbcUrl,
                postgreSQLContainer.databaseName,
                postgreSQLContainer.password
            )

            databaseConfig.connect()
            databaseConfig.createTables()
        }
    }


    @Test
    fun `integration - given a valid admin login, when register a new user should return 200`() = JavalinTest.test(app) { server, client ->
        val addedFirstUserResponse = client.post("/users", UserFactory.sampleNewRequest(birthDate = DateTime(1999, 1, 1, 1, 1)))//{

        val addedFirstUser = objectMapper.readValue(
            addedFirstUserResponse.body?.string(),
            LoggedUser::class.java)

        val addedSecondUserResponse = client.post("/users", UserFactory.sampleNewRequest(
            email = "secondUser@mail.com",
            birthDate = DateTime(1999, 1, 1, 1, 1)
        ))

        //email already exists on create new user
        val expectedEmailAlreadyExistsErrorResponse = client.post("/users", UserFactory.sampleNewRequest(
            email = "secondUser@mail.com",
            birthDate = DateTime(1999, 1, 1, 1, 1)
        ))

        val emailAlreadyExistsError = objectMapper.readValue(
            expectedEmailAlreadyExistsErrorResponse.body?.string(),
            ErrorResponse::class.java)

        //email already exists on update user
        val expectedEmailAlreadyExistsErrorOnUpdateResponse = client.put("/users/${addedFirstUser.id}",
            UserFactory.sampleDTORequest(
            email = "secondUser@mail.com",
            role = UserRole.USER,
            birthDate = DateTime(1999, 1, 1, 1, 1))
        ) {
            it.header("Authorization", "Bearer ${addedFirstUser.token}")
        }

        val emailAlreadyExistsOnUpdateError = objectMapper.readValue(
            expectedEmailAlreadyExistsErrorOnUpdateResponse.body?.string(),
            ErrorResponse::class.java)

        assertEquals(201, addedFirstUserResponse.code)
        assertEquals(201, addedSecondUserResponse.code)

        //assert email already exists on create
        assertEquals(400, expectedEmailAlreadyExistsErrorResponse.code)
        assertEquals("This email already exists", emailAlreadyExistsError.message)
        assertEquals(ApiError.EMAIL_ALREADY_EXISTS_ERROR, emailAlreadyExistsError.apiError)

        //assert email already exists on update
        assertEquals(400, expectedEmailAlreadyExistsErrorOnUpdateResponse.code)
        assertEquals("This email already exists", emailAlreadyExistsOnUpdateError.message)
        assertEquals(ApiError.EMAIL_ALREADY_EXISTS_ERROR, emailAlreadyExistsOnUpdateError.apiError)

    }

}