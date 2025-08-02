package group6.cinema_project.dto;

public class StatisticDto {
    private String userName;
    private String movieName;
    private long totalTickets;
    private double totalAmount;

    public StatisticDto() {}
    public StatisticDto(String userName, String movieName, long totalTickets, double totalAmount) {
        this.userName = userName;
        this.movieName = movieName;
        this.totalTickets = totalTickets;
        this.totalAmount = totalAmount;
    }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getMovieName() { return movieName; }
    public void setMovieName(String movieName) { this.movieName = movieName; }
    public long getTotalTickets() { return totalTickets; }
    public void setTotalTickets(long totalTickets) { this.totalTickets = totalTickets; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
