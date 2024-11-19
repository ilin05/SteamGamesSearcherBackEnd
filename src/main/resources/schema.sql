DROP DATABASE IF EXISTS steam_games;
CREATE DATABASE steam_games;
USE steam_games;

create table users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table games (
    app_id INT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    released_date VARCHAR(50),
    win BOOLEAN,
    mac BOOLEAN,
    linux BOOLEAN,
    price DECIMAL(10, 2),
    tags TEXT,
    support_language TEXT,
    website VARCHAR(255),
    header_image VARCHAR(255),
    recommendations INT,
    positive INT,
    negative INT,
    estimated_owners INT,
    screenshots TEXT,
    description TEXT,
    movies TEXT
);

create table search_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    search_text VARCHAR(255) NOT NULL,
    search_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    foreign key (user_id) REFERENCES users(id)
);

create table favorites (
   id INT AUTO_INCREMENT PRIMARY KEY,
   user_id INT,
   app_id INT,
   foreign key (user_id) REFERENCES users(id),
   foreign key (app_id) REFERENCES games(app_id)
);