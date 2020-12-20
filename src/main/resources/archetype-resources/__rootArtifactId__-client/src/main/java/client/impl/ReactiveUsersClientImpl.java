#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import ${package}.client.ReactiveUsersClient;
import ${package}.client.dto.LoginResponseDTO;
import ${package}.client.dto.RestResponsePage;
import ${package}.client.dto.UserDTO;
import ${package}.client.dto.UserFilterDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class ReactiveUsersClientImpl<K> implements ReactiveUsersClient<K> {
	
    private final String user;
    private final String password;
    private final String endpoint;
    private String version = "3";
    
    private static final String COMMA_TOKEN = ",";
    
    private static final String AUTH_URL = "/auth/login";
    private static final String USER_BASE_URL = "/api/v%s/users";
    private static final String USER_BASE_URL_WITH_ID_PARAM = USER_BASE_URL.concat("/{id}");
    private static final String PARAM_ID_NAME = "id";
    
    private static final String BEARER_PARAM_NAME = "Bearer ";
    
    private static final String EMAIL_PARAM_NAME = "email";
    private static final String NAME_PARAM_NAME = "name";
    private static final String SURNAME_PARAM_NAME = "surname";
    private static final String SORT_PARAM_NAME = "sort";
    private static final String PAGE_PARAM_NAME = "page";
    private static final String SIZE_PARAM_NAME = "size";
    
    private final WebClient client;
    
    public ReactiveUsersClientImpl(String user, String password, String endpoint) {
        this.user = user;
        this.password = password;
        this.endpoint = endpoint;
        this.client = WebClient.create(this.endpoint);
    }
    
    public ReactiveUsersClientImpl(String user, String password, String endpoint, String version) {
        this(user, password, endpoint);
        this.version = version;
    }

    @Override
    public Flux<UserDTO<K>> getAllUsers() {
        return getToken().flatMapMany(token -> {
            return this.client.get()
                    .uri(String.format(USER_BASE_URL, this.version))
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PARAM_NAME.concat(token.getToken()))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<UserDTO<K>>(){});
        });
    }

    @Override
    public Mono<UserDTO<K>> get(K id) {
        return getToken().flatMap(token -> {
            return this.client.get()
                    .uri(String.format(USER_BASE_URL_WITH_ID_PARAM, this.version), Collections.singletonMap(PARAM_ID_NAME, id))
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PARAM_NAME.concat(token.getToken()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<UserDTO<K>>(){});
        });
    }
    
    @Override
    public Mono<UserDTO<K>> save(UserDTO<K> user) {
        return getToken().flatMap(token -> {
            return client.post()
                    .uri(String.format(USER_BASE_URL, this.version))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PARAM_NAME.concat(token.getToken()))
                    .body(BodyInserters.fromValue(user))
                    .retrieve().bodyToMono(new ParameterizedTypeReference<UserDTO<K>>(){});
        });
    }

    @Override
    public Mono<UserDTO<K>> update(UserDTO<K> user, K id) {
        return getToken().flatMap(token -> {
            return client.put().uri(String.format(USER_BASE_URL_WITH_ID_PARAM, this.version), Collections.singletonMap(PARAM_ID_NAME, id))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PARAM_NAME.concat(token.getToken()))
                    .body(BodyInserters.fromValue(user))
                    .retrieve().bodyToMono(new ParameterizedTypeReference<UserDTO<K>>(){});
        });
    }

    @Override
    public Mono<Void> delete(K id) {
        return getToken().flatMap(token -> {
            return client.delete()
                    .uri(String.format(USER_BASE_URL_WITH_ID_PARAM, this.version), Collections.singletonMap(PARAM_ID_NAME, id))
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PARAM_NAME.concat(token.getToken()))
                    .retrieve()
                    .bodyToMono(Void.class);
        });
    }

	@Override
	public Flux<UserDTO<K>> getUsers(UserFilterDTO filter, Sort sort) {
        return getToken().flatMapMany(token -> {
            
            List<String> sorts = new ArrayList<>();

            if (sort != null) {
                sort.forEach(order -> sorts.add(String.join(COMMA_TOKEN, order.getProperty(), order.getDirection().toString())));
            }
            
            return this.client.get()
                    .uri(builder -> builder.path(String.format(USER_BASE_URL, this.version))
                            .queryParam(EMAIL_PARAM_NAME, filter.getEmail())
                            .queryParam(NAME_PARAM_NAME, filter.getName())
                            .queryParam(SURNAME_PARAM_NAME, filter.getSurname())
                            .queryParam(SORT_PARAM_NAME, sorts.toArray())
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PARAM_NAME.concat(token.getToken()))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<UserDTO<K>>(){});
        });
	}

    @Override
    public Mono<RestResponsePage<UserDTO<K>>> getUsers(UserFilterDTO filter, Pageable pageable) {
        return getToken().flatMap(token -> {
            
            List<String> sorts = new ArrayList<>();
            
            if (pageable.getSort() != null) {
                pageable.getSort().forEach(order -> sorts.add(String.join(COMMA_TOKEN, order.getProperty(), order.getDirection().toString())));
            }
            
            return this.client.get()
                    .uri(builder -> builder.path(String.format(USER_BASE_URL, this.version))
                            .queryParam(EMAIL_PARAM_NAME, filter.getEmail())
                            .queryParam(NAME_PARAM_NAME, filter.getName())
                            .queryParam(SURNAME_PARAM_NAME, filter.getSurname())
                            .queryParam(SORT_PARAM_NAME, sorts.toArray())
                            .queryParam(PAGE_PARAM_NAME, pageable.getPageNumber())
                            .queryParam(SIZE_PARAM_NAME, pageable.getPageSize())
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PARAM_NAME.concat(token.getToken()))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<RestResponsePage<UserDTO<K>>>(){});
        });
    }
    
    private Mono<LoginResponseDTO> getToken() {
        return  this.client.post().uri(AUTH_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new LoginRequestDTO(this.user, this.password)))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(LoginResponseDTO.class)
            .onErrorMap(e -> new Exception("Error While getting Token", e));
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    private class LoginRequestDTO {

        private String email;
        
        private String password;
    }    
}
