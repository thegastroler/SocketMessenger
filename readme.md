# Чат-приложение на основе сокетов

## Описание проекта

Данный проект представляет собой чат-приложение, построенное на архитектуре клиент-сервер 
с использованием сокет-соединений. Приложение разработано с использованием Java, 
Spring Framework и JDBC, обеспечивает функционал регистрации пользователей, обмена 
сообщениями между пользователями, а также поддержку чатов (комнат).

Проект состоит из двух Maven-проектов:
1. **Серверное приложение** — отвечает за обработку подключений клиентов и хранение данных.
2. **Клиентское приложение** — взаимодействует с пользователем, отправляет запросы на сервер и получает ответы.

Приложение поддерживает следующие функции:
- Регистрация пользователей;
- Авторизация пользователей;
- Обмен сообщениями между пользователями;
- Создание и выбор чат-комнат;
- История сообщений (последние 30 сообщений комнаты).

## Архитектура

### Серверное приложение
Серверная часть построена на Spring Framework с использованием таких компонентов, как 
`HikariCP` для подключения к базе данных и `JdbcTemplate` для выполнения SQL-запросов. 
Также используется BCrypt для хеширования паролей.

#### Структура проекта
```
SocketServer
├── src
│   └── main
│       ├── java
│       │    └── edu.school21.sockets
│       │       ├── app
│       │       │   └── Main.java                     # Главный класс сервера
│       │       ├── config
│       │       │   └── ApplicationConfig.java        # Конфигурационный класс
│       │       ├── models
│       │       │   ├── Message.java                  # Модель сообщений
│       │       │   ├── Room.java                     # Модель комнаты
│       │       │   └── User.java                     # Модель пользователя
│       │       ├── repositories
│       │       │   ├── CrudRepository.java           # Общий интерфейс репозитория
│       │       │   ├── MessagesRepository.java       # Репозиторий сообщений
│       │       │   ├── MessagesRepositoryImpl.java   # Реализация репозитория
│       │       │   ├── RoomsRepository.java          # Репозиторий комнат
│       │       │   ├── RoomsRepositoryImpl.java      # Реализация репозитория
│       │       │   ├── UsersRepository.java          # Репозиторий пользователей
│       │       │   └── UsersRepositoryImpl.java      # Реализация репозитория
│       │       ├── server
│       │       │   ├── ClientHandler.java            # Класс взаимодействия с пользователем
│       │       │   ├── CommandProcessor.java         # Класс обработки действий пользователя
│       │       │   └── Server.java                   # Главный класс сервера
│       │       ├── services
│       │       │   ├── ChatState.java                # Сервис чата
│       │       │   ├── MessagesService.java          # Сервис сообщений
│       │       │   ├── RoomsService.java             # Сервис комнат
│       │       │   ├── UsersService.java             # Интерфейс сервиса пользователей
│       │       │   └── UsersServiceImpl.java         # Реализация сервиса
│       └── resources
│           ├── db.properties                         # Настройки подключения к БД
│           └── schema.sql                            # Конфигурация БД
└── pom.xml                                           # Maven конфигурация
```

### Клиентское приложение
Клиентское приложение — это отдельный Maven-проект, который отправляет команды на сервер 
через сокет-соединение и отображает ответы сервера пользователю.

## Функциональные возможности

### Чат-комнаты

Чат-приложение поддерживает функционал комнат чатов, где пользователи могут создавать и 
выбирать комнаты, обмениваться сообщениями и просматривать историю последних 30 сообщений 
при входе в комнату.

Функциональные возможности:
1. Регистрация и авторизация пользователя;
2. Создание чат-комнаты;
3. Выбор существующей чат-комнаты;
4. Отправка сообщений в выбранную комнату;
5. История сообщений — при входе в комнату отображаются последние 30 сообщений;
6. Выход из комнаты.

Пример работы клиента:
```
Hello from Server!
1. signIn
2. SignUp
3. Exit
> 1
Enter username:
> Marsel
Enter password:
> qwerty007
> Authentication success!
1.	Create room
2.	Choose room
3.	Exit
> 2
Rooms:
1. First Room
2. SimpleRoom
3. JavaRoom
4. Exit
> 3
Java Room ---
JavaMan: Hello!
> Hello!
Marsel: Hello!
> Exit
Rooms:
1. First Room
2. SimpleRoom
3. JavaRoom
4. Exit
> 4
1.	Create room
2.	Choose room
3.	Exit
> 3
You have left the chat.
```

**Требования к реализации**:
- Сервер обрабатывает команды пользователей в отдельном потоке для каждого пользователя.
- Для каждой комнаты хранится история сообщений.

## Технологии

- **Java 11**
- **Spring Framework**
- **JDBC (JdbcTemplate)**
- **HikariCP** — для управления пулом соединений с базой данных.
- **BCryptPasswordEncoder** — для безопасного хранения паролей.
- **Maven** — для управления зависимостями и сборки проекта.

## Как запустить проект

1. **Сборка серверного приложения**:
    ```
    cd SocketServer
    mvn clean install
    java -jar target/SocketServer-1.0-SNAPSHOT-jar-with-dependencies.jar --port=8081
    ```

2. **Сборка клиентского приложения**:
    ```
    cd SocketClient
    mvn clean install
    java -jar target/SocketClient-1.0-SNAPSHOT-jar-with-dependencies.jar --port=8081
    ```

## Заключение

Этот проект демонстрирует принципы работы с сокетами, многопользовательские системы и 
концепцию комнат чатов. В проекте используются современные практики хранения данных и 
безопасного обращения с учетными данными пользователей.