# 🌿 EcoMarket - API de Gestión de Productos

API REST para la gestión de productos e inventario del sistema **EcoMarket**, desarrollada con **Spring Boot** y conectada a una base de datos **MySQL**.

---

## 📋 Tabla de Contenidos

- [✨ Características](#-características)
- [🛠 Tecnologías](#-tecnologías)
- [📋 Prerrequisitos](#-prerrequisitos)
- [🚀 Instalación](#-instalación)
- [⚙️ Configuración](#️-configuración)
- [📡 API Endpoints](#-api-endpoints)
- [🔧 Ejemplos de Uso](#-ejemplos-de-uso)
- [📚 Documentación Swagger](#-documentación-swagger)
- [🌐 API Gateway](#-api-gateway)


---

## ✨ Características

- ✅ **Gestión de Productos:** CRUD completo con validaciones.
- 📦 **Control de Inventario:** Seguimiento de stock con estados automáticos.
- 🔒 **Validaciones de Negocio:** Códigos únicos y control de stock mínimo.
- 📊 **Estados Automáticos:** Cálculo automático (`DISPONIBLE`, `BAJO_STOCK`, `AGOTADO`).
- 🌐 **API RESTful:** Endpoints estandarizados con códigos de estado HTTP.
- 📘 **Documentación OpenAPI:** Swagger UI integrado.
- 🕒 **Auditoría Temporal:** Registro automático de creación y actualización.

---

## 🛠 Tecnologías

- **Backend:** Spring Boot 3.4.6, Spring Data JPA, Spring Web  
- **Base de Datos:** MySQL 8.0  
- **Herramientas:** Maven, Lombok, Spring Boot Actuator  
- **Documentación:** Springdoc OpenAPI (Swagger)  
- **Java:** OpenJDK 17

---

## 📋 Prerrequisitos

Asegúrate de tener instalado:

- Java 17 o superior  
- Maven 3.6 o superior  
- MySQL 8.0  
- Git  

---

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/JorgeToledoIporre/producto-ms.git
cd producto-ms
```
### 2. Compilar el proyecto
```bash
mvn clean package spring-boot:repackage -DskipTests
```
### 3. ejecutar el proyecto
```bash
mvn clean package spring-boot:repackage -DskipTests
```
## ⚙️ Configuración local

Para desarrollo local, puedes sobrescribir las propiedades creando un archivo llamado application-local.properties dentro del directorio src/main/resources/:

- server.port=8081
- spring.datasource.url=jdbc:mysql://localhost:3306/ecomarket_local
- spring.datasource.username=tu_usuario
- spring.datasource.password=tu_password

Asegúrate de tener la base de datos ecomarket_local creada en tu instancia local de MySQL.

## 📖 API Endpoints

Productos
| Método | Endpoint                        | Descripción                  |
|--------|----------------------------------|------------------------------|
| GET    | `/api/v1/productos`             | Listar todos los productos   |
| GET    | `/api/v1/productos/{id}`        | Obtener producto por ID      |
| POST   | `/api/v1/productos`             | Crear nuevo producto         |
| PUT    | `/api/v1/productos/{id}`        | Actualizar producto          |
| DELETE | `/api/v1/productos/{id}`        | Eliminar producto            |

Inventario
| Método | Endpoint                         | Descripción                       |
|--------|----------------------------------|-----------------------------------|
| GET    | `/api/v1/inventario`            | Listar todo el inventario         |
| GET    | `/api/v1/inventario/{id}`       | Obtener inventario por ID         |
| POST   | `/api/v1/inventario`            | Crear registro de inventario      |
| PUT    | `/api/v1/inventario/{id}`       | Actualizar inventario             |
| DELETE | `/api/v1/inventario/{id}`       | Eliminar registro de inventario   |

## 🔧 Ejemplos de Uso

### Crear un Producto

```bash
curl -X POST http://localhost:8081/api/v1/productos \
  -H "Content-Type: application/json" \
  -d '{
    "codigo": "FRUTA001",
    "nombre": "Manzana Roja",
    "descripcion": "Manzanas rojas orgánicas",
    "precio": 2.50,
    "categoria": "Frutas",
    "activo": true
  }'
```
### Crear Registro de Inventario

```bash
curl -X POST http://localhost:8081/api/v1/inventario \
  -H "Content-Type: application/json" \
  -d '{
    "producto": {"id": 1},
    "cantidad": 100,
    "cantidadMinima": 20,
    "ubicacion": "Almacén Principal"
  }'
```
### Listar todos los productos
```bash
curl http://localhost:8081/api/v1/productos
```
### Obtener producto específico
```bash
curl http://localhost:8081/api/v1/productos/1
```

## 📚 Documentación Swagger
Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación interactiva en:

[🔗 Ver Swagger UI](http://localhost:8081/swagger-ui.html)

## 🌐 API Gateway
Este microservicio puede ser gestionado a través de una API Gateway desarrollada con Spring Cloud Gateway, la cual centraliza el acceso a los distintos servicios del sistema EcoMarket.

🔗 Repositorio de la API Gateway:
👉 [github.com/JorgeToledoIporre/api-gateway](https://github.com/JorgeToledoIporre/ApiGatewayEcoMarket)


