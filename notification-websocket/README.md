# Notification WebSocket Service

Este servicio expone un endpoint WebSocket para enviar notificaciones en tiempo real a los clientes conectados.

- Puerto: 8083
- Endpoints:
  - WebSocket: `/ws/notifications`
  - API REST para enviar notificaciones: `POST /api/notify`

Configura los orígenes permitidos en `WebSocketConfig` y en `application.yml`.
