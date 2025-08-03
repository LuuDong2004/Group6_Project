package group6.cinema_project.entity.Enum;

/**
 * Enum định nghĩa các trạng thái của lịch chiếu phim
 */
public enum ScheduleStatus {
    /**
     * Sắp chiếu - Lịch chiếu chưa bắt đầu
     */
    UPCOMING("UPCOMING", "Sắp chiếu"),
    
    /**
     * Đang chiếu - Lịch chiếu đang diễn ra
     */
    ACTIVE("ACTIVE", "Đang chiếu"),
    
    /**
     * Đã kết thúc - Lịch chiếu đã hoàn thành
     */
    ENDED("ENDED", "Đã kết thúc"),
    
    /**
     * Đã hủy - Lịch chiếu bị hủy bỏ
     */
    CANCELLED("CANCELLED", "Đã hủy");

    private final String value;
    private final String displayName;

    ScheduleStatus(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /**
     * Lấy giá trị string của enum
     * @return giá trị string
     */
    public String getValue() {
        return value;
    }

    /**
     * Lấy tên hiển thị của trạng thái
     * @return tên hiển thị
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Chuyển đổi từ string sang enum
     * @param value giá trị string
     * @return ScheduleStatus enum hoặc null nếu không tìm thấy
     */
    public static ScheduleStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        for (ScheduleStatus status : ScheduleStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * Kiểm tra xem trạng thái có thể chỉnh sửa được không
     * Chỉ có trạng thái UPCOMING mới có thể chỉnh sửa
     * @return true nếu có thể chỉnh sửa, false nếu không thể
     */
    public boolean isEditable() {
        return this == UPCOMING;
    }

    /**
     * Kiểm tra xem trạng thái có thể xóa được không
     * Chỉ có trạng thái UPCOMING mới có thể xóa
     * @return true nếu có thể xóa, false nếu không thể
     */
    public boolean isDeletable() {
        return this == UPCOMING;
    }

    @Override
    public String toString() {
        return value;
    }
}
