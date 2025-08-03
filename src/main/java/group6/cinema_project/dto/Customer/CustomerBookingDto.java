package group6.cinema_project.dto.Customer;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import group6.cinema_project.dto.BookedFoodDto;

/**
 * DTO cho Customer xem thông tin booking của mình
 * Chỉ chứa thông tin cần thiết cho customer
 */
@Data
@Getter
@Setter
public class CustomerBookingDto {
    private Integer id;
    private String code;
    private Integer amount;
    private String status;
    private LocalDate date;
    private Date expiryDate; // ngày hiệu lực của booking
    private List<String> seatNames;
    private List<BookedFoodDto> foodList;
    private String voucherCode;
    
    // Thông tin phim và suất chiếu (đã được format)
    private String movieName;
    private String movieImage;
    private String movieDuration; // Đã format "2h 30m"
    private String movieRating;
    private String movieGenre;
    
    // Thông tin rạp và phòng chiếu
    private String branchName;
    private String branchAddress;
    private String screeningRoomName;
    
    // Thông tin suất chiếu (đã được format)
    private String screeningDate; // Đã format "Thứ 2, 15/08/2024"
    private String screeningTime; // Đã format "19:30"
    private String endTime; // Thời gian kết thúc dự kiến
    
    // Thông tin thanh toán
    private String paymentStatus; // Trạng thái thanh toán
    private String paymentMethod; // Phương thức thanh toán
    private LocalDateTime paymentDateTime; // Thời gian thanh toán
    private Double originalAmount; // Số tiền gốc
    private Double discountAmount; // Số tiền giảm giá
    private Double finalAmount; // Số tiền cuối cùng
    
    // Thông tin trạng thái
    private boolean canCancel; // Có thể hủy hay không
    private boolean canRefund; // Có thể hoàn tiền hay không
    private String statusDisplay; // Trạng thái hiển thị cho user
    private String timeRemaining; // Thời gian còn lại đến suất chiếu
    
    // QR Code và vé điện tử
    private String qrCode; // Mã QR để check-in
    private boolean hasETicket; // Có vé điện tử hay không
    private String eTicketUrl; // Link download vé điện tử
    
    // Thông tin bổ sung
    private String specialRequests; // Yêu cầu đặc biệt
    private boolean isGift; // Có phải là quà tặng hay không
    private String giftMessage; // Tin nhắn quà tặng
}
