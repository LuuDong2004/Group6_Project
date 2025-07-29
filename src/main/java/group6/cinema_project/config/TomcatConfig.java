package group6.cinema_project.config;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình Tomcat để xử lý multipart request với nhiều file và parameter
 */
@Configuration
public class TomcatConfig {

    /**
     * Cấu hình Tomcat để tăng giới hạn file count trong multipart request
     * Giải quyết lỗi FileCountLimitExceededException khi form có nhiều input
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers(connector -> {
                // Tăng giới hạn số lượng file trong multipart request
                connector.setProperty("maxFileCount", "1000");
                
                // Tăng giới hạn số lượng parameter
                connector.setProperty("maxParameterCount", "10000");
                
                // Tăng kích thước tối đa cho form POST
                connector.setProperty("maxPostSize", "52428800"); // 50MB
            });
        };
    }
}
