-- Tạo cơ sở dữ liệu (Nếu chưa có)
-- CREATE DATABASE movie_booking_db;
-- GO

-- Sử dụng cơ sở dữ liệu
-- USE movie_booking_db;
-- GO

----------------------------------------------------
-- Bảng Vai trò (roles)
----------------------------------------------------
CREATE TABLE roles (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(50) NOT NULL UNIQUE -- Ví dụ: 'Admin', 'User', 'Staff'
);

----------------------------------------------------
-- Bảng Người dùng (users)
----------------------------------------------------
CREATE TABLE users (
    id INT PRIMARY KEY IDENTITY(1,1),
    email NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(MAX) NOT NULL, -- Lưu trữ mật khẩu đã được hash
    name NVARCHAR(100),
    address NVARCHAR(255),
    phone NVARCHAR(20),
    role_id INT NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT fk_users_role_id FOREIGN KEY (role_id) REFERENCES roles(id)
);

----------------------------------------------------
-- Bảng Thể loại phim (genres)
----------------------------------------------------
CREATE TABLE genres (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL UNIQUE
);

----------------------------------------------------
-- Bảng Người (people) - Dùng cho Diễn viên, Đạo diễn
----------------------------------------------------
CREATE TABLE people (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(150) NOT NULL,
    dob DATE, -- date_of_birth
    biography NVARCHAR(MAX),
    photo NVARCHAR(500) -- photo_url
);

----------------------------------------------------
-- Bảng Phim (movies)
----------------------------------------------------
CREATE TABLE movies (
    id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    release_date DATE,
    duration INT NOT NULL, -- duration_minutes (Thời lượng phim tính bằng phút)
    image NVARCHAR(500), -- image_url
    trailer NVARCHAR(500), -- trailer_url
    format NVARCHAR(50), -- Ví dụ: '2D', '3D', 'IMAX'
    age_rating NVARCHAR(10),
    status NVARCHAR(50) DEFAULT 'Coming Soon', -- Ví dụ: 'Coming Soon', 'Now Showing', 'Ended'
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);

----------------------------------------------------
-- Bảng Phim - Thể loại (movie_genres)
----------------------------------------------------
CREATE TABLE movie_genres (
    movie_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (movie_id, genre_id),
    CONSTRAINT fk_movie_genres_movie_id FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_movie_genres_genre_id FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

----------------------------------------------------
-- Bảng Phim - Người (movie_people) - Diễn viên, Đạo diễn trong phim
----------------------------------------------------
CREATE TABLE movie_people (
    movie_id INT NOT NULL,
    person_id INT NOT NULL,
    role_type NVARCHAR(100) NOT NULL, -- Ví dụ: 'Director', 'Main Actor', 'Supporting Actor'
    PRIMARY KEY (movie_id, person_id, role_type),
    CONSTRAINT fk_movie_people_movie_id FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_movie_people_person_id FOREIGN KEY (person_id) REFERENCES people(id) ON DELETE CASCADE
);

----------------------------------------------------
-- Bảng Phòng chiếu (cinema_halls)
----------------------------------------------------
CREATE TABLE cinema_halls (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL, -- Tên phòng chiếu, ví dụ: "Phòng 1", "IMAX Hall"
    rows_count INT NOT NULL, -- Tổng số hàng ghế
    seats_per_row_max INT NOT NULL, -- Số ghế tối đa trên một hàng (dùng để ước lượng)
    screen_type NVARCHAR(50), -- Ví dụ: 'Standard', 'IMAX', 'VIP'
    description NVARCHAR(MAX) -- Mô tả thêm về phòng chiếu nếu cần
);

----------------------------------------------------
-- Bảng Ghế ngồi (seats)
----------------------------------------------------
CREATE TABLE seats (
    id INT PRIMARY KEY IDENTITY(1,1),
    hall_id INT NOT NULL,
    row_char NVARCHAR(5) NOT NULL, -- Ký hiệu hàng ghế, ví dụ: 'A', 'B', 'VIP-A'
    seat_num INT NOT NULL, -- Số thứ tự ghế trong hàng
    type NVARCHAR(50) DEFAULT 'Standard', -- Loại ghế: 'Standard', 'VIP', 'Couple'
    is_active BIT DEFAULT 1, -- Ghế có đang sử dụng không (phòng trường hợp ghế hỏng)
    CONSTRAINT fk_seats_hall_id FOREIGN KEY (hall_id) REFERENCES cinema_halls(id) ON DELETE CASCADE,
    CONSTRAINT uq_seat_in_hall UNIQUE (hall_id, row_char, seat_num) -- Đảm bảo mỗi ghế là duy nhất trong phòng
);

----------------------------------------------------
-- Bảng Khung giờ chiếu (time_slots) - Để quản lý các suất chiếu dễ dàng hơn
----------------------------------------------------
CREATE TABLE time_slots (
    id INT PRIMARY KEY IDENTITY(1,1),
    start_time TIME NOT NULL UNIQUE, -- Ví dụ: '09:00:00', '11:30:00'
    slot_name NVARCHAR(100) NULL -- Tên gợi nhớ cho suất chiếu, ví dụ 'Suất sáng sớm', 'Suất trưa'
);

----------------------------------------------------
-- Bảng Lịch chiếu (showtimes)
----------------------------------------------------
CREATE TABLE showtimes (
    id INT PRIMARY KEY IDENTITY(1,1),
    movie_id INT NOT NULL,
    hall_id INT NOT NULL,
    show_date DATE NOT NULL, -- Ngày chiếu
    time_slot_id INT NOT NULL, -- Khung giờ chiếu từ bảng time_slots
    -- ends_at sẽ được tính toán: show_date + time_slots.start_time + movies.duration
    base_price DECIMAL(10, 2) NOT NULL, -- Giá vé cơ bản cho suất chiếu này
    CONSTRAINT fk_showtimes_movie_id FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_showtimes_hall_id FOREIGN KEY (hall_id) REFERENCES cinema_halls(id) ON DELETE CASCADE,
    CONSTRAINT fk_showtimes_time_slot_id FOREIGN KEY (time_slot_id) REFERENCES time_slots(id),
    CONSTRAINT uq_showtime_hall_date_slot UNIQUE (hall_id, show_date, time_slot_id) -- Đảm bảo một phòng không có 2 suất chiếu trùng giờ
);

CREATE INDEX ix_showtimes_movie_date ON showtimes (movie_id, show_date);

----------------------------------------------------
-- Bảng Trạng thái ghế theo Lịch chiếu (showtime_seat_status)
----------------------------------------------------
CREATE TABLE showtime_seat_status (
    id INT PRIMARY KEY IDENTITY(1,1),
    showtime_id INT NOT NULL,
    seat_id INT NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'Available', -- Ví dụ: 'Available', 'Booked', 'Locked', 'Unavailable'
    price DECIMAL(10, 2), -- Giá cụ thể cho ghế này tại suất chiếu này (có thể khác showtimes.base_price do loại ghế hoặc khuyến mãi)
    CONSTRAINT fk_showtime_seat_status_showtime_id FOREIGN KEY (showtime_id) REFERENCES showtimes(id) ON DELETE CASCADE,
    CONSTRAINT fk_showtime_seat_status_seat_id FOREIGN KEY (seat_id) REFERENCES seats(id), -- Không CASCADE DELETE ở đây
    CONSTRAINT uq_showtime_seat UNIQUE (showtime_id, seat_id) -- Mỗi ghế chỉ có 1 trạng thái cho 1 suất chiếu
);

----------------------------------------------------
-- Bảng Sản phẩm (Đồ ăn, Nước uống, ...) - Mở rộng hơn food_combos
----------------------------------------------------
CREATE TABLE products (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(150) NOT NULL,
    description NVARCHAR(500),
    price DECIMAL(10, 2) NOT NULL,
    image NVARCHAR(500), -- image_url
    type NVARCHAR(50) DEFAULT 'Food' -- Ví dụ: 'Food', 'Beverage', 'Combo'
);

----------------------------------------------------
-- Bảng Chi tiết Combo (combo_items) - Nếu một sản phẩm là 'Combo'
----------------------------------------------------
CREATE TABLE combo_items (
    id INT PRIMARY KEY IDENTITY(1,1),
    combo_product_id INT NOT NULL, -- FK đến products.id (sản phẩm loại 'Combo')
    item_product_id INT NOT NULL, -- FK đến products.id (sản phẩm con trong combo)
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_combo_items_combo_id FOREIGN KEY (combo_product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_combo_items_item_id FOREIGN KEY (item_product_id) REFERENCES products(id) -- Không cascade, vì sản phẩm con có thể tồn tại độc lập
);

----------------------------------------------------
-- Bảng Đặt vé (bookings)
----------------------------------------------------
CREATE TABLE bookings (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    showtime_id INT NOT NULL,
    booked_at DATETIME2 DEFAULT GETDATE(),
    total_amount DECIMAL(18, 2) NOT NULL,
    status NVARCHAR(50) DEFAULT 'Pending', -- 'Pending', 'Confirmed', 'Cancelled', 'Attended'
    payment_type NVARCHAR(50), -- Ví dụ: 'OnlineBanking', 'CreditCard', 'AtCounter'
    transaction_id NVARCHAR(255) NULL, -- Mã giao dịch từ cổng thanh toán
    staff_id INT NULL, -- FK đến users.id, nếu nhân viên đặt hộ
    notes NVARCHAR(MAX) NULL, -- Ghi chú thêm cho đơn đặt vé
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT fk_bookings_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_bookings_showtime_id FOREIGN KEY (showtime_id) REFERENCES showtimes(id),
    CONSTRAINT fk_bookings_staff_id FOREIGN KEY (staff_id) REFERENCES users(id)
);

CREATE INDEX ix_bookings_user_id_status ON bookings (user_id, status);

----------------------------------------------------
-- Bảng Chi tiết Vé đã đặt (booking_tickets) - Mỗi ghế đặt là 1 vé
----------------------------------------------------
CREATE TABLE booking_tickets (
    id INT PRIMARY KEY IDENTITY(1,1),
    booking_id INT NOT NULL,
    showtime_seat_id INT NOT NULL, -- FK đến showtime_seat_status.id
    ticket_code NVARCHAR(100) NOT NULL UNIQUE, -- Mã vé duy nhất (ví dụ QR code data)
    price_at_booking DECIMAL(10, 2) NOT NULL, -- Giá vé tại thời điểm đặt
    CONSTRAINT fk_booking_tickets_booking_id FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_tickets_showtime_seat_id FOREIGN KEY (showtime_seat_id) REFERENCES showtime_seat_status(id)
);

----------------------------------------------------
-- Bảng Chi tiết Sản phẩm đã đặt (booking_products)
----------------------------------------------------
CREATE TABLE booking_products (
    id INT PRIMARY KEY IDENTITY(1,1),
    booking_id INT NOT NULL,
    product_id INT NOT NULL, -- FK đến products.id (có thể là đồ ăn lẻ hoặc combo)
    quantity INT NOT NULL CHECK (quantity > 0),
    price_per_unit_at_booking DECIMAL(10, 2) NOT NULL,
    subtotal AS (quantity * price_per_unit_at_booking),
    CONSTRAINT fk_booking_products_booking_id FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_products_product_id FOREIGN KEY (product_id) REFERENCES products(id)
);

----------------------------------------------------
-- Bảng Đánh giá & Bình luận (reviews)
----------------------------------------------------
CREATE TABLE reviews (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    booking_id INT NOT NULL, -- Để kiểm tra người dùng đã xem phim từ đơn đặt vé này chưa
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment NVARCHAR(MAX),
    reviewed_at DATETIME2 DEFAULT GETDATE(),
    is_approved BIT DEFAULT 0, -- Admin có thể duyệt review
    CONSTRAINT fk_reviews_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_movie_id FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_reviews_booking_id FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT uq_review_user_movie_booking UNIQUE (user_id, movie_id, booking_id) -- Tránh 1 người review nhiều lần cho cùng 1 lần xem
);

CREATE INDEX ix_reviews_movie_id_approved ON reviews (movie_id, is_approved);

----------------------------------------------------
-- Thêm một số dữ liệu mẫu (Tùy chọn)
----------------------------------------------------
-- roles
INSERT INTO roles (name) VALUES ('Admin'), ('User'), ('Staff');

-- users
INSERT INTO users (email, password, name, role_id) VALUES
('admin@example.com', 'hashed_password_admin', 'Admin Cinema', 1),
('user1@example.com', 'hashed_password_user1', 'Nguyễn Văn An', 2),
('staff1@example.com', 'hashed_password_staff1', 'Trần Thị Bích', 3);

-- genres
INSERT INTO genres (name) VALUES ('Hành động'), ('Phiêu lưu'), ('Hài'), ('Chính kịch'), ('Kinh dị'), ('Khoa học viễn tưởng'), ('Hoạt hình'), ('Tình cảm');

-- people
INSERT INTO people (name, dob, biography, photo) VALUES
('Lý Hải', '1968-09-28', 'Đạo diễn, ca sĩ, diễn viên người Việt Nam, nổi tiếng với series phim Lật Mặt.', 'lyhai.jpg'),
('Trấn Thành', '1987-02-05', 'Diễn viên, MC, đạo diễn, nhà sản xuất phim nổi tiếng của Việt Nam.', 'tran_thanh.jpg');

-- movies
INSERT INTO movies (title, description, release_date, duration, image, trailer, format, language, country, age_rating, status) VALUES
(N'Lật Mặt 7: Một Điều Ước', N'Phim Lật Mặt 7 của Lý Hải xoay quanh câu chuyện cảm động về tình mẫu tử.', '2024-04-26', 135, 'latmat7.jpg', 'youtube.com/latmat7', '2D', N'Tiếng Việt', N'Việt Nam', 'P', 'Now Showing'),
(N'Doraemon: Nobita và Bản Giao Hưởng Địa Cầu', N'Nobita và những người bạn phiêu lưu trong thế giới âm nhạc.', '2024-05-24', 110, 'doraemon_movie.jpg', 'youtube.com/doraemon', '2D', N'Tiếng Nhật', N'Nhật Bản', 'P', 'Now Showing');

-- movie_genres
INSERT INTO movie_genres (movie_id, genre_id) VALUES (1, 4), (1, 8), (2, 7), (2, 2);

-- movie_people
INSERT INTO movie_people (movie_id, person_id, role_type) VALUES (1, 1, 'Director'), (1, 2, 'Main Actor');

-- cinema_halls
INSERT INTO cinema_halls (name, rows_count, seats_per_row_max, screen_type, description) VALUES
(N'Phòng chiếu 01', 10, 12, 'Standard', N'Phòng chiếu tiêu chuẩn, âm thanh Dolby 7.1'),
(N'Phòng chiếu IMAX', 15, 20, 'IMAX', N'Trải nghiệm màn hình khổng lồ IMAX');

-- seats (Ví dụ cho Phòng chiếu 01)
DECLARE @hall1_id INT = (SELECT id FROM cinema_halls WHERE name = N'Phòng chiếu 01');
INSERT INTO seats (hall_id, row_char, seat_num, type) VALUES
(@hall1_id, 'A', 1, 'Standard'), (@hall1_id, 'A', 2, 'Standard'), (@hall1_id, 'A', 3, 'Standard'),
(@hall1_id, 'B', 1, 'VIP'), (@hall1_id, 'B', 2, 'VIP');

-- time_slots
INSERT INTO time_slots (start_time, slot_name) VALUES ('09:00:00', N'Suất sáng 1'), ('11:30:00', N'Suất sáng 2'), ('14:00:00', N'Suất chiều 1'), ('16:30:00', N'Suất chiều 2'),('19:00:00', N'Suất tối 1'), ('21:30:00', N'Suất tối 2');

-- showtimes
DECLARE @movie1_id INT = (SELECT id FROM movies WHERE title LIKE N'Lật Mặt 7%');
DECLARE @slot1_id INT = (SELECT id FROM time_slots WHERE start_time = '19:00:00');
INSERT INTO showtimes (movie_id, hall_id, show_date, time_slot_id, base_price) VALUES
(@movie1_id, @hall1_id, GETDATE(), @slot1_id, 120000.00);

-- showtime_seat_status (Cần trigger hoặc procedure để tự động tạo khi có showtime mới)
-- Ví dụ thủ công cho suất chiếu vừa tạo:
DECLARE @showtime1_id INT = (SELECT id FROM showtimes WHERE movie_id = @movie1_id AND hall_id = @hall1_id AND time_slot_id = @slot1_id AND show_date = CAST(GETDATE() AS DATE));
INSERT INTO showtime_seat_status (showtime_id, seat_id, status, price)
SELECT @showtime1_id, s.id, 'Available',
       CASE s.type WHEN 'VIP' THEN st.base_price * 1.5 ELSE st.base_price END
FROM seats s JOIN showtimes st ON st.id = @showtime1_id
WHERE s.hall_id = st.hall_id AND s.is_active = 1;


-- products (Đồ ăn, Combo)
INSERT INTO products (name, description, price, image, type) VALUES
(N'Bắp rang bơ Phô Mai Lớn', N'Bắp rang bơ vị phô mai, size lớn.', 65000.00, 'popcorn_cheese_l.jpg', 'Food'),
(N'Nước ngọt Coca-Cola Vừa', N'Coca-Cola lon/ly size vừa.', 35000.00, 'coke_m.jpg', 'Beverage'),
(N'Combo Tình Yêu', N'1 Bắp Lớn + 2 Nước Vừa', 129000.00, 'combo_love.jpg', 'Combo');

-- combo_items (Chi tiết cho Combo Tình Yêu)
DECLARE @combo_love_id INT = (SELECT id FROM products WHERE name = N'Combo Tình Yêu');
DECLARE @popcorn_l_id INT = (SELECT id FROM products WHERE name LIKE N'Bắp rang bơ Phô Mai Lớn%');
DECLARE @coke_m_id INT = (SELECT id FROM products WHERE name LIKE N'Nước ngọt Coca-Cola Vừa%');
INSERT INTO combo_items (combo_product_id, item_product_id, quantity) VALUES
(@combo_love_id, @popcorn_l_id, 1),
(@combo_love_id, @coke_m_id, 2);

/*
Ghi chú quan trọng:
1.  **Mật khẩu (`users.password`):** Luôn phải được hash bằng thuật toán mạnh (bcrypt, Argon2) trước khi lưu.
2.  **`showtime_seat_status`:** Khi một lịch chiếu (`showtimes`) mới được tạo, bạn cần có logic (ví dụ: trigger trong DB hoặc code ở tầng ứng dụng) để tự động tạo các bản ghi tương ứng trong `showtime_seat_status` cho tất cả các ghế (`seats.is_active = 1`) của phòng chiếu đó với trạng thái 'Available' và giá khởi tạo.
3.  **Tính toán `ends_at` cho `showtimes`:** Thời gian kết thúc của một suất chiếu có thể được tính toán động (show_date + time_slots.start_time + movies.duration) hoặc lưu trữ trực tiếp nếu cần.
4.  **`booking_tickets.ticket_code`:** Cần cơ chế tạo mã vé duy nhất.
5.  **Quản lý Rạp/Chi nhánh:** Để giữ đơn giản theo yêu cầu, tôi không thêm bảng `cinema_chains` hay `branches`. Thông tin này có thể được ngầm hiểu hoặc thêm vào `cinema_halls.description` nếu cần thiết ở mức độ cơ bản.
6.  **`reviews.booking_id`:** Dùng để liên kết đánh giá với một lần xem cụ thể, giúp xác thực người dùng đã mua vé và xem phim.
*/
