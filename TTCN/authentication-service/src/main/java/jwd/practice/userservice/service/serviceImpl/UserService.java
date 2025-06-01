package jwd.practice.userservice.service.serviceImpl;


import jwd.practice.userservice.dto.request.ChangePasswordRequest;
import jwd.practice.userservice.dto.request.EmailRequest;
import jwd.practice.userservice.dto.request.UserCreateRequest;
import jwd.practice.userservice.dto.request.UserUpdateRequest;
import jwd.practice.userservice.dto.response.ApiResponse;
import jwd.practice.userservice.dto.response.UserResponse;
import jwd.practice.userservice.entity.Role;
import jwd.practice.userservice.entity.User;
import jwd.practice.userservice.exception.AppException;
import jwd.practice.userservice.exception.ErrException;
import jwd.practice.userservice.mapper.IUserMapper;
import jwd.practice.userservice.repository.UserRepository;
import jwd.practice.userservice.repository.httpClient.NotificationClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    IUserMapper userMapper;
    PasswordEncoder passwordEncoder;
    NotificationClient notificationClient;

    public UserResponse createUser(UserCreateRequest userCreateRequest) {
        User user = userMapper.toUser(userCreateRequest);
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(false);

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrException.USER_EXISTED);
        }

        if (userCreateRequest.getEmail() != null && !userCreateRequest.getEmail().isEmpty()) {
            String subject = "Welcome to our service";
            String body = "Your account is: "+userCreateRequest.getUsername()+", your password is: "+userCreateRequest.getPassword();
            String email = userCreateRequest.getEmail();
            EmailRequest emailRequest = new EmailRequest(email, subject, body);
            try {
                log.info("Sending email to: {}", emailRequest.getTo());
                ApiResponse<String> response = notificationClient.sendEmail(emailRequest);
                log.info("Email send response: {}", response.getMessage());
            } catch (Exception e) {
                log.warn("Email sending failed for user {}: {}", user.getUsername(), e.getMessage());
            }
        }


        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        System.out.println("Extracted username: " + name);

        User  user = userRepository.findById(Integer.parseInt(name)).orElseThrow(() -> new AppException(ErrException.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    //@PreAuthorize("hasAuthority('CREATE_POST')")
    public List<UserResponse> getAllUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info("GrantedAuthority: {}", grantedAuthority));
        return userRepository.findAll().stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

    //@PreAuthorize("hasRole('ADMIN')")
    //@PostAuthorize("returnObject.username == authentication.name or hasRole('ADMIN')")
    public UserResponse getUserById(int id) {
        return userRepository.findById(id).map(userMapper::toUserResponse).orElseThrow(() -> new AppException(ErrException.USER_NOT_EXISTED));
    }

    public UserResponse getUserByUsername(String username) {
        return null;
    }

    public UserResponse updateUser(int userId, UserUpdateRequest userUpdateRequest) {
        User userUpdate = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrException.USER_NOT_EXISTED));
        userMapper.updateUser(userUpdate, userUpdateRequest);
        userUpdate.setUpdatedAt(LocalDateTime.now());

        return userMapper.toUserResponse(userRepository.save(userUpdate));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    public void deleteUserById(int id) {
        User userDelete = userRepository.findById(id).orElseThrow(() -> new AppException(ErrException.USER_NOT_EXISTED));
        userRepository.delete(userDelete);
    }

    private boolean isPhoto(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private String storeFile(MultipartFile file) {
        if (!isPhoto(file) || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new AppException(ErrException.NOT_FILE);
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFilename = UUID.randomUUID().toString()+"_"+fileName;
        java.nio.file.Path uploadDir = Paths.get("upload/user");
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                throw new AppException(ErrException.DIRECTORY_CREATION_FAILED);
            }
        }
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new AppException(ErrException.FILE_STORAGE_FAILED);
        }
        return uniqueFilename;
    }

    public UserResponse uploadAvatar(int id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrException.USER_NOT_EXISTED));
        String fileName = storeFile(file);
        user.setAvatar(fileName);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    // Quên mật khẩu - Gửi qua email
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String subject = "Dear, "+user.getUsername()+", your account has been reset password successfully.";
        String resetPassword = generateRandomPassword(6);

        EmailRequest emailRequest = new EmailRequest(email, subject, "\nYour new password: "+resetPassword);
        notificationClient.sendEmail(emailRequest);

        user.setPassword(passwordEncoder.encode(resetPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Hàm để tạo chuỗi ngẫu nhiên dài `length` ký tự
    private String generateRandomPassword(int length) {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allowedChars.length());
            password.append(allowedChars.charAt(index));
        }

        return password.toString();
    }

    // Đổi mật khẩu
    public boolean changePassword(int userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user != null && passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public BigDecimal getNumberUsers(String date) {
        return userRepository.getNumberOfUsersCreatedOn(date);
    }
}
