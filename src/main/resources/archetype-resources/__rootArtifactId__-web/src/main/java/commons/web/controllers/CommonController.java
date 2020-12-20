#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.commons.web.controllers;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import ${package}.commons.annotation.loggable.Loggable;
import ${package}.commons.web.dto.CommonDTO;
import ${package}.service.CommonService;

import reactor.core.CorePublisher;
import reactor.core.publisher.Mono;

/**
 * Controlador genérico
 * @author oscar.martinezblanco
 *
 * @param <D> Clase DTO
 * @param <F> Clase Filter
 * @param <E> Clase Entity
 * @param <S> Clase del servicio
 * @param <K>
 */
@Loggable
public abstract class CommonController <DTO extends CommonDTO<ID>, BO, DAO, S extends CommonService<BO, DAO, ID>, ID> {

    protected static final String ID_PARAM_URL = "/{id}";
    
    protected static final String FORWARD_SLASH = "/";
    
    protected S service;
    
    public CommonController(S service) {
        this.service = service;
    }
    
    /**
     * Recupera una lista de dtos
     * @return Lista de dtos
     */
    @ResponseBody
    protected Mono<ResponseEntity<CorePublisher<?>>> findAll() {
        return Mono.just(ResponseEntity.ok().contentType(APPLICATION_JSON)
                .body(service.findAll()
                .map(this::convertToDto)));
    }
    
    /**
     * Recupera un dto por clave primaria
     * 
     * @param id Clave primaria
     * @return DTO
     */
    @GetMapping(ID_PARAM_URL)
    @ResponseBody
    public Mono<ResponseEntity<DTO>> get(@PathVariable ID id) {
        
        return service.findById(id)
            .map(bo -> ResponseEntity.ok().contentType(APPLICATION_JSON).body(convertToDto(bo)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Crea un usuario
     * 
     * @param monoUser Usuario a guardar
     * @return Usuario resultado de la operación
     */
    @PostMapping
    @ResponseBody
    public Mono<ResponseEntity<DTO>> create(@RequestBody @Valid Mono<DTO> monoDto) {
        return monoDto.flatMap(dto -> {
            dto.setId(null);
            return service.save(convertToBo(dto))
                    .map(this::convertToDto)
                    .map(savedDto -> ResponseEntity
                              .created(URI.create(getBaseUrl().concat(FORWARD_SLASH).concat(savedDto.getId().toString())))
                              .contentType(APPLICATION_JSON).body(savedDto));
        });
    }
    
    /**
     * Actualiza un dto
     * 
     * @param dto DTO a actualizar
     * @param id   Clave primaria del DTO a actualizar
     * @return Resultado de la actualización
     */
    @PutMapping(ID_PARAM_URL)
    @ResponseBody
    public Mono<ResponseEntity<DTO>> update(@PathVariable ID id, @Valid @RequestBody DTO dto) {
        return service.findById(id).flatMap(boToSave -> {
            updateBoToSave(dto, boToSave);
            return service.save(boToSave).map(this::convertToDto);
        }).map(savedDTO -> ResponseEntity.created(URI.create(getBaseUrl().concat(FORWARD_SLASH).concat(savedDTO.getId().toString())))
                .contentType(APPLICATION_JSON)
                .body(savedDTO))
        .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Elimina un dto
     * 
     * @param id Clave primaria del dto a eliminar
     * @return Resultado de la operación
     */
    @DeleteMapping(ID_PARAM_URL)
    public Mono<ResponseEntity<Void>> delete(@PathVariable ID id) {
        return service.findById(id).flatMap(dtoDb -> {
            return service.delete(dtoDb).then(Mono.just(new ResponseEntity<Void>(NO_CONTENT)));
        }).defaultIfEmpty(new ResponseEntity<Void>(NOT_FOUND));
    }
    
    protected abstract String getBaseUrl();
    
    protected abstract void updateBoToSave(DTO requestDto, BO bo);
    
    /**
     * Conversión de BO a DTO
     * @param bo BO
     * @return DTO
     */
    protected abstract DTO convertToDto(BO bo);
    
    /**
     * Transforma una página de BOs en DTOs
     * @param boPage Página de BOs
     * @return Página de DTOs
     */
    protected abstract Page<DTO> convertPageToBo(Page<BO> boPage);
    
    /**
     * Transforma un DTO en BO
     * @param dto DTO
     * @return BO
     */
    protected abstract BO convertToBo(DTO dto);
}
