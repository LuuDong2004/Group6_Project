-- Dữ liệu cho bảng roles
INSERT INTO roles (name) VALUES
('Admin'), ('User'), ('Staff'), ('Guest'),
('Admin_1'), ('User_1'), ('Staff_1'), ('Guest_1'),
('Admin_2'), ('User_2'), ('Staff_2'), ('Guest_2'),
('Admin_3'), ('User_3'), ('Staff_3'), ('Guest_3'),
('Admin_4'), ('User_4'), ('Staff_4'), ('Guest_4');

-- Dữ liệu cho bảng genres
INSERT INTO genres (name) VALUES
('Action'), ('Comedy'), ('Drama'), ('Sci-Fi'), ('Thriller'),
('Horror'), ('Animation'), ('Adventure'), ('Fantasy'), ('Romance'),
('Mystery'), ('Documentary'), ('Biography'), ('Musical'), ('Western'),
('Crime'), ('Family'), ('Sport'), ('War'), ('History');


-- Dữ liệu cho bảng people
INSERT INTO people (name, dob, biography, photo) VALUES
('Tom Hanks', '1956-07-09', 'An American actor and filmmaker. Known for his comedic and dramatic roles.', 'https://placehold.co/150x200/cccccc/000000?text=Tom+Hanks'),
('Steven Spielberg', '1946-12-18', 'An American film director, producer, and screenwriter. A founding pioneer of the New Hollywood era.', 'https://placehold.co/150x200/cccccc/000000?text=S.+Spielberg'),
('Scarlett Johansson', '1984-11-22', 'An American actress and singer. The world''s highest-paid actress in 2018 and 2019.', 'https://placehold.co/150x200/cccccc/000000?text=S.+Johansson'),
('Leonardo DiCaprio', '1974-11-11', 'An American actor and film producer known for his work in biopics and period films.', 'https://placehold.co/150x200/cccccc/000000?text=L.+DiCaprio'),
('Quentin Tarantino', '1963-03-27', 'An American film director, screenwriter, producer, and actor. His films are characterized by non-linear storylines, satirical subject matter, and stylized violence.', 'https://placehold.co/150x200/cccccc/000000?text=Q.+Tarantino'),
('Emma Stone', '1988-11-06', 'An American actress. She is the recipient of various accolades, including an Academy Award, a British Academy Film Award, and a Golden Globe Award.', 'https://placehold.co/150x200/cccccc/000000?text=Emma+Stone'),
('Dwayne Johnson', '1972-05-02', 'An American actor and former professional wrestler. Known for his action roles.', 'https://placehold.co/150x200/cccccc/000000?text=D.+Johnson'),
('Christopher Nolan', '1970-07-30', 'A British-American film director, screenwriter, and producer. His films often explore themes of time, memory, and identity.', 'https://placehold.co/150x200/cccccc/000000?text=C.+Nolan'),
('Meryl Streep', '1949-06-22', 'An American actress. Often described as "the best actress of her generation".', 'https://placehold.co/150x200/cccccc/000000?text=M.+Streep'),
('Martin Scorsese', '1942-11-17', 'An American film director, producer, and screenwriter. He is a prominent figure in the New Hollywood era.', 'https://placehold.co/150x200/cccccc/000000?text=M.+Scorsese'),
('Brad Pitt', '1963-12-18', 'An American actor and film producer. He has received various accolades, including two Academy Awards.', 'https://placehold.co/150x200/cccccc/000000?text=Brad+Pitt'),
('Jennifer Lawrence', '1990-08-15', 'An American actress. She was the highest-paid actress in the world in 2015 and 2016.', 'https://placehold.co/150x200/cccccc/000000?text=J.+Lawrence'),
('James Cameron', '1954-08-16', 'A Canadian film director, producer, screenwriter, and editor. Known for his epic films.', 'https://placehold.co/150x200/cccccc/000000?text=J.+Cameron'),
('Sandra Bullock', '1964-07-26', 'An American actress and producer. She was the world''s highest-paid actress in 2010 and 2014.', 'https://placehold.co/150x200/cccccc/000000?text=S.+Bullock'),
('Ryan Gosling', '1980-11-12', 'A Canadian actor and musician. He has received various accolades, including a Golden Globe Award.', 'https://placehold.co/150x200/cccccc/000000?text=R.+Gosling'),
('Denis Villeneuve', '1967-10-03', 'A Canadian film director, screenwriter, and producer. Known for his science fiction films.', 'https://placehold.co/150x200/cccccc/000000?text=D.+Villeneuve'),
('Anne Hathaway', '1982-11-12', 'An American actress. She is the recipient of various accolades, including an Academy Award.', 'https://placehold.co/150x200/cccccc/000000?text=A.+Hathaway'),
('Ridley Scott', '1937-11-30', 'An English film director and producer. Known for his highly influential science fiction and historical films.', 'https://placehold.co/150x200/cccccc/000000?text=R.+Scott'),
('Margot Robbie', '1990-07-02', 'An Australian actress and producer. She has received various accolades, including nominations for two Academy Awards.', 'https://placehold.co/150x200/cccccc/000000?text=M.+Robbie'),
('Wes Anderson', '1969-05-01', 'An American film director, screenwriter, and producer. Known for his distinctive visual and narrative styles.', 'https://placehold.co/150x200/cccccc/000000?text=W.+Anderson');


-- Dữ liệu cho bảng movies
INSERT INTO movies (title, description, release_date, duration, image, trailer, format, age_rating, status) VALUES
('Dune: Part Two', 'Paul Atreides unites with Chani and the Fremen while seeking revenge against those who destroyed his family.', '2024-03-01', 166, 'https://placehold.co/300x450/cccccc/000000?text=Dune+2', 'https://www.youtube.com/watch?v=Way9Dexny3w', '2D|IMAX', 'PG-13', 'Now Showing'),
('Kung Fu Panda 4', 'Po must train a new Dragon Warrior before he can take on a new villain known as The Chameleon.', '2024-03-08', 94, 'https://placehold.co/300x450/cccccc/000000?text=KFP4', 'https://www.youtube.com/watch?v=kYgYtC-BqFw', '2D|3D', 'PG', 'Now Showing'),
('Godzilla x Kong: The New Empire', 'Two ancient titans, Godzilla and Kong, clash in an epic battle as humans unravel their intertwined origins.', '2024-03-29', 115, 'https://placehold.co/300x450/cccccc/000000?text=GxK', 'https://www.youtube.com/watch?v=qqPF4T3wUus', '2D|IMAX', 'PG-13', 'Now Showing'),
('Ghostbusters: Frozen Empire', 'The Spengler family returns to where it all started – the iconic New York City firehouse – to team up with the original Ghostbusters, who have developed a top-secret research lab to take ghost-busting to the next level.', '2024-03-22', 115, 'https://placehold.co/300x450/cccccc/000000?text=GBFE', 'https://www.youtube.com/watch?v=F0fNmiP1r0k', '2D', 'PG-13', 'Now Showing'),
('The Fall Guy', 'A stuntman who left the business after an accident is recruited to track down a missing film star.', '2024-05-03', 125, 'https://placehold.co/300x450/cccccc/000000?text=Fall+Guy', 'https://www.youtube.com/watch?v=AdwS7L4Gj5s', '2D', 'PG-13', 'Coming Soon'),
('IF', 'A young girl gains the ability to see imaginary friends and embarks on a magical adventure to reconnect them with their real-world kids.', '2024-05-17', 104, 'https://placehold.co/300x450/cccccc/000000?text=IF', 'https://www.youtube.com/watch?v=P7tXk1_m53k', '2D', 'G', 'Coming Soon'),
('Furiosa: A Mad Max Saga', 'As the world fell, young Furiosa is snatched from the Green Place of Many Mothers and falls into the hands of a great Biker Horde led by the Warlord Dementus.', '2024-05-24', 148, 'https://placehold.co/300x450/cccccc/000000?text=Furiosa', 'https://www.youtube.com/watch?v=bYj68VwFv0Q', '2D|IMAX', 'R', 'Coming Soon'),
('Bad Boys: Ride or Die', 'The Bad Boys return for another action-packed adventure.', '2024-06-07', 115, 'https://placehold.co/300x450/cccccc/000000?text=BBROD', 'https://www.youtube.com/watch?v=B8oE6e3820Q', '2D', 'R', 'Coming Soon'),
('Inside Out 2', 'Riley is now a teenager, and Headquarters is undergoing a sudden demolition to make room for something entirely unexpected: new Emotions!', '2024-06-14', 100, 'https://placehold.co/300x450/cccccc/000000?text=Inside+Out+2', 'https://www.youtube.com/watch?v=LEjhY1Cku8c', '2D|3D', 'PG', 'Coming Soon'),
('A Quiet Place: Day One', 'A spin-off prequel revealing the day the world went quiet.', '2024-06-28', 100, 'https://placehold.co/300x450/cccccc/000000?text=A+Quiet+Place', 'https://www.youtube.com/watch?v=Yf1eS6d271g', '2D', 'PG-13', 'Coming Soon'),
('Spider-Man: Homecoming', 'Peter Parker is introduced to Iron Man and tries to balance his high school life with being Spider-Man.', '2017-07-07', 133, 'https://placehold.co/300x450/cccccc/000000?text=Spidey+Homecoming', 'https://www.youtube.com/watch?v=x_A_bM4_2h8', '2D', 'PG-13', 'Ended'),
('Avengers: Endgame', 'Adrift in space with no food or water, Tony Stark sends a message to Pepper Potts as his oxygen supply starts to dwindle. Meanwhile, the remaining Avengers -- Thor, Black Widow, Captain America and Bruce Banner -- must figure out a way to bring back their vanquished allies for an epic showdown with Thanos -- the evil demigod who decimated the planet and the universe.', '2019-04-26', 181, 'https://placehold.co/300x450/cccccc/000000?text=Endgame', 'https://www.youtube.com/watch?v=PyakRSni-c0', '2D|IMAX', 'PG-13', 'Ended'),
('Inception', 'A thief who steals corporate secrets through use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', '2010-07-16', 148, 'https://placehold.co/300x450/cccccc/000000?text=Inception', 'https://www.youtube.com/watch?v=YoVb9lXj6xI', '2D', 'PG-13', 'Ended'),
('The Dark Knight', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', '2008-07-18', 152, 'https://placehold.co/300x450/cccccc/000000?text=Dark+Knight', 'https://www.youtube.com/watch?v=EXe4O2Y6z20', '2D|IMAX', 'PG-13', 'Ended'),
('Forrest Gump', 'The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold from the perspective of an Alabama man with an IQ of 75, whose only desire is to be reunited with his childhood sweetheart.', '1994-07-06', 142, 'https://placehold.co/300x450/cccccc/000000?text=Forrest+Gump', 'https://www.youtube.com/watch?v=bT_Qf5R65qA', '2D', 'PG-13', 'Ended'),
('Pulp Fiction', 'The lives of two mob hitmen, a boxer, a gangster''s wife, and a pair of diner bandits intertwine in four tales of violence and redemption.', '1994-10-14', 154, 'https://placehold.co/300x450/cccccc/000000?text=Pulp+Fiction', 'https://www.youtube.com/watch?v=s75S_P1YgVw', '2D', 'R', 'Ended'),
('Avatar', 'A paraplegic marine dispatched to the moon Pandora on a unique mission becomes torn between following orders and protecting the world he feels is his home.', '2009-12-18', 162, 'https://placehold.co/300x450/cccccc/000000?text=Avatar', 'https://www.youtube.com/watch?v=5PSNL1fcD6o', '3D|IMAX', 'PG-13', 'Ended'),
('Titanic', 'A seventeen-year-old aristocrat falls in love with a kind but poor artist aboard the luxurious, ill-fated RMS Titanic.', '1997-12-19', 195, 'https://placehold.co/300x450/cccccc/000000?text=Titanic', 'https://www.youtube.com/watch?v=F2P3RkL99g0', '2D', 'PG-13', 'Ended'),
('Spirited Away', 'During her family''s move to the suburbs, a sullen 10-year-old girl wanders into a world ruled by gods, witches, and spirits, and where humans are changed into beasts.', '2001-07-20', 125, 'https://placehold.co/300x450/cccccc/000000?text=Spirited+Away', 'https://www.youtube.com/watch?v=ByXuk9Qq8GM', '2D', 'PG', 'Ended'),
('Toy Story', 'A cowboy doll is profoundly threatened and jealous when a new spaceman figure supplants him as a boy''s favorite toy.', '1995-11-22', 81, 'https://placehold.co/300x450/cccccc/000000?text=Toy+Story', 'https://www.youtube.com/watch?v=v-PjgYDrgRU', '2D', 'G', 'Ended');


-- Dữ liệu cho bảng movie_genres (20 bản ghi)
INSERT INTO movie_genres (movie_id, genre_id) VALUES
(1, 1), (1, 4), (1, 9), -- Dune 2: Action, Sci-Fi, Fantasy
(2, 2), (2, 7), (2, 8), -- KFP4: Comedy, Animation, Adventure
(3, 1), (3, 4), (3, 8), -- GxK: Action, Sci-Fi, Adventure
(4, 2), (4, 9), (4, 5), -- Ghostbusters: Comedy, Fantasy, Thriller
(5, 1), (5, 2), (5, 8), -- The Fall Guy: Action, Comedy, Adventure
(6, 2), (6, 7), (6, 9), -- IF: Comedy, Animation, Fantasy
(7, 1), (7, 4), (7, 8), -- Furiosa: Action, Sci-Fi, Adventure
(8, 1), (8, 2), (8, 5), -- Bad Boys: Action, Comedy, Thriller
(9, 7), (9, 2), (9, 8), -- Inside Out 2: Animation, Comedy, Adventure
(10, 5), (10, 4); -- A Quiet Place: Thriller, Sci-Fi


-- Dữ liệu cho bảng movie_people (20 bản ghi)
INSERT INTO movie_people (movie_id, person_id, role_type) VALUES
(1, 16, 'Director'), -- Dune 2 - Denis Villeneuve
(1, 4, 'Main Actor'), -- Dune 2 - Leonardo DiCaprio (placeholder)
(2, 7, 'Main Actor'), -- KFP4 - Dwayne Johnson (placeholder)
(3, 7, 'Main Actor'), -- GxK - Dwayne Johnson
(3, 13, 'Director'), -- GxK - James Cameron (placeholder)
(4, 1, 'Main Actor'), -- Ghostbusters - Tom Hanks (placeholder)
(5, 7, 'Main Actor'), -- The Fall Guy - Dwayne Johnson
(6, 6, 'Main Actor'), -- IF - Emma Stone
(7, 18, 'Director'), -- Furiosa - Ridley Scott (placeholder)
(7, 19, 'Main Actor'), -- Furiosa - Margot Robbie
(8, 7, 'Main Actor'), -- Bad Boys - Dwayne Johnson (placeholder)
(9, 6, 'Voice Actor'), -- Inside Out 2 - Emma Stone (placeholder)
(10, 3, 'Main Actor'), -- A Quiet Place - Scarlett Johansson (placeholder)
(11, 1, 'Supporting Actor'), -- Spider-Man - Tom Hanks (as Iron Man, placeholder)
(12, 3, 'Main Actor'), -- Avengers - Scarlett Johansson
(13, 8, 'Director'), -- Inception - Christopher Nolan
(13, 4, 'Main Actor'), -- Inception - Leonardo DiCaprio
(14, 8, 'Director'), -- Dark Knight - Christopher Nolan
(15, 1, 'Main Actor'), -- Forrest Gump - Tom Hanks
(16, 5, 'Director'); -- Pulp Fiction - Quentin Tarantino


-- Dữ liệu cho bảng cinema_halls (20 bản ghi)
INSERT INTO cinema_halls (name, rows_count, seats_per_row_max, screen_type, description) VALUES
('Hall 1 - Standard', 5, 10, 'Standard', 'A standard movie hall with comfortable seating.'),
('Hall 2 - VIP', 4, 8, 'VIP', 'A VIP hall with premium, spacious seating.'),
('Hall 3 - IMAX', 6, 12, 'IMAX', 'Our largest hall with an immersive IMAX screen and sound system.'),
('Hall 4 - 3D', 5, 9, 'Standard', 'Equipped for 3D movie viewing.'),
('Hall 5 - Couple', 3, 7, 'Standard', 'Designed for couples with spacious double seats.'),
('Hall 6 - Standard', 5, 10, 'Standard', 'A standard movie hall with comfortable seating.'),
('Hall 7 - VIP', 4, 8, 'VIP', 'A VIP hall with premium, spacious seating.'),
('Hall 8 - IMAX', 6, 12, 'IMAX', 'Our largest hall with an immersive IMAX screen and sound system.'),
('Hall 9 - 3D', 5, 9, 'Standard', 'Equipped for 3D movie viewing.'),
('Hall 10 - Couple', 3, 7, 'Standard', 'Designed for couples with spacious double seats.'),
('Hall 11 - Standard', 5, 10, 'Standard', 'A standard movie hall with comfortable seating.'),
('Hall 12 - VIP', 4, 8, 'VIP', 'A VIP hall with premium, spacious seating.'),
('Hall 13 - IMAX', 6, 12, 'IMAX', 'Our largest hall with an immersive IMAX screen and sound system.'),
('Hall 14 - 3D', 5, 9, 'Standard', 'Equipped for 3D movie viewing.'),
('Hall 15 - Couple', 3, 7, 'Standard', 'Designed for couples with spacious double seats.'),
('Hall 16 - Standard', 5, 10, 'Standard', 'A standard movie hall with comfortable seating.'),
('Hall 17 - VIP', 4, 8, 'VIP', 'A VIP hall with premium, spacious seating.'),
('Hall 18 - IMAX', 6, 12, 'IMAX', 'Our largest hall with an immersive IMAX screen and sound system.'),
('Hall 19 - 3D', 5, 9, 'Standard', 'Equipped for 3D movie viewing.'),
('Hall 20 - Couple', 3, 7, 'Standard', 'Designed for couples with spacious double seats');


-- Dữ liệu cho bảng seats (20 bản ghi)
INSERT INTO seats (hall_id, row_char, seat_num, type, is_active) VALUES
(1, 'A', 1, 'Standard', 1), (1, 'A', 2, 'Standard', 1), (1, 'A', 3, 'Standard', 1),
(1, 'B', 1, 'Standard', 1), (1, 'B', 2, 'Standard', 1), (1, 'B', 3, 'Standard', 1),
(1, 'C', 1, 'VIP', 1), (1, 'C', 2, 'VIP', 1), (1, 'C', 3, 'VIP', 1), (1, 'C', 4, 'VIP', 1),
(2, 'A', 1, 'Couple', 1), (2, 'A', 2, 'Couple', 1), (2, 'A', 3, 'Couple', 1),
(2, 'B', 1, 'Standard', 1), (2, 'B', 2, 'Standard', 1), (2, 'B', 3, 'Standard', 1),
(2, 'C', 1, 'Standard', 1), (2, 'C', 2, 'Standard', 1), (2, 'C', 3, 'Standard', 1),
(2, 'D', 1, 'VIP', 1);


-- Dữ liệu cho bảng time_slots (20 bản ghi)
INSERT INTO time_slots (start_time, slot_name) VALUES
('09:00:00', 'Morning Show'),
('11:30:00', 'Late Morning Show'),
('14:00:00', 'Afternoon Show'),
('16:30:00', 'Late Afternoon Show'),
('19:00:00', 'Evening Show'),
('21:30:00', 'Night Show'),
('10:00:00', 'Mid-Morning Show'),
('12:30:00', 'Lunch Time Show'),
('15:00:00', 'Mid-Afternoon Show'),
('17:30:00', 'Early Evening Show'),
('20:00:00', 'Prime Time Show'),
('22:30:00', 'Late Night Show'),
('08:30:00', 'Early Bird Show'),
('10:30:00', 'Mid-Morning 2'),
('13:00:00', 'Early Afternoon'),
('15:30:00', 'Mid-Afternoon 2'),
('18:00:00', 'Early Evening 2'),
('20:30:00', 'Prime Time 2'),
('09:30:00', 'Morning Show 2'),
('11:00:00', 'Late Morning Show 2');


-- Dữ liệu cho bảng users
INSERT INTO users (email, password, name, address, phone, role_id) VALUES
('admin@example.com', 'HASHED_PASSWORD_ADMIN_1', 'Nguyen Van A', '123 ABC Street, Hanoi', '0901111111', 1),
('user1@example.com', 'HASHED_PASSWORD_USER_1', 'Tran Thi B', '456 XYZ Road, HCMC', '0902222222', 2),
('staff1@example.com', 'HASHED_PASSWORD_STAFF_1', 'Le Van C', '789 DEF Lane, Da Nang', '0903333333', 3),
('user2@example.com', 'HASHED_PASSWORD_USER_2', 'Pham Thi D', '101 GHI Street, Hai Phong', '0904444444', 2),
('user3@example.com', 'HASHED_PASSWORD_USER_3', 'Hoang Van E', '202 JKL Road, Can Tho', '0905555555', 2),
('admin2@example.com', 'HASHED_PASSWORD_ADMIN_2', 'Vo Thi F', '303 MNO Lane, Hue', '0906666666', 1),
('user4@example.com', 'HASHED_PASSWORD_USER_4', 'Dang Van G', '404 PQR Street, Nha Trang', '0907777777', 2),
('staff2@example.com', 'HASHED_PASSWORD_STAFF_2', 'Bui Thi H', '505 STU Road, Da Lat', '0908888888', 3),
('user5@example.com', 'HASHED_PASSWORD_USER_5', 'Do Van I', '606 VWX Lane, Vung Tau', '0909999999', 2),
('user6@example.com', 'HASHED_PASSWORD_USER_6', 'Vu Thi K', '707 YZA Street, Bien Hoa', '0910000000', 2),
('admin3@example.com', 'HASHED_PASSWORD_ADMIN_3', 'Trinh Van L', '808 BCD Road, Quy Nhon', '0911111111', 1),
('user7@example.com', 'HASHED_PASSWORD_USER_7', 'Ngo Thi M', '909 EFG Lane, Long Xuyen', '0912222222', 2),
('staff3@example.com', 'HASHED_PASSWORD_STAFF_3', 'Nguyen Van N', '111 HIJ Street, Rach Gia', '0913333333', 3),
('user8@example.com', 'HASHED_PASSWORD_USER_8', 'Le Thi O', '222 KLM Road, Phan Thiet', '0914444444', 2),
('user9@example.com', 'HASHED_PASSWORD_USER_9', 'Tran Van P', '333 NOP Lane, Ca Mau', '0915555555', 2),
('user10@example.com', 'HASHED_PASSWORD_USER_10', 'Ly Thi Q', '444 QRS Street, Bac Lieu', '0916666666', 2),
('user11@example.com', 'HASHED_PASSWORD_USER_11', 'Phan Van R', '555 TUV Road, Soc Trang', '0917777777', 2),
('user12@example.com', 'HASHED_PASSWORD_USER_12', 'Bui Thi S', '666 WXY Lane, Tra Vinh', '0918888888', 2),
('user13@example.com', 'HASHED_PASSWORD_USER_13', 'Dinh Van T', '777 ZAB Street, Ben Tre', '0919999999', 2),
('user14@example.com', 'HASHED_PASSWORD_USER_14', 'Duong Thi U', '888 CDE Road, My Tho', '0920000000', 2);


-- Dữ liệu cho bảng showtimes (20 bản ghi)
INSERT INTO showtimes (movie_id, hall_id, show_date, time_slot_id, base_price) VALUES
(1, 1, '2024-06-07', 5, 120000.00),
(2, 2, '2024-06-07', 3, 90000.00),
(3, 1, '2024-06-08', 6, 130000.00),
(4, 2, '2024-06-08', 1, 100000.00),
(5, 1, '2024-06-09', 2, 110000.00),
(6, 2, '2024-06-09', 4, 95000.00),
(7, 1, '2024-06-10', 5, 140000.00),
(8, 2, '2024-06-10', 3, 115000.00),
(9, 1, '2024-06-11', 1, 105000.00),
(10, 2, '2024-06-11', 6, 125000.00),
(1, 1, '2024-06-06', 1, 100000.00),
(2, 2, '2024-06-06', 2, 85000.00),
(3, 1, '2024-06-05', 3, 110000.00),
(4, 2, '2024-06-05', 4, 90000.00),
(5, 1, '2024-06-12', 4, 115000.00),
(6, 2, '2024-06-12', 5, 98000.00),
(7, 1, '2024-06-13', 2, 145000.00),
(8, 2, '2024-06-13', 1, 118000.00),
(9, 1, '2024-06-14', 3, 108000.00),
(10, 2, '2024-06-14', 6, 128000.00);


-- Dữ liệu cho bảng showtime_seat_status (20 bản ghi)
INSERT INTO showtime_seat_status (showtime_id, seat_id, status, price) VALUES
(1, 1, 'Booked', 120000.00),
(1, 2, 'Booked', 120000.00),
(1, 7, 'Booked', 150000.00),
(2, 11, 'Available', 90000.00),
(2, 12, 'Locked', 90000.00),
(3, 3, 'Available', 130000.00),
(3, 8, 'Booked', 160000.00),
(4, 13, 'Available', 100000.00),
(4, 14, 'Booked', 100000.00),
(5, 4, 'Booked', 110000.00),
(5, 9, 'Available', 140000.00),
(6, 15, 'Booked', 95000.00),
(6, 16, 'Booked', 95000.00),
(7, 5, 'Available', 140000.00),
(7, 10, 'Booked', 170000.00),
(8, 17, 'Available', 115000.00),
(8, 18, 'Booked', 115000.00),
(9, 6, 'Available', 105000.00),
(10, 19, 'Booked', 125000.00),
(10, 20, 'Booked', 155000.00);


-- Dữ liệu cho bảng products
INSERT INTO products (name, description, price, image, type) VALUES
('Popcorn Lớn', 'Bắp rang bơ lớn', 50000.00, 'https://placehold.co/100x100/cccccc/000000?text=Popcorn+L', 'Food'),
('Coca Cola Lớn', 'Coca Cola kích thước lớn', 30000.00, 'https://placehold.co/100x100/cccccc/000000?text=Coca+L', 'Beverage'),
('Combo Đôi', '1 Popcorn Lớn + 2 Coca Cola Lớn', 90000.00, 'https://placehold.co/100x100/cccccc/000000?text=Combo+Doi', 'Combo'),
('Popcorn Nhỏ', 'Bắp rang bơ nhỏ', 35000.00, 'https://placehold.co/100x100/cccccc/000000?text=Popcorn+N', 'Food'),
('Coca Cola Nhỏ', 'Coca Cola kích thước nhỏ', 20000.00, 'https://placehold.co/100x100/cccccc/000000?text=Coca+N', 'Beverage'),
('Hotdog', 'Bánh mì xúc xích', 45000.00, 'https://placehold.co/100x100/cccccc/000000?text=Hotdog', 'Food'),
('Nước Suối', 'Nước suối đóng chai', 15000.00, 'https://placehold.co/100x100/cccccc/000000?text=Nuoc+S', 'Beverage'),
('Snack Khoai Tây', 'Bánh snack khoai tây', 25000.00, 'https://placehold.co/100x100/cccccc/000000?text=Snack', 'Food'),
('Combo Gia Đình', '2 Popcorn Lớn + 4 Coca Cola Lớn + 2 Hotdog', 200000.00, 'https://placehold.co/100x100/cccccc/000000?text=Combo+GD', 'Combo'),
('Kẹo Dẻo', 'Túi kẹo dẻo', 20000.00, 'https://placehold.co/100x100/cccccc/000000?text=Keo+D', 'Food'),
('Kem', 'Cốc kem', 30000.00, 'https://placehold.co/100x100/cccccc/000000?text=Kem', 'Food'),
('Trà Sữa', 'Trà sữa trân châu', 40000.00, 'https://placehold.co/100x100/cccccc/000000?text=Tra+Sua', 'Beverage'),
('Bánh Mì Kẹp', 'Bánh mì kẹp thịt nguội', 55000.00, 'https://placehold.co/100x100/cccccc/000000?text=Banh+kep', 'Food'),
('Cà Phê Đá', 'Cà phê đá Việt Nam', 35000.00, 'https://placehold.co/100x100/cccccc/000000?text=Cafe+Da', 'Beverage'),
('Socola Thanh', 'Thanh socola', 22000.00, 'https://placehold.co/100x100/cccccc/000000?text=Socola', 'Food'),
('Nước Ép Trái Cây', 'Nước ép trái cây tươi', 40000.00, 'https://placehold.co/100x100/cccccc/000000?text=Nuoc+Ep', 'Beverage'),
('Bánh Quy', 'Gói bánh quy', 18000.00, 'https://placehold.co/100x100/cccccc/000000?text=Banh+Quy', 'Food'),
('Sữa Chua', 'Hộp sữa chua', 28000.00, 'https://placehold.co/100x100/cccccc/000000?text=Sua+Chua', 'Food'),
('Combo Hẹn Hò', '1 Popcorn Vừa + 2 Nước Suối + 1 Kẹo Dẻo', 80000.00, 'https://placehold.co/100x100/cccccc/000000?text=Combo+HH', 'Combo'),
('Khoai Tây Chiên', 'Khoai tây chiên giòn', 40000.00, 'https://placehold.co/100x100/cccccc/000000?text=Fries', 'Food');


-- Dữ liệu cho bảng combo_items (20 bản ghi)
INSERT INTO combo_items (combo_product_id, item_product_id, quantity) VALUES
(3, 1, 1),
(3, 2, 2),
(9, 1, 2),
(9, 2, 4),
(9, 6, 2),
(19, 4, 1),
(19, 7, 2),
(19, 10, 1),
(3, 6, 1),
(9, 11, 1),
(19, 12, 1),
(3, 13, 1),
(9, 14, 1),
(19, 15, 1),
(3, 16, 1),
(9, 17, 1),
(19, 18, 1),
(3, 4, 1),
(9, 5, 1),
(19, 6, 1);


-- Dữ liệu cho bảng bookings (20 bản ghi)
INSERT INTO bookings (user_id, showtime_id, booked_at, total_amount, status, payment_type, transaction_id, staff_id, notes) VALUES
(2, 1, '2024-06-06 10:00:00', 120000.00, 'Confirmed', 'OnlineBanking', 'TXN001ABC', NULL, 'User booked online.'),
(3, 2, '2024-06-06 11:00:00', 90000.00, 'Confirmed', 'CreditCard', 'TXN002DEF', NULL, NULL),
(4, 3, '2024-06-06 12:00:00', 220000.00, 'Pending', 'AtCounter', NULL, 3, 'Booking by staff for D.'),
(5, 4, '2024-06-06 13:00:00', 100000.00, 'Confirmed', 'OnlineBanking', 'TXN004GHI', NULL, NULL),
(6, 5, '2024-06-06 14:00:00', 110000.00, 'Confirmed', 'CreditCard', 'TXN005JKL', NULL, NULL),
(7, 6, '2024-06-06 15:00:00', 95000.00, 'Confirmed', 'OnlineBanking', 'TXN006MNO', NULL, NULL),
(8, 7, '2024-06-06 16:00:00', 290000.00, 'Confirmed', 'CreditCard', 'TXN007PQR', NULL, NULL),
(9, 8, '2024-06-06 17:00:00', 115000.00, 'Pending', 'AtCounter', NULL, 8, NULL),
(10, 9, '2024-06-06 18:00:00', 105000.00, 'Confirmed', 'OnlineBanking', 'TXN009TUV', NULL, NULL),
(11, 10, '2024-06-06 19:00:00', 125000.00, 'Confirmed', 'CreditCard', 'TXN010WXY', NULL, NULL),
(12, 11, '2024-06-05 10:00:00', 100000.00, 'Attended', 'OnlineBanking', 'TXN011ZAB', NULL, 'Past booking, attended.'),
(13, 12, '2024-06-05 11:00:00', 85000.00, 'Attended', 'CreditCard', 'TXN012CDE', NULL, 'Past booking, attended.'),
(14, 13, '2024-06-04 12:00:00', 110000.00, 'Cancelled', 'OnlineBanking', 'TXN013FGH', NULL, 'User cancelled.'),
(15, 14, '2024-06-04 13:00:00', 90000.00, 'Attended', 'CreditCard', 'TXN014IJK', NULL, NULL),
(16, 15, '2024-06-07 09:00:00', 115000.00, 'Pending', 'OnlineBanking', NULL, NULL, NULL),
(17, 16, '2024-06-07 10:00:00', 98000.00, 'Confirmed', 'CreditCard', 'TXN016OPQ', NULL, NULL),
(18, 17, '2024-06-07 11:00:00', 145000.00, 'Confirmed', 'OnlineBanking', 'TXN017RST', NULL, NULL),
(19, 18, '2024-06-07 12:00:00', 118000.00, 'Pending', 'AtCounter', NULL, 3, 'Staff booked.'),
(20, 19, '2024-06-07 13:00:00', 108000.00, 'Confirmed', 'CreditCard', 'TXN019XYZ', NULL, NULL),
(2, 20, '2024-06-07 14:00:00', 128000.00, 'Confirmed', 'OnlineBanking', 'TXN020123', NULL, NULL);


-- Dữ liệu cho bảng booking_tickets (20 bản ghi)
INSERT INTO booking_tickets (booking_id, showtime_seat_id, ticket_code, price_at_booking) VALUES
(1, 1, 'TKT001-A1', 120000.00),
(1, 2, 'TKT001-A2', 120000.00),
(2, 11, 'TKT002-A1', 90000.00),
(3, 3, 'TKT003-A3', 130000.00),
(3, 8, 'TKT003-C2', 160000.00),
(4, 13, 'TKT004-A3', 100000.00),
(5, 4, 'TKT005-B1', 110000.00),
(6, 15, 'TKT006-B2', 95000.00),
(7, 5, 'TKT007-B2', 140000.00),
(7, 10, 'TKT007-C4', 170000.00),
(8, 17, 'TKT008-C1', 115000.00),
(9, 6, 'TKT009-B3', 105000.00),
(10, 19, 'TKT010-C3', 125000.00),
(11, 7, 'TKT011-C1', 150000.00),
(12, 12, 'TKT012-A2', 90000.00),
(13, 14, 'TKT013-B1', 100000.00),
(14, 16, 'TKT014-B3', 95000.00),
(15, 9, 'TKT015-C3', 140000.00),
(16, 18, 'TKT016-C2', 115000.00),
(17, 20, 'TKT017-D1', 155000.00);


-- Dữ liệu cho bảng booking_products (20 bản ghi)
INSERT INTO booking_products (booking_id, product_id, quantity, price_per_unit_at_booking) VALUES
(1, 1, 1, 50000.00),
(1, 2, 1, 30000.00),
(2, 4, 1, 35000.00),
(2, 5, 1, 20000.00),
(3, 3, 1, 90000.00),
(4, 6, 1, 45000.00),
(5, 7, 2, 15000.00),
(6, 8, 1, 25000.00),
(7, 9, 1, 200000.00),
(8, 10, 1, 20000.00),
(9, 11, 1, 30000.00),
(10, 12, 1, 40000.00),
(11, 1, 1, 50000.00),
(12, 2, 1, 30000.00),
(13, 3, 1, 90000.00),
(14, 4, 1, 35000.00),
(15, 6, 1, 45000.00),
(16, 7, 1, 15000.00),
(17, 8, 1, 25000.00),
(18, 9, 1, 200000.00);


-- Dữ liệu cho bảng reviews (20 bản ghi)
INSERT INTO reviews (user_id, movie_id, booking_id, rating, comment, is_approved) VALUES
(12, 11, 11, 5, 'Phim Người Nhện rất hay, kỹ xảo đẹp mắt!', 1),
(13, 12, 12, 4, 'Endgame hoành tráng, nhưng hơi dài.', 1),
(14, 13, 13, 5, 'Inception đúng là một siêu phẩm cân não!', 1),
(15, 14, 14, 5, 'The Dark Knight quá đỉnh, Joker ám ảnh.', 1),
(2, 1, 1, 4, 'Dune 2 hình ảnh đẹp, nhưng hơi khó hiểu.', 1),
(3, 2, 2, 3, 'Kung Fu Panda 4 khá vui, nhưng không bằng các phần trước.', 1),
(4, 3, 4, 4, 'Godzilla x Kong đánh nhau mãn nhãn.', 1),
(5, 4, 5, 3, 'Ghostbusters vẫn giữ được chất hài, giải trí.', 1),
(6, 5, 6, 4, 'The Fall Guy rất hành động và hài hước.', 0),
(7, 6, 7, 5, 'IF là một bộ phim dễ thương và ý nghĩa.', 1),
(8, 7, 8, 5, 'Furiosa quá tuyệt vời, xứng đáng mong đợi.', 0),
(9, 8, 9, 4, 'Bad Boys vẫn giữ phong độ, rất bùng nổ.', 1),
(10, 9, 10, 4, 'Inside Out 2 lại một lần nữa chạm đến cảm xúc.', 1),
(11, 10, 15, 3, 'A Quiet Place: Day One khá căng thẳng.', 0),
(12, 15, 16, 5, 'Forrest Gump là một bộ phim kinh điển.', 1),
(13, 16, 17, 5, 'Pulp Fiction xem lại vẫn cuốn hút.', 1),
(14, 17, 18, 4, 'Avatar xem lại vẫn đẹp, 3D sống động.', 1),
(15, 18, 19, 5, 'Titanic là bộ phim tình cảm lãng mạn nhất mọi thời đại.', 1),
(16, 19, 20, 4, 'Spirited Away có thế giới quan rất độc đáo.', 0),
(17, 20, 1, 4, 'Toy Story luôn là tuổi thơ, rất đáng xem.', 1);
