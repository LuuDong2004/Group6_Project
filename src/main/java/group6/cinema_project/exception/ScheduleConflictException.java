package group6.cinema_project.exception;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Exception thrown when there is a schedule conflict in the cinema system.
 * This occurs when trying to schedule a movie in a screening room that already
 * has an overlapping schedule during the same time period.
 */
public class ScheduleConflictException extends RuntimeException {

    private final Integer screeningRoomId;
    private final String screeningRoomName;
    private final LocalDate screeningDate;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final List<ConflictingSchedule> conflictingSchedules;

    public ScheduleConflictException(String message) {
        super(message);
        this.screeningRoomId = null;
        this.screeningRoomName = null;
        this.screeningDate = null;
        this.startTime = null;
        this.endTime = null;
        this.conflictingSchedules = null;
    }

    public ScheduleConflictException(String message,
                                     Integer screeningRoomId,
                                     String screeningRoomName,
                                     LocalDate screeningDate,
                                     LocalTime startTime,
                                     LocalTime endTime,
                                     List<ConflictingSchedule> conflictingSchedules) {
        super(message);
        this.screeningRoomId = screeningRoomId;
        this.screeningRoomName = screeningRoomName;
        this.screeningDate = screeningDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.conflictingSchedules = conflictingSchedules;
    }

    public Integer getScreeningRoomId() {
        return screeningRoomId;
    }

    public String getScreeningRoomName() {
        return screeningRoomName;
    }

    public LocalDate getScreeningDate() {
        return screeningDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public List<ConflictingSchedule> getConflictingSchedules() {
        return conflictingSchedules;
    }

    /**
     * Inner class to represent a conflicting schedule
     */
    public static class ConflictingSchedule {
        private final Integer scheduleId;
        private final String movieName;
        private final LocalTime startTime;
        private final LocalTime endTime;

        public ConflictingSchedule(Integer scheduleId, String movieName, LocalTime startTime, LocalTime endTime) {
            this.scheduleId = scheduleId;
            this.movieName = movieName;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public Integer getScheduleId() {
            return scheduleId;
        }

        public String getMovieName() {
            return movieName;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        @Override
        public String toString() {
            return String.format("%s (%s - %s)", movieName, startTime, endTime);
        }
    }

    /**
     * Generate a detailed error message for display to users
     */
    public String getDetailedMessage() {
        if (conflictingSchedules == null || conflictingSchedules.isEmpty()) {
            return getMessage();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Xung đột lịch chiếu trong phòng ").append(screeningRoomName != null ? screeningRoomName : screeningRoomId)
                .append(" vào ngày ").append(screeningDate)
                .append(" từ ").append(startTime).append(" đến ").append(endTime).append(".\n");

        sb.append("Các lịch chiếu bị xung đột:\n");
        for (ConflictingSchedule conflict : conflictingSchedules) {
            sb.append("- ").append(conflict.toString()).append("\n");
        }

        return sb.toString();
    }
}