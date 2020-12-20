#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.web.controllers;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import ${package}.commons.web.dto.appinfo.AppInfoDTO;
import ${package}.commons.web.utils.BaseApiConstants;

/**
 * AppInfoController
 * @author oscar.martinezblanco
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
@AutoConfigureTestDatabase
public class AppInfoControllerTests {

    @Autowired
    private WebTestClient client;
    
    @Value("${symbol_dollar}{app.version}")
    private String appVersion;
    
    @Value("${symbol_dollar}{app.environment}")
    private String environment;
    
    @Value("${symbol_dollar}{app.name}")
    private String name;
    
    @Test
    public void getTest() {
        client.get()
        .uri(BaseApiConstants.STATUS_BASE_URL_V1)
        .accept(MediaType.APPLICATION_JSON)
        .exchange().expectStatus()
        .isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(AppInfoDTO.class)
        .consumeWith(response -> {
            AppInfoDTO appInfoDto = response.getResponseBody();
            
            Assertions.assertThat(appInfoDto.getName()).isEqualTo(name);
            Assertions.assertThat(appInfoDto.getEnvironment()).isEqualTo(environment);
            Assertions.assertThat(appInfoDto.getVersion()).isEqualTo(appVersion);
        });
    }
}
