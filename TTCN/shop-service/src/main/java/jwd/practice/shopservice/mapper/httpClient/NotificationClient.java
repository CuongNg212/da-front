package jwd.practice.shopservice.mapper.httpClient;

import jwd.practice.shopservice.dto.request.EmailRequest;
import jwd.practice.shopservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "http://localhost:8082/notification")
public interface NotificationClient {
    @PostMapping("/email/send")
    ApiResponse<String> sendEmail(@RequestBody EmailRequest emailRequest);
}
