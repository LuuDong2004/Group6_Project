package group6.cinema_project.entity.Enum;

public enum AuthProvider {
    LOCAL("LOCAL"),
    GOOGLE("GOOGLE"),
    FACEBOOK("FACEBOOK");
    private String value;

    AuthProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
