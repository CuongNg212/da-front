package jwd.practice.shopservice.controller;


import jakarta.servlet.http.HttpServletResponse;
import jwd.practice.shopservice.entity.TempOrder;
import jwd.practice.shopservice.entity.order.Order;
import jwd.practice.shopservice.service.Service.CartService;
import jwd.practice.shopservice.service.Service.OrderService;
import jwd.practice.shopservice.service.Service.TempOrderService;
import jwd.practice.shopservice.service.Service.VnpayService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@RestController
@AllArgsConstructor
public class PaymentController {

    VnpayService vnpayService;
    TempOrderService tempOrderService;
    OrderService orderService;
    CartService cartService;

    @GetMapping("/createPayment")
    public String createPayment(@RequestParam String orderInfo, @RequestParam long amount, @RequestParam int orderId)   {
        try {
            // lưu thông tin order vào bảng tạm để đối chiếu xử lý sau khi thanh toán
            String result = vnpayService.createPaymentURL(orderInfo, amount);
            TempOrder tempOrder = new TempOrder();
            tempOrder.setOrderId(orderId);
            tempOrder.setTxnRef(vnpayService.code);
            tempOrder.setSumPrice(amount);
            this.tempOrderService.save(tempOrder);
            return result ;
        }
        catch (Exception e){
           return "xay ra loi: " + e.getMessage().toString();
        }
    }

    @GetMapping("/vnpay-return")
    public void vnpayReturn(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");

        // In ra các tham số nhận được
        System.out.println("Received params: " + params);
        StringBuilder hashData = new StringBuilder();
        params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    try {
                        if (entry.getValue() != null) {
                            hashData.append(entry.getKey()).append("=")
                                    .append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                });

        // Loại bỏ ký tự '&' cuối cùng
        if (hashData.length() > 0) {
            hashData.setLength(hashData.length() - 1);
        }

        // Tính toán mã hash
        String computedHash = vnpayService.HMACSHA512(vnpayService.getHashSecret(), hashData.toString());

        // In ra các thông tin cần thiết để kiểm tra
        System.out.println("Computed Hash: " + computedHash);
        System.out.println("Received Secure Hash: " + vnp_SecureHash);

        if (computedHash.equals(vnp_SecureHash)) {
            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef");
            if ("00".equals(vnp_ResponseCode)) {
                TempOrder tempOrder = this.tempOrderService.findByTxnRef(vnp_TxnRef);
                this.orderService.changePaymentStatus(tempOrder.getOrderId(), "paid");
                Order order = orderService.findOrderByOrderId(tempOrder.getOrderId());
                this.cartService.deleteCart(order.getUserId());
                this.orderService.handleAfterCreateOrder(tempOrder.getOrderId());
                tempOrder.setStatus("success");
                this.tempOrderService.save(tempOrder);
                // Chuyển hướng tới trang thành công
                response.sendRedirect("http://localhost:5500/success.html");
            } else {
                TempOrder tempOrder = this.tempOrderService.findByTxnRef(vnp_TxnRef);
                this.orderService.deleteOrder(tempOrder.getOrderId());
                tempOrder.setStatus("cancel");
                // Chuyển hướng tới trang thất bại
                response.sendRedirect("http://localhost:5500/fail.html");
            }
        } else {
            // Chữ ký không hợp lệ
            response.sendRedirect("http://localhost:5500/fail.html");
        }
    }



}