#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.commons.web.controllers.appinfo;

import static ${package}.commons.web.utils.BaseApiConstants.STATUS_BASE_URL_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ${package}.commons.web.dto.appinfo.AppInfoDTO;

import reactor.core.publisher.Mono;

/**
 * Controlador del estado de la aplicación
 * @author oscar.martinezblanco
 *
 */
@RestController
@RequestMapping(STATUS_BASE_URL_V1)
public class AppInfoController {

    @Value("${symbol_dollar}{app.version}")
    private String appVersion;
    
    @Value("${symbol_dollar}{app.environment}")
    private String environment;
    
    @Value("${symbol_dollar}{app.name}")
    private String name;
    
    /**
     * Recupera el estado de la aplicación
     * @return
     */
    @GetMapping
    public Mono<ResponseEntity<AppInfoDTO>> getAppInfo() {
        
        AppInfoDTO appInfoDto = new AppInfoDTO();
        appInfoDto.setEnvironment(environment);
        appInfoDto.setVersion(appVersion);
        appInfoDto.setNow(new Date());
        appInfoDto.setName(name);
        
        return Mono.just(ResponseEntity.ok().contentType(APPLICATION_JSON).body(appInfoDto));
    }
}
