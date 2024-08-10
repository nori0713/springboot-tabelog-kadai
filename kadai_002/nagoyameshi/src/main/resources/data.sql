-- 既存のデータを削除
DELETE FROM restaurant;
DELETE FROM users;
DELETE FROM roles;

-- 飲食店データ --
INSERT INTO restaurant (id, name, image_name, description, price, postal_code, address, phone_number, category, created_at, updated_at) VALUES 
(1, '寿司屋', '001.jpg', '町で一番の寿司', 3000, '123-4567', '名古屋市寿司町123', '052-123-4567', '和食', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'ラーメンハウス', '002.jpg', '美味しいラーメンとサイドメニュー', 1200, '123-4567', '名古屋市ラーメン通り456', '052-123-4568', '和食', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'ピザワールド', '003.jpg', '本格的なイタリアンピザ', 2500, '123-4567', '名古屋市ピザ大通り789', '052-123-4569', 'イタリアン', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'バーガータウン', '004.jpg', 'ジューシーなバーガーとフライドポテト', 1500, '123-4567', '名古屋市バーガー通り321', '052-123-4570', 'アメリカン', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'カレーハウス', '005.jpg', 'スパイシーで風味豊かなカレー', 1800, '123-4567', '名古屋市カレー通り654', '052-123-4571', 'インド料理', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'BBQヘブン', '006.jpg', 'スモーキーで柔らかいBBQ', 3500, '123-4567', '名古屋市BBQ通り987', '052-123-4572', 'アメリカン', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'パスタパレス', '007.jpg', '新鮮で美味しいパスタ', 2200, '123-4567', '名古屋市パスタ通り147', '052-123-4573', 'イタリアン', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'シーフードデライト', '008.jpg', '新鮮なシーフード料理', 4000, '123-4567', '名古屋市シーフード通り258', '052-123-4574', 'シーフード', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'ベジタリアンビストロ', '009.jpg', 'ヘルシーで美味しいベジタリアン料理', 2000, '123-4567', '名古屋市ベジタリアン通り369', '052-123-4575', 'ベジタリアン', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'デザートヘイブン', '010.jpg', '甘くて美味しいデザート', 1000, '123-4567', '名古屋市デザート通り741', '052-123-4576', 'デザート', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'テストレストラン', NULL, 'テスト説明', 2000, '123-4567', '名古屋市テスト通り123', '052-123-4567', 'テストカテゴリ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'テストレストラン2', '', 'テスト説明2', 2500, '123-4567', '名古屋市テスト通り456', '052-123-4568', 'テストカテゴリ2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ロール（権限）データ --
INSERT INTO roles (name) VALUES ('ROLE_FREE'); -- 無料会員
INSERT INTO roles (name) VALUES ('ROLE_PREMIUM'); -- 有料会員
INSERT INTO roles (name) VALUES ('ROLE_ADMIN'); -- 管理者

-- ユーザーデータ --
INSERT INTO users (name, furigana, postal_code, address, phone_number, email, password, role_id, enabled) VALUES
('Free Member', 'フリーメンバー', '123-4567', 'Free Address', '111-1111-1111', 'free@example.com', '$2a$10$rR03./p6CWi8Q2XeD255veqngRm.HoJSxRhojg62YVSROvcYOOp1O', (SELECT id FROM roles WHERE name = 'ROLE_FREE'), true),
('Premium Member', 'プレミアムメンバー', '234-5678', 'Premium Address', '222-2222-2222', 'premium@example.com', '$2a$10$qbmwGeTbzWkFKxcNGkL0GOpJhUa9e3wzCYpHYVSB5ioOxVM9.xwae', (SELECT id FROM roles WHERE name = 'ROLE_PREMIUM'), true),
('Admin User', 'アドミンユーザー', '345-6789', 'Admin Address', '333-3333-3333', 'admin@example.com', '$2a$10$B4hNBRUN9R6T94IiymzQMOQmBybPXuANcm8VZKlvI57sttz9bTPzG', (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'), true);