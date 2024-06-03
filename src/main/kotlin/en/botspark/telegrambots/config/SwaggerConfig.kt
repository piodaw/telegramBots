package en.botspark.telegrambots.config

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;


@Configuration
class SwaggerConfig {

    //    @Value("\${dodac env dev}")
    private val devUrl: String? = "http://localhost:8080"

    //    @Value("\${dodac env prod}")
    private val prodUrl: String? = null

    @Bean
    fun myOpenAPI(): OpenAPI {
        val devServer: Server = Server()
        devServer.setUrl(devUrl)
        devServer.setDescription("Server URL in Development environment")

        val prodServer: Server = Server()
        prodServer.setUrl(prodUrl)
        prodServer.setDescription("Server URL in Production environment")

        val contact: Contact = Contact()
        contact.setEmail("bochen@bochen.com")
        contact.setName("Bochen")

        val mitLicense: License = License().name("MIT License").url("https://choosealicense.com/licenses/mit/")

        val info: Info = Info()
            .title("Tutorial Management API")
            .version("1.0")
            .contact(contact)
            .description("This API exposes endpoints to manage tutorials.")
            .license(mitLicense)

        return OpenAPI()
            .info(info)
            .servers(listOf(devServer, prodServer))
    }
}