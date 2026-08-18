const express = require('express');
const http = require('http');
const socketIO = require('socket.io');
const cors = require('cors');

const app = express();
const server = http.createServer(app);
const io = socketIO(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    }
});

const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Хранилища данных (in-memory)
const users = new Map();
const posts = new Map();
const chats = new Map();
const messages = new Map();
const mapEvents = new Map();
const sosAlerts = new Map();
const onlineUsers = new Map();

// ==================== REST API ====================

// Health check
app.get('/health', (req, res) => {
    res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

// Auth
app.post('/api/auth/register', (req, res) => {
    const { phone, username, password } = req.body;
    
    if (!phone || !username || !password) {
        return res.status(400).json({ success: false, error: { message: 'Missing fields' } });
    }
    
    const userId = `user_${Date.now()}`;
    users.set(userId, {
        id: userId,
        phone,
        username,
        password,
        avatarUrl: null,
        rating: 0,
        carsCount: 0,
        createdAt: new Date().toISOString()
    });
    
    res.json({
        success: true,
        data: {
            userId,
            username,
            token: `token_${userId}`
        }
    });
});

app.post('/api/auth/login', (req, res) => {
    const { phone, password } = req.body;
    
    // Простая проверка
    const user = Array.from(users.values()).find(u => u.phone === phone);
    
    if (!user || user.password !== password) {
        return res.status(401).json({ success: false, error: { message: 'Invalid credentials' } });
    }
    
    res.json({
        success: true,
        data: {
            userId: user.id,
            username: user.username,
            token: `token_${user.id}`
        }
    });
});

// Posts (Feed)
app.get('/api/posts', (req, res) => {
    const postList = Array.from(posts.values())
        .sort((a, b) => b.createdAt.localeCompare(a.createdAt));
    
    res.json({ success: true, data: { items: postList, hasMore: false } });
});

app.post('/api/posts', (req, res) => {
    const { content, postType, lat, lng } = req.body;
    const userId = req.headers['user-id'] || 'anonymous';
    
    const postId = `post_${Date.now()}`;
    const post = {
        id: postId,
        userId,
        username: users.get(userId)?.username || 'Anonymous',
        content,
        postType: postType || 'NORMAL',
        lat,
        lng,
        likesCount: 0,
        commentsCount: 0,
        createdAt: new Date().toISOString()
    };
    
    posts.set(postId, post);
    res.json({ success: true, data: post });
});

// Chats
app.get('/api/chats', (req, res) => {
    const chatList = Array.from(chats.values())
        .sort((a, b) => b.updatedAt.localeCompare(a.updatedAt));
    
    res.json({ success: true, data: { items: chatList } });
});

app.post('/api/chats', (req, res) => {
    const { type, participantIds, title } = req.body;
    
    const chatId = `chat_${Date.now()}`;
    const chat = {
        id: chatId,
        type: type || 'GROUP',
        title: title || 'Chat',
        participants: participantIds || [],
        lastMessage: null,
        unreadCount: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
    };
    
    chats.set(chatId, chat);
    messages.set(chatId, []);
    
    res.json({ success: true, data: chat });
});

app.get('/api/chats/:chatId/messages', (req, res) => {
    const { chatId } = req.params;
    const chatMessages = messages.get(chatId) || [];
    
    res.json({ success: true, data: { items: chatMessages } });
});

app.post('/api/chats/:chatId/messages', (req, res) => {
    const { chatId } = req.params;
    const { content, messageType } = req.body;
    const userId = req.headers['user-id'] || 'anonymous';
    
    const messageId = `msg_${Date.now()}`;
    const message = {
        id: messageId,
        chatId,
        userId,
        senderName: users.get(userId)?.username || 'Anonymous',
        content,
        messageType: messageType || 'TEXT',
        createdAt: new Date().toISOString(),
        status: 'SENT'
    };
    
    const chatMessages = messages.get(chatId) || [];
    chatMessages.push(message);
    messages.set(chatId, chatMessages);
    
    // Обновляем чат
    const chat = chats.get(chatId);
    if (chat) {
        chat.lastMessage = message;
        chat.updatedAt = message.createdAt;
        chats.set(chatId, chat);
    }
    
    // Отправляем через WebSocket
    io.to(`chat_${chatId}`).emit('message', message);
    
    res.json({ success: true, data: message });
});

// Map Events
app.get('/api/map/events', (req, res) => {
    const eventList = Array.from(mapEvents.values())
        .filter(e => new Date(e.expiresAt) > new Date());
    
    res.json({ success: true, data: eventList });
});

app.post('/api/map/events', (req, res) => {
    const { eventType, lat, lng, description } = req.body;
    const userId = req.headers['user-id'] || 'anonymous';
    
    const eventId = `event_${Date.now()}`;
    const event = {
        id: eventId,
        eventType,
        lat,
        lng,
        description,
        userId,
        username: users.get(userId)?.username || 'Anonymous',
        createdAt: new Date().toISOString(),
        expiresAt: new Date(Date.now() + 4 * 60 * 60 * 1000).toISOString(),
        confirmations: 1
    };
    
    mapEvents.set(eventId, event);
    
    // Уведомляем всех через WebSocket
    io.emit('map_event', event);
    
    res.json({ success: true, data: event });
});

// SOS
app.post('/api/sos', (req, res) => {
    const { lat, lng, message } = req.body;
    const userId = req.headers['user-id'] || 'anonymous';
    
    const sosId = `sos_${Date.now()}`;
    const sos = {
        id: sosId,
        userId,
        username: users.get(userId)?.username || 'Anonymous',
        lat,
        lng,
        message,
        createdAt: new Date().toISOString(),
        status: 'ACTIVE'
    };
    
    sosAlerts.set(sosId, sos);
    
    // Отправляем всем через WebSocket (высокий приоритет)
    io.emit('sos_alert', sos);
    
    res.json({
        success: true,
        data: {
            sosId,
            notifiedUsers: onlineUsers.size,
            timestamp: sos.createdAt
        }
    });
});

app.post('/api/sos/:sosId/cancel', (req, res) => {
    const { sosId } = req.params;
    sosAlerts.delete(sosId);
    res.json({ success: true });
});

// ==================== WebSocket ====================

io.on('connection', (socket) => {
    console.log(`User connected: ${socket.id}`);
    
    // Регистрация пользователя
    socket.on('register', (data) => {
        const userId = data.userId;
        onlineUsers.set(userId, socket.id);
        socket.userId = userId;
        
        // Уведомляем о подключении
        io.emit('presence', { userId, isOnline: true });
    });
    
    // Присоединение к чату
    socket.on('join_chat', (chatId) => {
        socket.join(`chat_${chatId}`);
    });
    
    // Покидание чата
    socket.on('leave_chat', (chatId) => {
        socket.leave(`chat_${chatId}`);
    });
    
    // Отправка сообщения
    socket.on('send_message', (data) => {
        const { chatId, content, messageType } = data;
        const userId = socket.userId || 'anonymous';
        
        const messageId = `msg_${Date.now()}`;
        const message = {
            id: messageId,
            chatId,
            userId,
            senderName: users.get(userId)?.username || 'Anonymous',
            content,
            messageType: messageType || 'TEXT',
            createdAt: new Date().toISOString(),
            status: 'DELIVERED'
        };
        
        const chatMessages = messages.get(chatId) || [];
        chatMessages.push(message);
        messages.set(chatId, chatMessages);
        
        io.to(`chat_${chatId}`).emit('message', message);
    });
    
    // Обновление местоположения
    socket.on('location_update', (data) => {
        const userId = socket.userId;
        if (userId) {
            onlineUsers.set(userId, socket.id);
            socket.lat = data.lat;
            socket.lng = data.lng;
        }
    });
    
    // Отключение
    socket.on('disconnect', () => {
        if (socket.userId) {
            onlineUsers.delete(socket.userId);
            io.emit('presence', { userId: socket.userId, isOnline: false });
        }
        console.log(`User disconnected: ${socket.id}`);
    });
});

// Запуск сервера
server.listen(PORT, () => {
    console.log(`MitsuDrive API server running on port ${PORT}`);
    console.log(`WebSocket server running on ws://localhost:${PORT}`);
});
