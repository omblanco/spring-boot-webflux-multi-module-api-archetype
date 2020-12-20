#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.commons.web.utils;

/**
 * Fichero de constantes
 * @author oscar.martinezblanco
 *
 */
public class BaseApiConstants {

    private BaseApiConstants() {
        //Empty
    }
    
    public static final String AUTH_URL_V1 = "/auth/login";
    
    public static final String USER_BASE_URL_V1 = "/api/v1/users";
    
    public static final String USER_BASE_URL_V2 = "/api/v2/users";
    
    public static final String USER_BASE_URL_V3 = "/api/v3/users";
    
    public static final String STATUS_BASE_URL_V1 = "/api/v1/status";
    
    public static final String ID_PARAM_URL = "/{id}";
    
    public static final String FORWARD_SLASH = "/";
    
    public static final String ID_PARAM_NAME = "id";

}
