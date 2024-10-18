package edu.school21.sockets.services;

public enum ChatState {
    WELCOME,          // Приветственное сообщение
    MAIN_MENU,        // Главное меню после входа в систему (создание комнаты, выбор комнаты, выход)
    ROOMS_LIST,      // Выбор комнаты
    ROOM_CHAT,        // Чат внутри комнаты
    EXIT
}