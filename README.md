# 📚 BiblioSoft - Backend

Sistema de gestión de biblioteca para la Universidad del Valle - Servidor Backend

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Status](https://img.shields.io/badge/Status-En%20Desarrollo-yellow.svg)]()

## 📋 Descripción

API REST desarrollada con Spring Boot que proporciona servicios para la gestión de libros, usuarios y préstamos de una biblioteca universitaria. Incluye autenticación JWT, recuperación de contraseña por email y sistema de roles (Admin/Usuario).

---

## 🚀 Características Principales

### ✅ Módulos Implementados

- **🔐 Autenticación y Autorización**
  - Login y registro de usuarios
  - Tokens JWT (JSON Web Tokens)
  - Roles: `ADMIN` y `USER`
  - Cambio de contraseña seguro
  - Recuperación de contraseña por email

- **📖 Gestión de Libros**
  - CRUD completo de libros
  - Búsqueda por título, autor o editorial
  - Validaciones de datos

- **👥 Gestión de Usuarios**
  - Registro con validaciones
  - Búsqueda de usuarios por código
  - Perfiles de usuario

- **📧 Sistema de Emails**
  - Recuperación de contraseña
  - Envío de enlaces de restablecimiento
  - Integración con Gmail SMTP

- **📚 Sistema de Préstamos** *(En desarrollo)*
  - Modelo de préstamos definido
  - Relaciones entre usuarios y libros

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| **Java** | 21 | Lenguaje de programación |
| **Spring Boot** | 3.5.7 | Framework principal |
| **Spring Data JPA** | - | Persistencia de datos |
| **Spring Security** | - | Seguridad y autenticación |
| **JWT (jjwt)** | 0.11.5 | Tokens de autenticación |
| **MySQL** | 8.0+ | Base de datos |
| **Lombok** | - | Reducción de código boilerplate |
| **Spring Mail** | - | Envío de correos electrónicos |
| **Gradle** | 8.x | Gestión de dependencias |

---

## 📦 Estructura del Proyecto

```
BiblioSoft-Back/
├── src/main/java/co/edu/univalle/
│   ├── BiblioSoftwareApplication.java    # Punto de entrada
│   │
│   ├── Auth/                              # DTOs de autenticación
│   │   ├── AuthResponse.java
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── PasswordGenerate.java
│   │
│   ├── Controllers/                       # Controladores REST
│   │   ├── AuthController.java           # Login, registro, cambio de contraseña
│   │   ├── BookController.java           # CRUD de libros
│   │   ├── EmailController.java          # Recuperación de contraseña
│   │   └── UserController.java           # Gestión de usuarios
│   │
│   ├── Models/                            # Entidades JPA
│   │   ├── BookModel.java                # Modelo de libro
│   │   ├── UserModel.java                # Modelo de usuario
│   │   ├── PrestamoModel.java            # Modelo de préstamo
│   │   └── Role.java                     # Enum de roles
│   │
│   ├── Repositories/                      # Repositorios JPA
│   │   ├── BookRepository.java
│   │   ├── UserRepository.java
│   │   └── PrestamoRepository.java
│   │
│   ├── Services/                          # Lógica de negocio
│   │   ├── AuthService.java              # Autenticación
│   │   ├── BookService.java              # Lógica de libros
│   │   ├── UserService.java              # Lógica de usuarios
│   │   ├── JWTService.java               # Generación/validación JWT
│   │   └── EmailService.java             # Envío de emails
│   │
│   └── Security/                          # Configuración de seguridad
│       ├── SecurityConfig.java           # Configuración Spring Security
│       ├── ApplicationConfig.java        # Beans de configuración
│       ├── JWTAuthenticationFilter.java  # Filtro JWT
│       └── UserDetailsServiceImpl.java   # Servicio de autenticación
│
└── src/main/resources/
    └── application.properties             # Configuración de la aplicación
```

---

## 🔧 Requisitos Previos

- **Java JDK 21** o superior
- **MySQL 8.0** o superior
- **Gradle 8.x** (incluido con Gradle Wrapper)
- **Git**

---

## ⚙️ Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd BiblioSoft-Back
```

### 2. Configurar la base de datos MySQL

```sql
CREATE DATABASE bibliosoftdb;
```

### 3. Configurar `application.properties`

Edita `src/main/resources/application.properties`:

```properties
# Configuración de la base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/bibliosoftdb
spring.datasource.username=root
spring.datasource.password=tu_contraseña

# Configuración de email (Gmail)
spring.mail.username=tu_email@gmail.com
spring.mail.password=tu_app_password
```

> **Nota:** Para Gmail, necesitas generar una [contraseña de aplicación](https://support.google.com/accounts/answer/185833).

### 4. Compilar el proyecto

```bash
# Windows
.\gradlew build

# Linux/Mac
./gradlew build
```

### 5. Ejecutar la aplicación

```bash
# Windows
.\gradlew bootRun

# Linux/Mac
./gradlew bootRun
```

La API estará disponible en: `http://localhost:8080`

---

## 📡 Endpoints de la API

### 🔐 Autenticación (`/auth`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `POST` | `/auth/login` | Iniciar sesión | ❌ |
| `POST` | `/auth/register` | Registrar usuario | ❌ |
| `PUT` | `/auth/change-password` | Cambiar contraseña | ✅ |
| `POST` | `/auth/email/send` | Enviar email de recuperación | ❌ |
| `GET` | `/auth/validate-token` | Validar token de recuperación | ❌ |
| `POST` | `/auth/reset-password` | Restablecer contraseña | ❌ |

### 📚 Libros (`/api/book`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/book/allBooks` | Listar todos los libros | ✅ |
| `POST` | `/api/book/save` | Crear nuevo libro | ✅ Admin |
| `PUT` | `/api/book/edit` | Editar libro | ✅ Admin |
| `DELETE` | `/api/book/deleteBook/{id}` | Eliminar libro | ✅ Admin |
| `GET` | `/api/book/search?query=` | Buscar libros | ✅ |
| `GET` | `/api/book/ping` | Health check | ❌ |

### 👥 Usuarios (`/api/user`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/user/code/{code}` | Buscar usuario por código | ✅ Admin |

---

## 🔑 Autenticación JWT

### Ejemplo de Login

**Request:**
```json
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "Admin123!"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

### Usar el token

Incluye el token en el header `Authorization`:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📊 Modelos de Datos

### BookModel
```java
{
  "id": Long,
  "titulo": String (max 50),
  "autor": String (max 70),
  "anio": int,
  "editorial": String (max 70)
}
```

### UserModel
```java
{
  "id": int,
  "firstname": String,
  "lastname": String,
  "username": String (unique),
  "email": String (unique),
  "code": String (unique, max 20),
  "password": String (encrypted),
  "role": "ADMIN" | "USER"
}
```

### PrestamoModel *(En desarrollo)*
```java
{
  "id": Long,
  "usuarioCode": String,
  "usuario": UserModel,
  "libro": BookModel,
  "fechaPrestamo": LocalDate,
  "fechaDevolucion": LocalDate
}
```

---

## 🔒 Validaciones de Seguridad

### Contraseña
- Mínimo 8 caracteres, máximo 30
- Al menos 1 letra mayúscula
- Al menos 1 letra minúscula
- Al menos 1 número
- Al menos 1 carácter especial: `!@#$%^&*.,;:?¡¿_+-=`

**Regex:**
```
^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*.,;:?¡¿_+\-=]).{8,30}$
```

### Username
- Máximo 10 caracteres

---

## 🧪 Testing

```bash
# Ejecutar tests
.\gradlew test

# Ejecutar tests con reporte
.\gradlew test --info
```

---

## 🌐 CORS

El backend está configurado para aceptar peticiones desde:
- `http://localhost:5173` (Frontend en desarrollo)

Para modificar, edita las anotaciones `@CrossOrigin` en los controladores.

---

## 📝 Variables de Entorno (Producción)

Para producción, usa variables de entorno en lugar de `application.properties`:

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/db
export SPRING_DATASOURCE_USERNAME=usuario
export SPRING_DATASOURCE_PASSWORD=contraseña
export SPRING_MAIL_USERNAME=email@gmail.com
export SPRING_MAIL_PASSWORD=app_password
export JWT_SECRET=tu_secreto_jwt
```

---

## 🐛 Debugging

### Modo Debug
```bash
.\gradlew bootRun --debug-jvm
```

### Ver SQL Queries
Ya está habilitado en `application.properties`:
```properties
spring.jpa.show-sql=true
```

---

## 📚 Próximas Funcionalidades

- [ ] Sistema completo de préstamos
- [ ] Historial de préstamos
- [ ] Notificaciones de devolución
- [ ] Dashboard de estadísticas
- [ ] Exportación de reportes
- [ ] Sistema de multas
- [ ] Reserva de libros
- [ ] API de búsqueda avanzada

---

## 👥 Autores

**Equipo de Desarrollo - Universidad del Valle**
- Jean Lopez
- Sebastian Santacruz
- Camilo Olave
- Angel Lopez

---

## 📄 Licencia

Este proyecto está en desarrollo como parte de un proyecto académico de la Universidad del Valle.

---

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📞 Soporte

Para reportar bugs o solicitar features, abre un issue en el repositorio.

---

**Desarrollado con ❤️ para la Universidad del Valle** 🎓
