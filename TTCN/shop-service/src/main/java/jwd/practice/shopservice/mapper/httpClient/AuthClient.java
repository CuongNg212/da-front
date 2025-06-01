package jwd.practice.shopservice.mapper.httpClient;

import jwd.practice.shopservice.dto.response.ApiResponse;
import jwd.practice.shopservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "authentication-service", url = "http://localhost:8080/authentication")
public interface AuthClient {
    @GetMapping("/user/{id}")
    ApiResponse<UserResponse> getUserById(@PathVariable("id") int id);
}