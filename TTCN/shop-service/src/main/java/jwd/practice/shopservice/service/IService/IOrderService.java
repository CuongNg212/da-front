package jwd.practice.shopservice.service.IService;


import jwd.practice.shopservice.dto.request.OrderCreate;
import jwd.practice.shopservice.dto.request.OrderUpdateUser;
import jwd.practice.shopservice.dto.response.OrderResponse;
import jwd.practice.shopservice.dto.response.ResultPaginationDTO;
import jwd.practice.shopservice.entity.TempOrder;
import jwd.practice.shopservice.entity.order.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public interface IOrderService {

    ResultPaginationDTO getOrdersForAdmin(Pageable pageable);

    ResultPaginationDTO getOrderByUsername(String username, Pageable pageable);

    OrderResponse createOrder(int userId, OrderCreate orderCreate);

    OrderResponse updateOrder(Integer orderId, OrderUpdateUser orderUpdateUser);

    boolean deleteOrder(Integer orderId);

    OrderResponse changeOrderStatus(Integer orderId, String orderStatus);

    OrderResponse changePaymentStatus(Integer orderId, String paymentStatus);

    boolean cancelOrder(Integer orderId);

    ResultPaginationDTO getOrderByUserNameAndStatusOrder(String username, String status, Pageable pageable);

    ResultPaginationDTO getOrderByStatusOrder(String status, Pageable pageable);

    ResultPaginationDTO  getOrderByUserNameAndPaymentStatus(String username, String paymentStatus, Pageable pageable);

    ResultPaginationDTO getOrderByPaymentStatus(String payMentStatus, Pageable pageable);

    ResultPaginationDTO getOrderByUserId(int userId, Pageable pageable);

    OrderResponse getOrderByOrderId(Integer orderId);

    BigDecimal getSumByDay(String date);

    BigDecimal getSumByMonth(int month, int year);

    BigDecimal getSumByYear(int year);

    void handleAfterCreateOrder(int orderId);

    ResultPaginationDTO getAllTempOrder(Specification<TempOrder> spec, Pageable pageable);

    List<TempOrder> getAllTemp();

    Order findOrderByOrderId(Integer orderId);
}
