#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.commons.aop;

import org.aspectj.lang.annotation.Pointcut;

/**
 * Define los pointcuts para el traceo de la aplicación
 *
 */
public class SystemArchitecture {
    /**
     * Pointcut para la capa Web de la aplicación.
     */
	@Pointcut("execution(public * @org.springframework.web.bind.annotation.RestController *.*(..)) || execution(public * @org.springframework.stereotype.Controller *.*(..))")
    public void inWebLayer() {
        //Firma del pointcut para la capa de servicios de la aplicación
    }

    /**
     * Pointcut para la capa de Servicios de la aplicación.
     */
    @Pointcut("execution(public * @org.springframework.stereotype.Service *.*(..))")
    public void inServiceLayer() {
        //Firma del pointcut para la capa de servicios de la aplicación
    }

    /**
     * Pointcut para la capa de persistencia de la aplicación.
     */
    
    @Pointcut("execution(public !void org.springframework.data.repository.Repository+.*(..))")
    public void inDataAccessLayer() {
        //Firma del pointcut para la capa de repositorios de la aplicación
    }
    
    /**
     * Pointcut para componentes que tengan la anotación personalizada @Loggable
     */
    @Pointcut("execution(public * @${package}.commons.annotation.loggable.Loggable *.*(..))")
    public void loggableElement() {
        //Firma del pointcut para elementos con la anotación personalizada @Loggable
    }
    
    /**
     * Pointcut para componentes que tengan la anotación personalizada Traceable
     */
    @Pointcut("execution(public * @${package}.commons.annotation.traceable.Traceable *.*(..))")
    public void traceableElement() {
        //Firma del pointcut para elementos con la anotación personalizada @Traceable
    }
}
