package dev.templates.springhtmxtemplate

import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class HomeControllerTest(
    @Autowired private val webTestClient: WebTestClient
) {
    @Test
    fun `it renders the home page`() {
        webTestClient.get().uri("/")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .consumeWith { response ->
                val responseBody = response.responseBody
                responseBody shouldContain "Spring HTMX Template"
            }
    }
}