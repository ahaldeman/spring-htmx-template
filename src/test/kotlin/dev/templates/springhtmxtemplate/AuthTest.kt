package dev.templates.springhtmxtemplate

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.StatusAssertions
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.web.reactive.function.BodyInserters

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AuthTest(
    @Autowired private val webTestClient: WebTestClient
) {
    companion object {
        @JvmStatic
        private fun nonAuthenticatedUris(): List<Arguments> = listOf(
            Arguments.of("/registration", StatusAssertions::isOk),
            Arguments.of("/h2-console/", StatusAssertions::isOk)
        )
    }

    @Test
    fun `it redirects to the login page when the user is not authenticated`() {
        webTestClient.get().uri("/")
            .exchange()
            .shouldRedirectTo("/login")
    }

    @ParameterizedTest
    @MethodSource("nonAuthenticatedUris")
    fun `it can access endpoints not requiring authentication without being authenticated`(
        uri: String,
        assertion: StatusAssertions.() -> WebTestClient.ResponseSpec
    ) {
        webTestClient.get().uri(uri)
            .exchange()
            .expectStatus().assertion()
    }

    @Test
    fun `it allows user to register and successfully login and logout`() {
        val email = "me@email.com"
        val firstName = "John"
        val lastName = "Doe"
        val password = "somePassword"

        registerUser(email = email, firstName = firstName, lastName = lastName, password = password)
            .shouldRedirectTo("/login")

        val sessionCookie = login(email = email, password = password)
            .shouldRedirectTo("/")
            .captureSessionCookie()

        webTestClient.get().uri("/")
            .cookie("JSESSIONID", sessionCookie)
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .consumeWith { body -> body.responseBody shouldContain "Welcome, <span>John</span>"}

        webTestClient.get().uri("/logout")
            .exchange()
            .shouldRedirectTo("/login\\?logout")
    }

    @Test
    fun `it shows an error message when the email is not registered`()  {
        login(email = "notregistered@email.com", password = "somePassword")
            .shouldRedirectTo("/login\\?error")

        webTestClient.get().uri("/login?error")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .consumeWith { body -> body.responseBody shouldContain "Invalid username or password" }
    }

    private fun registerUser(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
    ): WebTestClient.ResponseSpec {
        return webTestClient.post().uri("/registration")
            .body(BodyInserters
                .fromFormData("email", email)
                .with("firstName", firstName)
                .with("lastName", lastName)
                .with("password", password)
            )
            .exchange()
    }

    private fun login(email: String, password: String): WebTestClient.ResponseSpec {
        return webTestClient.post().uri("/login")
            .body(BodyInserters
                .fromFormData("username", email)
                .with("password", password)
            )
            .exchange()
    }

    private fun WebTestClient.ResponseSpec.shouldRedirectTo(path: String): WebTestClient.ResponseSpec {
        this.expectStatus().isFound
            .expectHeader()
            .value("Location") { v -> v shouldMatch "^http:\\/\\/localhost:[0-9]+\\$path\$" }
        return this
    }

    private fun WebTestClient.ResponseSpec.captureSessionCookie(): String {
        return this.returnResult<String>()
            .responseCookies["JSESSIONID"]
            ?.firstOrNull()!!.value
    }
}