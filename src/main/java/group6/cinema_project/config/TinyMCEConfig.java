package group6.cinema_project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration class cho TinyMCE Editor.
 * Quản lý API key và các cấu hình liên quan đến TinyMCE.
 */
@Component
public class TinyMCEConfig {

    @Value("${tinymce.api.key}")
    private String apiKey;

    /**
     * Lấy API key cho TinyMCE.
     * 
     * @return API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Lấy URL CDN TinyMCE với API key.
     * 
     * @return URL CDN đầy đủ
     */
    public String getTinyMCEUrl() {
        return "https://cdn.tiny.cloud/1/" + apiKey + "/tinymce/6/tinymce.min.js";
    }
}
