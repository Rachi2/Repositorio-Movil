# Kairo

Aplicación de chat para Android con mensajería en tiempo real, desarrollada con Java y Firebase como proyecto de la asignatura **ICC-451 · Desarrollo de Aplicaciones Móviles** (PUCMM).

**Autores**
- Yuji Yamaki
- [Nombre del compañero]

---

## Funcionalidades

### Requerimientos del proyecto
- **Registro e inicio de sesión** con correo y contraseña (Firebase Authentication), con validación de campos y mensajes de error claros.
- **Sesión persistente**: si el usuario no cierra sesión, la app entra directo a la lista de chats.
- **Mensajería en tiempo real** con Firestore: cada mensaje muestra el nombre de quien lo envió y la hora, agrupados por día ("Hoy", "Ayer", el día de la semana o la fecha completa). No se pueden enviar mensajes vacíos.
- **Envío de imágenes** desde la galería, guardadas en Firebase Storage.
- **Notificaciones push** con Firebase Cloud Messaging y Cloud Functions:
  - Con la app cerrada o en segundo plano: notificación del sistema.
  - Con la app abierta: un aviso propio dentro de la app.
  - Si el usuario ya tiene abierto ese chat, no se notifica.
  - Al tocar la notificación se abre el chat de quien escribió.
  - Al cerrar sesión, el dispositivo deja de recibir notificaciones de esa cuenta.

### Mejoras adicionales
- **Foto de perfil** con recorte circular.
- **Búsqueda** de usuarios por nombre o correo.
- **Visor de imágenes** a pantalla completa con zoom.
- **Compresión de imágenes** antes de subirlas (de ~3 MB a ~200 KB).

---

## Tecnologías

| Área | Tecnología |
|---|---|
| Lenguaje | Java |
| Interfaz | XML Views, RecyclerView, Material Components |
| Arquitectura | MVVM con capas `data`, `domain` y `presentation` |
| Autenticación | Firebase Authentication |
| Base de datos | Cloud Firestore |
| Archivos | Firebase Storage |
| Notificaciones | Firebase Cloud Messaging + Cloud Functions (Node.js) |
| Librerías | Glide (imágenes), PhotoView (zoom), Android Image Cropper (recorte) |

**Requisitos de compilación:** Android Studio actualizado, `minSdk 24`, `targetSdk 37`.

---

## Arquitectura

```
Proyecto 1/app/src/main/java/com/example/proyecto1/
├── data/
│   ├── local/        Acceso a datos del teléfono (compresión de imágenes)
│   ├── model/        DTOs: los datos tal como se guardan en Firestore
│   ├── remote/       Único lugar que usa Firebase (Auth, Firestore, Storage, FCM)
│   └── repository/   Implementaciones de los repositorios
├── domain/
│   ├── model/        Modelos de la app (User, Message)
│   ├── repository/   Interfaces de los repositorios
│   └── usecase/      Casos de uso (una acción por clase)
└── presentation/
    ├── view/         Activities, adapters y utilidades de interfaz
    └── viewmodel/    ViewModels con LiveData
```

Flujo de una acción, por ejemplo enviar un mensaje:

```
ChatActivity → ChatViewModel → SendMessageUseCase → ChatRepository → FirestoreChatSource → Firestore
```

Las Activities no acceden a Firebase directamente: todo pasa por el ViewModel, los casos de uso y los repositorios.

---

## Cómo ejecutar el proyecto

### 1. Archivo `google-services.json`
Por seguridad, este archivo **no está en el repositorio**. Pídelo a los autores y colócalo en:

```
Proyecto 1/app/google-services.json
```

Sin este archivo el proyecto no compila.

### 2. Abrir y correr la app
1. En Android Studio, abre la carpeta **`Proyecto 1`** (no la raíz del repositorio).
2. Espera a que termine la sincronización de Gradle.
3. Elige un teléfono o emulador **con Google Play** (necesario para las notificaciones) y pulsa **Run**.

### 3. Cloud Function de notificaciones (solo si se modifica)
La función ya está publicada en el proyecto de Firebase y funciona sin hacer nada. Solo hay que volver a publicarla si se cambia `firebase/functions/index.js`:

```bash
npm install -g firebase-tools
firebase login
cd firebase/functions
npm install
cd ..
firebase deploy --only functions
```

Requiere Node.js y el plan Blaze de Firebase.

---

## Configuración de Firebase

Para usar el proyecto con un Firebase propio:

1. **Authentication**: activar el inicio de sesión con correo y contraseña.
2. **Firestore** y **Storage**: crearlos en la misma región que la Cloud Function (`us-east1`).
3. **Manifest**: el proyecto ya incluye el `meta-data` `firebase_messaging_installation_id_enabled`, necesario para el registro de notificaciones.
4. **Reglas de Firestore**:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null;
      allow create, update: if request.auth != null && request.auth.uid == userId;
    }
    match /chats/{conversationId}/messages/{messageId} {
      allow read: if request.auth != null
                  && request.auth.uid in conversationId.split('_');
      allow create: if request.auth != null
                    && request.auth.uid in conversationId.split('_')
                    && request.resource.data.senderId == request.auth.uid;
    }
  }
}
```

5. **Reglas de Storage**:

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /chat_images/{conversationId}/{fileName} {
      allow read: if request.auth != null
                  && request.auth.uid in conversationId.split('_');
      allow write: if request.auth != null
                   && request.auth.uid in conversationId.split('_')
                   && request.resource.size < 5 * 1024 * 1024
                   && request.resource.contentType.matches('image/.*');
    }
    match /profile_images/{userId}/{fileName} {
      allow read: if request.auth != null;
      allow write: if request.auth != null
                   && request.auth.uid == userId
                   && request.resource.size < 5 * 1024 * 1024
                   && request.resource.contentType.matches('image/.*');
    }
  }
}
```

---

## Estructura de datos

```
users/{uid}
  ├── id, name, email
  ├── photoUrl     enlace de la foto de perfil (opcional)
  └── fcmToken     identificador del dispositivo para notificaciones

chats/{uidA_uidB}/messages/{messageId}
  ├── id, senderId, senderName
  ├── text         texto del mensaje (null si es una imagen)
  ├── imageUrl     enlace de la imagen en Storage (null si es texto)
  └── timestamp    fecha y hora en milisegundos
```

El id de cada conversación se forma con los dos `uid` en orden alfabético, así los dos usuarios llegan siempre a la misma conversación.
