package dev.templates.springhtmxtemplate

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.web.reactive.function.BodyInserters

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AuthTest(
    @Autowired private val webTestClient: WebTestClient
) {
    @Test
    fun `it redirects to the login page when the user is not authenticated`() {
        webTestClient.get().uri("/")
            .exchange()
            .shouldRedirectTo("/login")
    }

    @Test
    fun `it returns 200 for endpoints not requiring authentication`() {
    }

    @Test
    fun `it allows user to register and successfully login and logout`() {
        webTestClient.post().uri("/registration")
            .body(BodyInserters
                .fromFormData("email", "me@email.com")
                .with("password", "somePassword")
            )
            .exchange()
            .shouldRedirectTo("/login")

        val loginResponse = webTestClient.post().uri("/login")
            .body(BodyInserters
                .fromFormData("username", "me@email.com")
                .with("password", "somePassword")
            )
            .exchange()
            .shouldRedirectTo("/")
            .returnResult<String>()

        val jsessionId = loginResponse.responseCookies["JSESSIONID"]?.firstOrNull()!!.value

        webTestClient.get().uri("/")
            .cookie("JSESSIONID", jsessionId)
            .exchange()
            .expectStatus().isOk
    }

    private fun WebTestClient.ResponseSpec.shouldRedirectTo(path: String): WebTestClient.ResponseSpec {
        this.expectStatus().isFound
            .expectHeader()
            .value("Location") { v -> v shouldMatch "^http:\\/\\/localhost:[0-9]+\\$path\$" }
        return this
    }
}