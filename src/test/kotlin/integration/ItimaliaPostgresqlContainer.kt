package integration

import org.testcontainers.containers.PostgreSQLContainer

private const val IMAGE_VERSION = "postgres:11.1"

// https://www.baeldung.com/spring-boot-testcontainers-integration-test
class ItimaliaPostgresqlContainer :
    PostgreSQLContainer<ItimaliaPostgresqlContainer>(IMAGE_VERSION) {

    companion object {
        private var container: ItimaliaPostgresqlContainer? = null
        fun getInstance(): ItimaliaPostgresqlContainer? {
            if (container == null) {
                container = ItimaliaPostgresqlContainer()
            }
            return container
        }
    }

    override fun start() {
        super.start()
        container?.jdbcUrl?.let { System.setProperty("JDBC_URL", it) }
        container?.username?.let { System.setProperty("DATABASE_USERNAME", it) }
        container?.password?.let { System.setProperty("DATABASE_PASSWORD", it) }
    }

    override fun stop() {
    }
}
