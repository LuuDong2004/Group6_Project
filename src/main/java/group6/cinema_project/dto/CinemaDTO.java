package group6.cinema_project.dto;

import group6.cinema_project.entity.Cinema;

public class CinemaDTO {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String googleMapsUrl;

    public static CinemaDTO fromEntity(Cinema cinema) {
        CinemaDTO dto = new CinemaDTO();
        dto.setName(cinema.getName());
        dto.setAddress(cinema.getAddress());
        dto.setLatitude(cinema.getLat());
        dto.setLongitude(cinema.getLng());
        dto.setGoogleMapsUrl("https://www.google.com/maps/search/?api=1&query="
            + cinema.getLat() + "," + cinema.getLng());
        return dto;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getGoogleMapsUrl() { return googleMapsUrl; }
    public void setGoogleMapsUrl(String googleMapsUrl) { this.googleMapsUrl = googleMapsUrl; }
} 