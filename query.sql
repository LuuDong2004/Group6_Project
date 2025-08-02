-- Tạo bảng ratings
CREATE TABLE ratings (
    id INT IDENTITY(1,1) PRIMARY KEY,
    code NVARCHAR(10) NOT NULL UNIQUE,
    description NVARCHAR(255)
);

-- Tạo bảng genres  
CREATE TABLE genres (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(255)
);

-- Tạo bảng trung gian movie_genres (many-to-many)
CREATE TABLE movie_genres (
    movie_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (movie_id, genre_id),
    FOREIGN KEY (movie_id) REFERENCES Movie(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

-- Thêm cột rating_id vào bảng Movie
ALTER TABLE Movie ADD rating_id INT;

-- Thêm foreign key constraint cho rating
ALTER TABLE Movie 
ADD CONSTRAINT FK_Movie_Rating 
FOREIGN KEY (rating_id) REFERENCES ratings(id);

-- Thêm index để tối ưu query
CREATE INDEX idx_movie_rating_id ON Movie(rating_id);
CREATE INDEX idx_movie_genres_movie_id ON movie_genres(movie_id);
CREATE INDEX idx_movie_genres_genre_id ON movie_genres(genre_id);

-- Thêm dữ liệu cho bảng ratings
INSERT INTO ratings (code, description) VALUES 
('G', N'Mọi lứa tuổi'),
('K', N'Dành cho trẻ em dưới 13 tuổi'),
('T13', N'Từ 13 tuổi trở lên'),
('T16', N'Từ 16 tuổi trở lên'),
('T18', N'Từ 18 tuổi trở lên'),
('C', N'Cấm chiếu');

-- Thêm dữ liệu cho bảng genres
INSERT INTO genres (name, description) VALUES 
(N'Hành động', N'Phim hành động'),
(N'Hài kịch', N'Phim hài kịch'),
(N'Kinh dị', N'Phim kinh dị'),
(N'Lãng mạn', N'Phim lãng mạn'),
(N'Khoa học viễn tưởng', N'Phim khoa học viễn tưởng'),
(N'Tâm lý', N'Phim tâm lý'),
(N'Phiêu lưu', N'Phim phiêu lưu'),
(N'Hoạt hình', N'Phim hoạt hình'),
(N'Chính kịch', N'Phim chính kịch'),
(N'Tài liệu', N'Phim tài liệu'),
(N'Thể thao', N'Phim thể thao'),
(N'Âm nhạc', N'Phim âm nhạc'),
(N'Chiến tranh', N'Phim chiến tranh'),
(N'Tội phạm', N'Phim tội phạm'),
(N'Gia đình', N'Phim gia đình');