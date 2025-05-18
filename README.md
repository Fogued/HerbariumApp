# HerbariumApp

Aplicación Android desarrollada en Kotlin que permite gestionar usuarios, capturar fotos, completar metadatos personalizados, y almacenarlos en la nube utilizando Firebase. También incluye una galería interactiva con diferentes vistas.

---

## Características

- **Autenticación de usuarios**  
  Registro, inicio de sesión y cierre de sesión con Firebase Authentication.

- **Captura de fotos**  
  Toma fotos desde la cámara con `CameraX`.

- **Formulario de metadatos**  
  Antes de guardar la imagen, el usuario puede ingresar:
  - Nombre de la imagen
  - Ubicación (manual o automática si usas GPS)
  - Descripción

- **Almacenamiento en la nube**  
  Las fotos se suben a Firebase Storage y los metadatos a Firebase Realtime Database.

- **Galería de fotos**  
  Visualiza todas las imágenes subidas por el usuario (o todos, según configuración).

- **Cambio de vista**  
  Alterna entre vista de lista 📃 y vista de cuadrícula 🟦 en la galería.

---

## Tecnologías Usadas

- Kotlin
- Android Jetpack (ViewModel, LiveData, Navigation)
- Firebase:
  - Authentication
  - Realtime Database
  - Storage
- CameraX
- Glide (para mostrar imágenes)

---

## Futuros avances

- Agregar soporte para iniciar sesión con proveedores externos (Google, Facebook, Apple)
- Permitir compartir fotos directamente desde la galería
- Incorporar filtros o edición básica de imágenes antes de subirlas
- Soporte para comentarios y reacciones en las fotos
- Implementación de sistema de notificaciones para interacciones
- Integración de etiquetas (tags) para clasificar fotos
- Buscador por nombre, ubicación o descripción de la imagen

