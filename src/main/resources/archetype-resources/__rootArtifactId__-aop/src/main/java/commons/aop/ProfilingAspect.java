#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.commons.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

@Aspect
public class ProfilingAspect {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfilingAspect.class);

    /**
     * Registra datos de temporización en los logs.
     */
    @Around("SystemArchitecture.inServiceLayer() || SystemArchitecture.traceableElement()")
    public Object profile(ProceedingJoinPoint call) throws Throwable {
        StopWatch clock = new StopWatch("Profiling");
        try {
            clock.start(call.toShortString());
            return call.proceed();
        } finally {
            clock.stop();
            LOGGER.info(clock.prettyPrint());
        }
    }

}
