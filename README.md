# usuario-service

# Microservicio de Usuarios - Arka

Microservicio encargado de la gestión de usuarios, proveedores y direcciones para el e-commerce Arka.

## 🎯 Funcionalidades

- Registro de usuarios (clientes y administradores)
- Gestión de proveedores
- Administración de direcciones de envío
- Encriptación de contraseñas

## 🛠️ Tecnologías

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Data JPA**
- **MySQL 8**
- **Spring Cloud Netflix Eureka Client**
- **Springdoc OpenAPI 2.7.0** (Swagger)
- **Lombok**
- **BCrypt** (encriptación de contraseñas)


## 🏗️ Arquitectura

Este microservicio implementa una **arquitectura en capas** (Layered Architecture):
```
┌─────────────────────────┐
│     Controller Layer    │  → Endpoints REST
├─────────────────────────┤
│      Service Layer      │  → Lógica de negocio
├─────────────────────────┤
│    Repository Layer     │  → Acceso a datos (JPA)
├─────────────────────────┤
│      Mapper Layer       │  → Conversión DTO ↔ Entity
├─────────────────────────┤
│     Exception Layer     │  → Manejo centralizado de errores
└─────────────────────────┘
```

### ¿Por qué capas y no arquitectura limpia?

- **Overhead mínimo**: Para operaciones CRUD no necesitamos la complejidad adicional de arquitectura limpia
- **Simplicidad adecuada**: Las capas son suficientes para este contexto de microservicio
- **Velocidad de desarrollo**: Menos abstracciones innecesarias
- **Spring Boot estándar**: Aprovecha las convenciones del framework


## 📁 Estructura del Proyecto
```
src/main/java/com/arka/usuario_service/
├── controller/
│   ├── UsuarioController.java       # Endpoints de usuarios
│   ├── ProovedorController.java     # Endpoints de proveedores
│   └── DireccionController.java     # Endpoints de direcciones
│
├── service/
│   ├── UsuarioService.java          # Lógica de negocio de usuarios
│   ├── ProveedoresService.java      # Lógica de negocio de proveedores
│   └── DireccionService.java        # Lógica de negocio de direcciones
│
├── repository/
│   ├── UsuarioRepository.java       # Acceso a datos de usuarios
│   ├── ProovedorRepository.java     # Acceso a datos de proveedores
│   └── DireccionesRepository.java   # Acceso a datos de direcciones
│
├── model/
│   ├── Usuarios.java                # Entidad Usuario (JPA)
│   ├── Proovedores.java             # Entidad Proveedor (JPA)
│   ├── Direcciones.java             # Entidad Dirección (JPA)
│   └── UserType.java                # Enum (cliente/administrador)
│
├── DTO/
│   ├── UserDto.java                 # DTO para crear usuario
│   ├── AdminDto.java                # DTO para crear admin
│   ├── UserResponse.java            # DTO de respuesta
│   ├── ProovedorDto.java            # DTO de proveedor
│   ├── DtoAddressCreate.java        # DTO para crear dirección
│   ├── UpdateUserRequest.java       # DTO para actualizar password
│   └── UserUpdateEmailOrName.java   # DTO para actualizar datos
│
├── Mapper/
│   ├── UserMapper.java              # Conversión User DTO ↔ Entity
│   ├── ProovedorMapper.java         # Conversión Proveedor DTO ↔ Entity
│   └── DireccionMapper.java         # Conversión Dirección DTO ↔ Entity
│
├── config/
│   └── SecurityConfigPassword.java  # Configuración BCrypt
│
├── excepcion/
│   └── GlobalExceptionHandler.java  # Manejo global de errores
│
└── UsuarioServiceApplication.java   # Clase principal
```

## ⚙️ Prerequisitos

- **Java 21** o superior
- **Maven 3.8+**
- **MySQL 8.0+**
- **Eureka Server** corriendo en `http://localhost:8761`

## 🚀 Instalación

### 1. Clonar el repositorio
```bash
git clone [URL_DEL_REPOSITORIO]
cd usuario-service
```

### 2. Configurar base de datos

Crear la base de datos en MySQL:
```sql
CREATE DATABASE arka;
```

### 3. Configurar variables de entorno

Editar `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/arka?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: TU_PASSWORD  # Cambiar por tu contraseña
```

### 4. Compilar el proyecto
```bash
mvn clean install
```

### 5. Ejecutar el microservicio
```bash
mvn spring-boot:run
```

El servicio estará disponible en: `http://localhost:8081`

## 📚 Documentación del API

### Swagger UI

La documentación interactiva del API está disponible en:

**URL**: `http://localhost:8081/swagger-ui/index.html`

### OpenAPI JSON

Especificación OpenAPI en formato JSON:

**URL**: `http://localhost:8081/v3/api-docs`

### Endpoints Principales

#### 👤 Usuarios
- `POST /api/usuarios` - Crear usuario
- `POST /api/usuarios/admin` - Crear administrador
- `GET /api/usuarios` - Listar todos los usuarios
- `GET /api/usuarios/{id}` - Obtener usuario por ID
- `GET /api/usuarios/email/{email}` - Obtener usuario por email
- `PUT /api/usuarios/update-password` - Actualizar contraseña
- `PUT /api/usuarios/update-user` - Actualizar datos del usuario
- `DELETE /api/usuarios/{id}` - Eliminar usuario

#### 🏢 Proveedores
- `POST /api/proveedores` - Crear proveedor
- `GET /api/proveedores` - Listar todos los proveedores
- `GET /api/proveedores/{id}` - Obtener proveedor por ID
- `PUT /api/proveedores/{id}` - Actualizar proveedor

#### 📍 Direcciones
- `POST /api/usuarios/{idUsuario}/direcciones` - Crear dirección
- `GET /api/usuarios/{idUsuario}/direcciones` - Listar direcciones del usuario
- `GET /api/usuarios/{idUsuario}/direcciones/principal` - Obtener dirección principal
- `PUT /api/usuarios/{idUsuario}/direcciones/{idDireccion}` - Actualizar dirección
- `DELETE /api/usuarios/{idUsuario}/direcciones/{idDireccion}` - Eliminar dirección


## 🗄️ Base de Datos

### Tablas

- **usuarios**: Almacena clientes y administradores
- **proveedores**: Almacena información de proveedores
- **direcciones**: Direcciones de envío asociadas a usuarios
