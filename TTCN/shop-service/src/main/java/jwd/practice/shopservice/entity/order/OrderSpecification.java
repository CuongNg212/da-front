package jwd.practice.shopservice.entity.order;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification implements Specification<Order> {
    private String username;
    private String status; // thêm trường cho trạng thái đơn hàng
    private String paymentStatus; // thêm trường cho trạng thái thanh toán

    public OrderSpecification(String username, String status, String paymentStatus) {
        this.username = username;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }
    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction(); // Khởi tạo Predicate chung

        // Kiểm tra username
        if (username != null && !username.isEmpty()) {
            predicate = cb.and(predicate, cb.equal(root.get("user").get("username"), username));
        }

        // Kiểm tra status
        if (status != null && !status.isEmpty()) {
            OrderStatus orderStatus = OrderStatus.PENDING;
            if(status.equalsIgnoreCase(OrderStatus.SHIPPED.name())) orderStatus = OrderStatus.SHIPPED;
            if(status.equalsIgnoreCase(OrderStatus.DELIVERED.name())) orderStatus = OrderStatus.DELIVERED;
            if(status.equalsIgnoreCase(OrderStatus.CANCELED.name())) orderStatus = OrderStatus.CANCELED;
            predicate = cb.and(predicate, cb.equal(root.get("status"), orderStatus));
        }

        // Kiểm tra payment status
        if (paymentStatus != null && !paymentStatus.isEmpty()) {
            PaymentStatus paymentStat =  PaymentStatus.UNPAID;
            if(paymentStatus.equalsIgnoreCase(PaymentStatus.PAID.name())) paymentStat = PaymentStatus.PAID;
            predicate = cb.and(predicate, cb.equal(root.get("paymentStatus"), paymentStat));
        }

        return predicate; // Trả về Predicate cuối cùng
    }
}