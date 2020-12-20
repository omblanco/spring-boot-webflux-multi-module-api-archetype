#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.commons.aop;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Registra los errores de la aplicación
 *
 * see
 * https://azizulhaq-ananto.medium.com/how-to-handle-logs-and-tracing-in-spring-webflux-and-microservices-a0b45adc4610
 */
@Aspect
public class LoggingAspect {

    private static final String LOG_ENTER_PATTERN = "Request uuid: {} -> Enter: {}.{}() with argument[s] = {}";
    private static final String LOG_EXIT_PATTERN = "Request uuid: {} -> Exit: {}.{}() with result = {}";
    private static final String LOG_ERROR_ARROUND_PATTERN = "Request uuid: {} -> Illegal argument: {} in {}.{}()";
    
    private static final String LOG_ERROR_PATTERN = "Error in {}.{}.{}()";
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Traza correspondiente a un error en la capa de acceso a
     * 
     * @param joinPoint
     * @param e         Error
     */
    @AfterThrowing(pointcut = "SystemArchitecture.inDataAccessLayer()", throwing = "e")
    public void logAfterDataAccessThrowing(JoinPoint joinPoint, Throwable e) {
        String typeName = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String msg = e.getMessage();
        LOGGER.error(LOG_ERROR_PATTERN, typeName, methodName, msg, e);
    }

    /**
     * Trazas de log de entrada/salida/excepción para la capa de servicio.
     * 
     * @param joinPoint
     * @return el objeto que sería devuelto originalmente en el método.
     * @throws Throwable Excepción lanzada por el método
     */
    @Around("SystemArchitecture.inServiceLayer() || SystemArchitecture.inWebLayer() || SystemArchitecture.inDataAccessLayer() || SystemArchitecture.loggableElement() || SystemArchitecture.traceableElement()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {

        String uuid = UUID.randomUUID().toString();
        
        try {
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(LOG_ENTER_PATTERN, uuid, joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
            }
            
            Object result = joinPoint.proceed();

            if (LOGGER.isDebugEnabled()) {
                if (result instanceof Mono) {
                    var monoResult = (Mono<?>) result;

                    return monoResult.doOnSuccess(o -> {
                        var response = "";
                        if (Objects.nonNull(o)) {
                            response = o.toString();
                        }
                        LOGGER.debug(LOG_EXIT_PATTERN, uuid,
                                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                                response);
                    });
                }if (result instanceof Flux) {
                    var fluxResult = (Flux<?>) result;
                    return fluxResult.map(fluxItem -> {
                        LOGGER.debug(LOG_EXIT_PATTERN, uuid, joinPoint.getSignature().getDeclaringTypeName(),
                                joinPoint.getSignature().getName(), fluxItem);
                        return fluxItem;
                    });
                    
                } else {
                    LOGGER.debug(LOG_EXIT_PATTERN, uuid, joinPoint.getSignature().getDeclaringTypeName(),
                            joinPoint.getSignature().getName(), result);
                }
            }
            return result;
        } catch (IllegalArgumentException e) {
            LOGGER.error(LOG_ERROR_ARROUND_PATTERN, uuid, Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), e);
            throw e;
        }
    }

}
