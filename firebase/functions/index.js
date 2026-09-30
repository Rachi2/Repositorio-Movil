const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const { initializeApp } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");
const { getMessaging } = require("firebase-admin/messaging");
const logger = require("firebase-functions/logger");

initializeApp();

// Se ejecuta sola cada vez que se guarda un mensaje nuevo en cualquier chat
exports.notifyNewMessage = onDocumentCreated(
  { document: "chats/{conversationId}/messages/{messageId}", region: "us-east1" },
  async (event) => {
    const message = event.data?.data();
    if (!message) return;

    // El id del chat es "idA_idB": el destinatario es el que NO envió el mensaje
    const senderId = message.senderId;
    const receiverId = event.params.conversationId
      .split("_")
      .find((id) => id !== senderId);
    if (!receiverId) return;

    // Buscar el token del teléfono del destinatario
    const receiverDoc = await getFirestore().collection("users").doc(receiverId).get();
    const token = receiverDoc.get("fcmToken");
    if (!token) {
      logger.info(`El usuario ${receiverId} no tiene token de notificaciones`);
      return;
    }

    // Si es una foto, no hay texto: se muestra "📷 Imagen"
    const body = message.imageUrl ? "📷 Imagen" : (message.text || "");

    try {
      // Mensaje de solo datos: así la app siempre decide cómo mostrarlo
      await getMessaging().send({
        fid: token,
        data: {
          senderId: senderId,
          senderName: message.senderName || "Nuevo mensaje",
          body: body,
        },
        android: { priority: "high" },
      });
      logger.info(`Notificación enviada a ${receiverId}`);
    } catch (error) {
      logger.error("No se pudo enviar la notificación", error);
    }
  }
);