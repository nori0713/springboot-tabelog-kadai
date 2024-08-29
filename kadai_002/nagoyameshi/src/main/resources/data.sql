-- 外部キー制約を一時的に無効化
SET FOREIGN_KEY_CHECKS = 0;

-- 既存のデータを削除
DELETE FROM favorites;
DELETE FROM reviews;
DELETE FROM reservations;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM restaurants;

-- 外部キー制約を再び有効化
SET FOREIGN_KEY_CHECKS = 1;

-- カラム追加 --
-- ALTER TABLE users
-- ADD COLUMN subscription_status VARCHAR(50) NOT NULL DEFAULT 'INACTIVE';

-- 飲食店データ --
INSERT INTO restaurants (id, name, image_name, description, price, postal_code, address, phone_number, category, capacity, created_at, updated_at)
VALUES 
(1, '寿司屋', '001.jpg', '町で一番の寿司', 3000, '123-4567', '名古屋市寿司町123', '052-123-4567', '和食', 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'ラーメンハウス', '002.jpg', '美味しいラーメンとサイドメニュー', 1200, '123-4567', '名古屋市ラーメン通り456', '052-123-4568', '和食', 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'ピザワールド', '003.jpg', '本格的なイタリアンピザ', 2500, '123-4567', '名古屋市ピザ大通り789', '052-123-4569', 'イタリアン', 40, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'バーガータウン', '004.jpg', 'ジューシーなバーガーとフライドポテト', 1500, '123-4567', '名古屋市バーガー通り321', '052-123-4570', 'アメリカン', 35, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'カレーハウス', '005.jpg', 'スパイシーで風味豊かなカレー', 1800, '123-4567', '名古屋市カレー通り654', '052-123-4571', 'インド料理', 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'BBQヘブン', '006.jpg', 'スモーキーで柔らかいBBQ', 3500, '123-4567', '名古屋市BBQ通り987', '052-123-4572', 'アメリカン', 60, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'パスタパレス', '007.jpg', '新鮮で美味しいパスタ', 2200, '123-4567', '名古屋市パスタ通り147', '052-123-4573', 'イタリアン', 40, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'シーフードデライト', '008.jpg', '新鮮なシーフード料理', 4000, '123-4567', '名古屋市シーフード通り258', '052-123-4574', 'シーフード', 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'ベジタリアンビストロ', '009.jpg', 'ヘルシーで美味しいベジタリアン料理', 2000, '123-4567', '名古屋市ベジタリアン通り369', '052-123-4575', 'ベジタリアン', 45, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'デザートヘイブン', '010.jpg', '甘くて美味しいデザート', 1000, '123-4567', '名古屋市デザート通り741', '052-123-4576', 'デザート', 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'テストレストラン', NULL, 'テスト説明', 2000, '123-4567', '名古屋市テスト通り123', '052-123-4567', 'テストカテゴリ', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'テストレストラン2', '', 'テスト説明2', 2500, '123-4567', '名古屋市テスト通り456', '052-123-4568', 'テストカテゴリ2', 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- rolesテーブル
INSERT IGNORE INTO roles (id, name) VALUES 
(1, 'ROLE_FREE'), 
(2, 'ROLE_PREMIUM'), 
(3, 'ROLE_ADMIN');

-- ユーザーデータ --
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id, enabled, subscription_status) VALUES
(1, 'Free Member', 'フリーメンバー', '123-4567', 'Free Address', '111-1111-1111', 'free@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_FREE' LIMIT 1), true, 'INACTIVE'),
(2, 'Premium Member', 'プレミアムメンバー', '234-5678', 'Premium Address', '222-2222-2222', 'premium@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_PREMIUM' LIMIT 1), true, 'ACTIVE'),
(3, 'Admin User', 'アドミンユーザー', '345-6789', 'Admin Address', '333-3333-3333', 'admin@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_ADMIN' LIMIT 1), true, 'ACTIVE'),
(4, '田中 一郎', 'タナカ イチロウ', '456-7890', '名古屋市中区栄1-1-1', '090-1111-2222', 'ichiro.tanaka@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_FREE' LIMIT 1), true, 'INACTIVE'),
(5, '鈴木 花子', 'スズキ ハナコ', '123-7890', '名古屋市西区名駅3-3-3', '090-3333-4444', 'hanako.suzuki@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_FREE' LIMIT 1), true, 'INACTIVE'),
(6, '佐藤 次郎', 'サトウ ジロウ', '789-1234', '名古屋市東区東桜4-4-4', '090-5555-6666', 'jiro.sato@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_FREE' LIMIT 1), true, 'INACTIVE'),
(7, '山田 太郎', 'ヤマダ タロウ', '234-5678', '名古屋市南区大須5-5-5', '090-7777-8888', 'taro.yamada@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_FREE' LIMIT 1), true, 'INACTIVE'),
(8, '伊藤 美咲', 'イトウ ミサキ', '567-8901', '名古屋市港区港南1-1-1', '090-9999-0000', 'misaki.ito@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_FREE' LIMIT 1), true, 'INACTIVE'),
(9, '高橋 勇', 'タカハシ イサム', '345-6789', '名古屋市守山区小幡2-2-2', '090-1234-5678', 'isamu.takahashi@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_FREE' LIMIT 1), true, 'INACTIVE'),
(10, '松本 愛', 'マツモト アイ', '789-0123', '名古屋市瑞穂区弥富町6-6-6', '090-8765-4321', 'ai.matsumoto@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_FREE' LIMIT 1), true, 'INACTIVE'),
(11, '中村 健太', 'ナカムラ ケンタ', '456-0123', '名古屋市熱田区神宮西7-7-7', '090-3456-7890', 'kenta.nakamura@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_FREE' LIMIT 1), true, 'INACTIVE'),
(12, '小林 真由美', 'コバヤシ マユミ', '123-8901', '名古屋市千種区山手通8-8-8', '090-5678-1234', 'mayumi.kobayashi@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_FREE' LIMIT 1), true, 'INACTIVE'),
(13, '加藤 仁', 'カトウ ヒトシ', '678-9012', '名古屋市名東区藤ヶ丘9-9-9', '090-7890-1234', 'hitoshi.kato@example.com', '$2a$10$fLkgnX6vtfaUCh5/NdjHa.3giUiF..TLsIlnJH4lJEKGdLeaV.eKW', (SELECT id FROM roles WHERE name = 'ROLE_FREE' LIMIT 1), true, 'INACTIVE');

-- 予約データ --
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, reservation_date, number_of_people, created_at, updated_at)
VALUES 
(1, 1, 1, '2024-08-25', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 2, '2024-08-26', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 3, '2024-08-27', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 4, 4, '2024-08-28', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 5, 5, '2024-08-29', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 6, 6, '2024-08-30', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 7, 7, '2024-08-31', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 8, 8, '2024-09-01', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 9, 9, '2024-09-02', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 10, 10, '2024-09-03', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 1, 4, '2024-09-04', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 2, 3, '2024-09-05', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 3, 2, '2024-09-06', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 4, 1, '2024-09-07', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 5, 5, '2024-09-08', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- レビューデータ --
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, score, content) VALUES 
(1, 1, 1, 5, '最高の寿司！新鮮で美味しかったです。'), 
(2, 2, 2, 4, 'うなぎが完璧に調理されていましたが、少し高いです。'), 
(3, 3, 3, 5, 'サクサクのとんかつに素晴らしいソース。非常におすすめです！'), 
(4, 4, 4, 3, 'ラーメンはまあまあでしたが、スープが少し塩辛かったです。'), 
(5, 5, 5, 4, 'そばの風味がよく、食感も素晴らしかったです。'), 
(6, 6, 6, 5, '最高の鉄板焼き体験！シェフが素晴らしかったです。'), 
(7, 7, 7, 4, '焼き鳥は美味しく、よく味付けされていました。夕食にぴったりの場所です。'), 
(8, 8, 8, 5, '素晴らしい焼肉！お肉の品質が最高です。'), 
(9, 9, 9, 3, '天ぷらは美味しかったが、量が少なかったです。'), 
(10, 10, 10, 4, '良い寿司でしたが、特別感はありません。訪れる価値はあります。'), 
(11, 1, 2, 5, '二度目の訪問ですが、寿司は前回と同じくらい素晴らしかったです。'), 
(12, 2, 3, 4, 'うなぎは素晴らしかったですが、付け合わせの料理が少し物足りなかったです。'), 
(13, 3, 4, 5, 'この店は期待を裏切りません。とんかつはいつも完璧です。'), 
(14, 4, 5, 3, 'ラーメンはまあまあでしたが、名古屋にはもっと良い場所があります。'), 
(15, 5, 6, 4, 'そばがとても美味しかったです。また来たいと思います。'), 
(16, 6, 7, 5, 'ここでの鉄板焼きは必見です。素晴らしい体験でした。'), 
(17, 7, 8, 4, '焼き鳥は美味しかったですが、サービスが少し遅かったです。'), 
(18, 8, 9, 5, '焼肉が最高でした！お肉の質がとても良かったです。'), 
(19, 9, 10, 3, '天ぷらはサクサクしていましたが、つけダレが少し薄味でした。'), 
(20, 10, 1, 4, '新鮮な食材を使ったしっかりとした寿司店です。');

-- お気に入りデータ --
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES 
(1, 1, 1),
(2, 2, 1),
(3, 3, 1),
(4, 4, 1),
(5, 5, 1),
(6, 6, 1),
(7, 7, 1),
(8, 8, 1),
(9, 9, 1),
(10, 10, 1),
(11, 11, 1),
(12, 12, 1),
(13, 1, 2),
(14, 2, 2),
(15, 3, 2),
(16, 4, 2),
(17, 5, 2),
(18, 6, 2),
(19, 7, 2),
(20, 8, 2),
(21, 9, 2),
(22, 10, 2),
(23, 11, 2),
(24, 12, 2),
(25, 1, 3),
(26, 2, 3),
(27, 3, 3),
(28, 4, 3),
(29, 5, 3),
(30, 6, 3);