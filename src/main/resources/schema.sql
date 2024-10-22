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
    description TEXT
);

create table recommendations (
    review_id INT PRIMARY KEY,
    app_id INT,
    helpful INT,
    funny INT,
    hours DECIMAL(10, 1),
    is_recommend BOOLEAN,
    foreign key (app_id) REFERENCES games(app_id)
)