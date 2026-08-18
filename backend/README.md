# MitsuDrive Backend

Простой API сервер для тестирования приложения.

## Установка

cd backend
npm install

## Запуск

npm start
# или для разработки
npm run dev

## API Endpoints

### Health
- GET /health

### Auth
- POST /api/auth/register
- POST /api/auth/login

### Posts
- GET /api/posts
- POST /api/posts

### Chats
- GET /api/chats
- POST /api/chats
- GET /api/chats/:id/messages
- POST /api/chats/:id/messages

### Map
- GET /api/map/events
- POST /api/map/events

### SOS
- POST /api/sos
- POST /api/sos/:id/cancel
