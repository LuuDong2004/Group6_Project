-- Add payment-related columns to Booking table
ALTER TABLE Booking
ADD payment_created_at DATETIME,
ADD payment_updated_at DATETIME,
ADD payment_gateway_response TEXT,
ADD payment_method VARCHAR(50),
ADD payment_status VARCHAR(20),
ADD transaction_ref VARCHAR(100); 