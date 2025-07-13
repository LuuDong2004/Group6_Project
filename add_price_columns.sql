-- Add price columns to ScreeningRoom table
ALTER TABLE ScreeningRoom 
ADD COLUMN price_standard DECIMAL(10,2) NOT NULL DEFAULT 0.00,
ADD COLUMN price_vip DECIMAL(10,2) NOT NULL DEFAULT 0.00,
ADD COLUMN price_couple DECIMAL(10,2) NOT NULL DEFAULT 0.00; 