-- Thêm cột booking_id vào bảng SeatReservation
ALTER TABLE SeatReservation ADD booking_id INT;

-- Thêm foreign key constraint
ALTER TABLE SeatReservation 
ADD CONSTRAINT FK_SeatReservation_Booking 
FOREIGN KEY (booking_id) REFERENCES Booking(id);

-- Thêm index để tối ưu query
CREATE INDEX idx_seat_reservation_booking_id ON SeatReservation(booking_id); 