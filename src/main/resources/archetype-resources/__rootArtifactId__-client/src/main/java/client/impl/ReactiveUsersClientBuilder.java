#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client.impl;

import ${package}.client.ReactiveUsersClient;

/**
 * Builder para obtener una instacia de forma estática
 * @author oscar.martinezblanco
 *
 */
public class ReactiveUsersClientBuilder<K> {

    private String user;
    
    private String password;
    
    private String endpoint;
    
    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Obtiene la instancia de un client
     * El usuario, la contraseña y el endpoint no pueden estar vacías
     * @return Cliente
     */
    public ReactiveUsersClient<K> build() {
        emptyString(this.user, "Usuario");
        emptyString(this.password, "Contraseña");
        emptyString(this.endpoint, "Endpoint");
        return new ReactiveUsersClientImpl<K>(this.user, this.password, this.endpoint);
    }
    
    /**
     * Obtiene una instancia del cliente
     * @param user Usuario
     * @param password Password
     * @param endpoint Endpoint 
     * @return Cliente
     */
    public static <K> ReactiveUsersClient<K> build(String user, String password, String endpoint) {
        emptyString(user, "Usuario");
        emptyString(password, "Contraseña");
        emptyString(endpoint, "Endpoint");
        return new ReactiveUsersClientImpl<K>(user, password, endpoint);
    }
    
    /**
     * Obtiene una instancia del cliente
     * @param user Usuario
     * @param password Password
     * @param endpoint Endpoint 
     * @param version Version
     * @return
     */
    public static <K> ReactiveUsersClient<K> build(String user, String password, String endpoint, String version) {
        emptyString(user, "Usuario");
        emptyString(password, "Contraseña");
        emptyString(endpoint, "Endpoint");
        emptyString(version, "Version");
        return new ReactiveUsersClientImpl<>(user, password, endpoint, version);
    }    

    /**
     * Valida si una cadena está vacía
     * @param value Valor de propiedad a validar
     * @param property Nombre de la propiedad
     */
    private static void emptyString(String value, String property) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(String.format("La propiedad %s no puede estar vacía.", property));
        }
    }
}
