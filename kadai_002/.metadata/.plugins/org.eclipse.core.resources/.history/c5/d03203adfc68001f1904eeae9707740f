-- 飲食店（Restaurant）テーブル --
CREATE TABLE IF NOT EXISTS restaurants (
   id INT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR (255) NOT NULL,
   image_name VARCHAR (255),
   description TEXT,
   price INT NOT NULL,
   postal_code VARCHAR (50) NOT NULL,
   address VARCHAR (255) NOT NULL,
   phone_number VARCHAR (50) NOT NULL,
   category VARCHAR (100) NOT NULL,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
-- ロール（role）テーブル --
CREATE TABLE IF NOT EXISTS roles (
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR (50) NOT NULL
);

-- 会員（user）テーブル --
CREATE TABLE IF NOT EXISTS users (
     id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(50) NOT NULL,
     furigana VARCHAR(50) NOT NULL,
     postal_code VARCHAR(50) NOT NULL,
     address VARCHAR(255) NOT NULL,
     phone_number VARCHAR(50) NOT NULL,
     email VARCHAR(255) NOT NULL UNIQUE,
     password VARCHAR(255) NOT NULL,    
     role_id INT NOT NULL,
     enabled BOOLEAN NOT NULL,
     subscription_status VARCHAR(20) DEFAULT 'INACTIVE', -- サブスクリプションの状態を表す新しいカラムを追加
     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,    
     FOREIGN KEY (role_id) REFERENCES roles (id)
);
 
 -- 認証（verification_tokens）テーブル --
 CREATE TABLE IF NOT EXISTS verification_tokens (
     id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
     user_id INT NOT NULL UNIQUE,
     token VARCHAR(255) NOT NULL,        
     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     FOREIGN KEY (user_id) REFERENCES users (id) 
 );
 
 -- 予約（reservations）テーブル --
 CREATE TABLE IF NOT EXISTS reservations (
     id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
     restaurant_id INT NOT NULL,
     user_id INT NOT NULL,
     reservation_date DATE NOT NULL,
     number_of_people INT NOT NULL,
     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     FOREIGN KEY (restaurant_id) REFERENCES restaurant(id),
     FOREIGN KEY (user_id) REFERENCES users(id)
);

-- レビュー（reviews）テーブル --
CREATE TABLE IF NOT EXISTS reviews (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    user_id INT NOT NULL,
    score INT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE (restaurant_id, user_id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- お気に入り（favorites）テーブル --
CREATE TABLE IF NOT EXISTS favorites (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    user_id INT NOT NULL,    
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE (restaurant_id, user_id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
    FOREIGN KEY (user_id) REFERENCES users (id)  
);