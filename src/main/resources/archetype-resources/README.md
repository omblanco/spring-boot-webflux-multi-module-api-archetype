# Spring Boot Webflux Multi Module Api
Spring Boot Webflux Multi Module Api

## Índice
- [Descripción](#descripción)
- [Características](#características)
- [Módulos](#módulos)
- [Uso](#uso)

## Descripción
Api rest de ejemplo para la creación de microservicios reactivos con Spring Boot y Spring WebFlux tanto con una base de datos MySQL como Mongo. Contiene ejemplos tanto de definición de controladores con Spring MVC como con Endpoints Funcionales ([Functional Endpoints](https://spring.getdocs.org/en-US/spring-framework-docs/docs/spring-web-reactive/webflux/webflux-fn.html)). Implementa características como la securización, trazabilidad de capas, bbdd en memoria, documentación del endpoint … todas ellas activables mediante perfiles de Spring. La funcionalidad y los componentes están basados en [Spring Boot WebFlux Simple Api
](https://github.com/omblanco/spring-boot-webflux-simple-api), la diferencia es la división de los módulos. Se aplica una división por capas Model, Service, Web y App. Cada capa se abstrae de la implementación utilizada en los módulos inferiores.

## Características
Además de las descritas para [Spring Boot WebFlux Simple Api](https://github.com/omblanco/spring-boot-webflux-simple-api), se añaden:

### División de las capa en módulos
- model: Capa de acceso a datos, cuenta con dos implementaciones SQL y Mongo).
- service: Capa de negocio que hace uso del modelo, aplica la lógica de negocio para cada caso de uso y devuelve los resultados a la capa de nivel superior, en este caso la capa web.
- web: Habilita los puntos de entrada de la información, en este caso servicios rest mediante controladores y handlers.

### Conversión de modelos por capas
Cada capa tiene su modelo de datos: DTO, BO y DAO con sus respectivos transformadores para realizar la conversión.

### Interfaces
Utilización de interfaces en los módulos Service y Model para encapsular y abstraer la implementación.

## Módulos
- spring-boot-webflux-multi-module-api-aop: definiciones de los pointcut a tracear y anotaciones para tracear la aplicación.
- spring-boot-webflux-multi-module-api-client: cliente reactivo para consumir el api de usuarios expuesta.
- spring-boot-webflux-multi-module-api-model: módulo de acceso a datos.
	- spring-boot-webflux-multi-module-api-model-interface: interfaz para usar el acceso a datos
	- spring-boot-webflux-multi-module-api-model-mongo-impl: implementación para usar una MongoDB de forma reactiva.
	- spring-boot-webflux-multi-module-api-model-sql-impl: implementación para usar una base de datos SQL relacional de forma reactiva.
- spring-boot-webflux-multi-module-api-security: implementación para la securización de la aplicación
- spring-boot-webflux-multi-module-api-service: módulo de la capa de negocio.
	- spring-boot-webflux-multi-module-api-service-interface: interfaz para hacer uso de la capa de negocio.
	- spring-boot-webflux-multi-module-api-service-impl: implementación de la lógica de negocio.
- spring-boot-webflux-multi-module-api-web: capa web que habilita los accesos a negocio mediante api REST.
- spring-boot-webflux-multi-module-api-app: módulo de configuración de la aplicación SQL. Paquetes:
	- configuration: clases que configuran la aplicación
	- web: controladores, dtos y handlers acoplados a la implementación SQL.
- spring-boot-webflux-multi-module-api-mongo-app: módulo de configuración de la aplicación Mongo.
	- configuration: clases que configuran la aplicación
	- web: controladores, dtos y handlers acoplados a la implementación Mongo.

- [Uso](#uso)
[Spring Boot WebFlux Simple Api](https://github.com/omblanco/spring-boot-webflux-simple-api#uso)
