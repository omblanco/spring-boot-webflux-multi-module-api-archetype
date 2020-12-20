#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.commons.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Clase que valida el token de autenticación
 * @author oscar.martinezblanco
 *
 */
@AllArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private TokenProvider tokenProvider;

    private static final String AUTHORITIES_KEY = "scopes";

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String email;
        try {
            email = tokenProvider.getEmailFromToken(authToken);
        } catch (Exception e) {
            email = null;
        }
        if (email != null && !tokenProvider.isTokenExpired(authToken)) {
            Claims claims = tokenProvider.getAllClaimsFromToken(authToken);
            List<String> roles = claims.get(AUTHORITIES_KEY, List.class);
            
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
            
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, email,
                    authorities);
            SecurityContextHolder.getContext().setAuthentication(new AuthenticatedUser(email, authorities));
            
            return Mono.just(auth);
        } else {
            
            return Mono.empty();
        }
    }
}
