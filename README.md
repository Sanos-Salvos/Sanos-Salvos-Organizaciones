# Sanos-Salvos-Organizaciones
Microservicio encargado de gestionar organizaciones y fundaciones de rescate animal dentro del ecosistema *Sanos y Salvos*.

Implementa:

- JWT Authentication
- Apache Kafka
- Circuit Breaker con Resilience4j

---

# Requisitos

- Java JDK 17
- Maven 3.8+
- Apache Kafka (puerto 9092)
- IntelliJ IDEA (opcional)
- Postman o Insomnia

---

# Instalación

## Clonar repositorio

```bash
git clone https://github.com/TU_USUARIO/Sanos-Salvos-Organizaciones.git
cd Sanos-Salvos-Organizaciones
```

## Instalar dependencias

### Linux / Mac

```bash
mvn clean install
```

### Windows

```powershell
.\mvnw clean install
```

---

# Configuración

Verificar:

```bash
src/main/resources/application.properties
```

Configuración principal:

```properties
server.port=8081
spring.application.name=microservicio-organizaciones

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# JWT
jwt.secret=TuClaveJWT
jwt.expiration=3600000

# Circuit Breaker
resilience4j.circuitbreaker.instances.usuariosService.slidingWindowSize=10
resilience4j.circuitbreaker.instances.usuariosService.failureRateThreshold=50

```

---

# Ejecución

Antes de iniciar el servicio, asegúrate de tener Apache Kafka ejecutándose.

## Desde IntelliJ

Ejecutar:

```bash
OrganizacionesApplication.java
```

## Desde consola

```bash
mvn spring-boot:run
```

El servicio quedará disponible en:

```bash
http://localhost:8081
```

---

# Pruebas API

## Endpoint protegido con JWT

### GET

```http
GET http://localhost:8081/api/organizaciones/1
```

### Header requerido

```http
Authorization: Bearer TU_TOKEN_JWT
```

### Respuesta esperada

```json
{
  "id": 1,
  "nombre": "Fundación Patitas A Salvo",
  "rut": "12.345.678-9",
  "direccion": "Av. Principal 123",
  "estado": "ACTIVA"
}
```

---

# Prueba Circuit Breaker

1. Apagar el microservicio `usuarios-service`.
2. Realizar una consulta o registro de organización.
3. Resilience4j ejecutará el método fallback evitando fallos en cascada.

---

# Prueba Kafka Producer

Al registrar una organización:

- Se enviará automáticamente un evento al tópico:

```bash
organizaciones-nuevas-topic
```

- El evento podrá ser consumido por otros microservicios como Notificaciones.

---

# Arquitectura

```text
Frontend
   ↓
BFF
   ↓
Organizaciones Service
   ↓
Kafka + Base de Datos
```

Tecnologías utilizadas:

- Spring Boot
- Spring Security JWT
- Apache Kafka
- Resilience4j Circuit Breaker
- H2 Database
- REST API