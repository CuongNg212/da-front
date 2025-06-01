package jwd.practice.shopservice.controller;


import com.turkraft.springfilter.boot.Filter;
import jwd.practice.shopservice.dto.request.OrderCreate;
import jwd.practice.shopservice.dto.request.OrderUpdateUser;
import jwd.practice.shopservice.dto.response.OrderResponse;
import jwd.practice.shopservice.dto.response.ResultPaginationDTO;
import jwd.practice.shopservice.entity.TempOrder;
import jwd.practice.shopservice.service.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/admin/list")
    public ResponseEntity<ResultPaginationDTO> getOrdersForAdmin (Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersForAdmin(pageable));
    }

    @GetMapping("/admin/index/list")
    public ResponseEntity<ResultPaginationDTO> getOrdersForAdminIndex (Pageable pageable) {
        Pageable sortedByCreateAt = PageRequest.of(pageable.getPageNumber(), 5, Sort.by("createdAt").descending());

        return ResponseEntity.ok(orderService.getOrdersForAdmin(sortedByCreateAt));
    }

    @GetMapping("/orderDetail/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable("orderId") Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderByOrderId(orderId));
    }
    // Xem tất cả các đơn hàng theo username
    @GetMapping("/{userId}")
    public ResponseEntity<ResultPaginationDTO> getOrderByUserId(@PathVariable("userId") int userId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "5") int size
                                                                )
    {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getOrderByUserId(userId, pageable));
    }

    // Tạo đơn hàng
    @PostMapping("/create/{userId}")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable("userId") int userId, @RequestBody OrderCreate order) {
        return ResponseEntity.ok(orderService.createOrder(userId,order));
    }

    // Cập nhật đơn hàng
    @PutMapping("/updateOrder/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Integer orderId, @RequestBody OrderUpdateUser orderUpdateUser)
    {
        return ResponseEntity.ok(orderService.updateOrder(orderId,orderUpdateUser));
    }

    // Xóa đơn hàng theo ID
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.deleteOrder(id)? "Xóa thành công" : "Xóa không thành công");
    }

    // Hủy đơn hàng
    @PutMapping("/cancelOrder/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Integer orderId) {
        String result = "Failed";
        if(orderService.cancelOrder(orderId)) result = "Successed";
        return ResponseEntity.ok(result);
    }

    //  Cập nhật trạng thái đơn hàng
    @PutMapping("/admin/statusOrder/{id}")
    public ResponseEntity<OrderResponse> getOrderStatus(@PathVariable Integer id, @RequestParam("status") String status) {
        return ResponseEntity.ok(orderService.changeOrderStatus(id, status));
    }
    @PutMapping("/admin/paymentStatus/{id}")
    public ResponseEntity<OrderResponse> getOrderPaymentStatus(@PathVariable Integer id, @RequestParam("paymentStatus") String paymentStatus) {
        return ResponseEntity.ok(orderService.changePaymentStatus(id, paymentStatus));
    }

    @GetMapping("/listOrderByStatus/{username}") //
    public ResponseEntity<ResultPaginationDTO> getOrdersByStatus(@PathVariable String username, @RequestParam("status") String status, Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrderByUserNameAndStatusOrder(username, status, pageable));
    }
    @GetMapping("/admin/listOrderByStatus")
    public ResponseEntity<ResultPaginationDTO> getOrdersByStatusForAdmin(@RequestParam("status") String status, Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrderByStatusOrder(status, pageable));
    }

    @GetMapping("/listOrderByPaymentStatus/{username}")
    public ResponseEntity<ResultPaginationDTO> getOrderByUserNameAndPaymentStatus(@PathVariable String username, @RequestParam("paymentStatus") String paymentStatus, Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrderByUserNameAndPaymentStatus(username, paymentStatus, pageable));
    }
    @GetMapping("/admin/listOrderByPaymentStatus")
    public ResponseEntity<ResultPaginationDTO> getOrderByPaymentStatus(@RequestParam("paymentStatus") String paymentStatus, Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrderByPaymentStatus(paymentStatus, pageable));
    }

    // loc gia tri don hang
    @GetMapping("/admin/getDay")
    public ResponseEntity<List<BigDecimal>> getOrderByDay(@RequestParam(name = "year") int year, @RequestParam(name = "month") int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        List<BigDecimal> revenues = new ArrayList<>();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            //BigDecimal revenue = orderRepository.getTotalOrderAmountByDate(java.sql.Date.valueOf(date));

            BigDecimal revenue =  orderService.getSumByDay(date.toString());
            revenues.add(revenue);
        }
        return ResponseEntity.ok(revenues);
    }

    @GetMapping("/admin/getMonth")
    public ResponseEntity<List<BigDecimal>> getOrderByMonth(@RequestParam(name = "year") int year) {
        List<BigDecimal> revenues = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            BigDecimal revenue =  orderService.getSumByMonth(month, year);
            revenues.add(revenue);
        }
        return ResponseEntity.ok( revenues);
    }

    @GetMapping("/admin/getYear")
    public ResponseEntity<List<BigDecimal>> getOrderByYear() {
        int currentYear = LocalDate.now().getYear();
        List<BigDecimal> revenues = new ArrayList<>();
        for (int year = currentYear - 6; year <= currentYear; year++) {
            BigDecimal revenue =  orderService.getSumByYear(year);
            revenues.add(revenue);
        }
        return ResponseEntity.ok(revenues);
    }

    @GetMapping("/admin/temp-order")
    public ResponseEntity<ResultPaginationDTO> getAllTempOrder(@Filter Specification<TempOrder> spec, Pageable pageable)
    {
        return ResponseEntity.ok(this.orderService.getAllTempOrder(spec,pageable));
    }
    @GetMapping("/admin/getAllTempOrder")
    public ResponseEntity<List<TempOrder>> getAllTemp()
    {
        return ResponseEntity.ok(this.orderService.getAllTemp());
    }

}

