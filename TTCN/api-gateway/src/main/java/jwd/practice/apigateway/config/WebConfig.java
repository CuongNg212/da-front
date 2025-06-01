package jwd.practice.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
@Configuration
public class WebConfig {

    private static final String BASE_PATH = "E:/DATN/Cuong/upload/";
    @Bean
    public RouterFunction<ServerResponse> imageRoutes() {
        return route(GET("/images/{type}/{filename}"), this::serveImage);
    }

    private Mono<ServerResponse> serveImage(ServerRequest request) {
        String type = request.pathVariable("type");  // avatar hoặc product
        String filename = request.pathVariable("filename");

        // Chuyển đổi type từ avatar → user, product giữ nguyên
        String folderName = "product".equals(type) ? "product" : "user";

        // Tạo đường dẫn thư mục
        String basePath = "E:/DATN/Cuong/upload/" + folderName;
        Path imagePath = Paths.get(basePath, filename);

        try {
            Resource imageResource = new UrlResource(imagePath.toUri());

            // Xác định contentType dựa trên phần mở rộng
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Nếu không xác định được
            }

            return ServerResponse.ok()
                    .contentType(MediaType.parseMediaType(contentType)) // Tự động nhận diện
                    .bodyValue(imageResource);
        } catch (Exception e) {
            return ServerResponse.notFound().build();
        }
    }

}
