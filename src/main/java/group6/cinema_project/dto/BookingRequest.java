package group6.cinema_project.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BookingRequest {
    private Integer userId;
    private Integer scheduleId;
    private List<Integer> seatIds;  // Danh sách ID của các ghế đã chọn
    private Map<String, Integer> foodItems;  // Danh sách đồ ăn và số lượng
    private double totalAmount;  // Tổng số tiền
    private String notes;  // Ghi chú đặt vé
    private String voucherCode;
}
