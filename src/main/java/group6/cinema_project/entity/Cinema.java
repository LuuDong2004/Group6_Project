package group6.cinema_project.entity;

public class Cinema {
    private String name;
    private String address;
    private double lat;
    private double lng;

    public Cinema(String name, String address, double lat, double lng) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }

    public double distanceTo(double userLat, double userLng) {
        // Haversine formula
        final int R = 6371; // Earth radius km
        double dLat = Math.toRadians(lat - userLat);
        double dLng = Math.toRadians(lng - userLng);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(lat))
                * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
