package jwd.practice.userservice.controller;


import jwd.practice.userservice.dto.request.ChangePasswordRequest;
import jwd.practice.userservice.dto.request.ForgotPasswordRequest;
import jwd.practice.userservice.dto.request.UserCreateRequest;
import jwd.practice.userservice.dto.request.UserUpdateRequest;
import jwd.practice.userservice.dto.response.ApiResponse;
import jwd.practice.userservice.dto.response.UserResponse;
import jwd.practice.userservice.service.serviceImpl.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/create")
    public ApiResponse<UserResponse> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Success")
                .result(userService.createUser(userCreateRequest))
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .code(200)
                .message("Success")
                .result(userService.getAllUser())
                .build();
    }

    @GetMapping("/my-info")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(userService.getMyInfo())
                .build() ;
    }
    

    // cach 1
//    @GetMapping("{id}")
//    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") int id) {
//        return ResponseEntity.ok(userService.getUserById(id));
//    }

    // cach 2
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable("id") int id) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Success")
                .result(userService.getUserById(id))
                .build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable("id") int id, @RequestBody UserUpdateRequest userUpdateRequest) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Success")
                .result(userService.updateUser(id, userUpdateRequest))
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteUser(@PathVariable("id") int id) {
        userService.deleteUserById(id);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Success")
                .result("Deleted")
                .build();
    }

    @PutMapping("/photo/{id}")
    public ApiResponse<UserResponse> updateUserPhoto(@PathVariable("id") int id,@RequestParam("avatar") MultipartFile file) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.uploadAvatar(id, file))
                .code(200)
                .build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        userService.forgotPassword(forgotPasswordRequest.getEmail());
        return ResponseEntity.ok("Verification code sent to your email.");
    }

    @PutMapping("/change-password/{userId}")
    public ResponseEntity<String> resetPassword(@PathVariable(name = "userId") int userId,@RequestBody ChangePasswordRequest changePasswordRequest) {
        boolean isReset = userService.changePassword(userId,changePasswordRequest);
        if (isReset) {
            return ResponseEntity.ok("Password has been reset successfully.");
        } else {
            return ResponseEntity.badRequest().body("non");
        }
    }

    @GetMapping("/getSumUser")
    public ResponseEntity<List<BigDecimal>> getNumberUser(@RequestParam(name = "year") int year, @RequestParam(name = "month") int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        List<BigDecimal> revenues = new ArrayList<>();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            BigDecimal revenue = userService.getNumberUsers(date.toString());
            revenues.add(revenue);
        }
        return ResponseEntity.ok(revenues);
    }

}
