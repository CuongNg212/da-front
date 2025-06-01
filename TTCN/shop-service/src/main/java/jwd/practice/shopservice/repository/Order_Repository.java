package jwd.practice.shopservice.repository;


import jwd.practice.shopservice.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;


@Repository
public interface Order_Repository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {

    @Query("select o from Order o where o.userId = :userId")
    Page<Order> findByUserId(@Param("userId") int userId, Pageable pageable);

    @Query("select o from Order o where o.id = :id")
    Order findByOrderId(@Param("id") Integer orderId);
    /*
        Page<Order> findByUser_Username(String username, Pageable pageable);

    Page<Order> findByUser_UsernameAndStatus(String username, OrderStatus status, Pageable pageable);

        @Query("SELECT o FROM Order o WHERE o.user.username = :username AND o.status IN (:statuses)")
    List<Order> findOrdersByUsernameAndStatuses(@Param("username") String username, @Param("statuses") List<OrderStatus> statuses);

        @Query(value = "SELECT * FROM orders o WHERE o.user_id = (SELECT u.user_id FROM users u WHERE u.username = :username) AND o.status IN (:statuses)", nativeQuery = true)
    List<Order> findOrdersByUsernameAndStatusesNative(@Param("username") String username, @Param("statuses") List<String> statuses);

    @Query(value = "SELECT o FROM Order o WHERE o.user.username = :username AND o.paymentStatus = :status")
    List<Order> findByUser_UsernameAndPaymentStats(@Param("username") String username, @Param("status") PaymentStatus paymentStatus);

     */

    // Tổng tiền theo ngày
    @Query(value = "SELECT SUM(o.total_price) FROM shop.orders o WHERE DATE(o.created_at) = :date", nativeQuery = true)
    BigDecimal getTotalOrderAmountByDate(@Param("date") java.sql.Date date);


    // Tổng tiền theo các tháng trong năm
    @Query(value = "SELECT COALESCE(SUM(o.total_price), 0) " +
            "FROM shop.orders o " +
            "WHERE MONTH(o.created_at) = :month AND YEAR(o.created_at) = :year", nativeQuery = true)
    BigDecimal getTotalOrderAmountByMonth(@Param("month") int month, @Param("year") int year);

    // Tổng tiền theo các năm (6 năm trước và năm hiện tại)
    @Query(value = "SELECT COALESCE(SUM(o.total_price), 0) " +
            "FROM shop.orders o " +
            "WHERE YEAR(o.created_at) = :year", nativeQuery = true)
    BigDecimal getTotalOrderAmountByYear(@Param("year") int year);

//    // Tổng tiền theo tháng
//    @Query(value = "SELECT SUM(o.total_price) " +
//            "FROM ananas.orders o " +
//            "WHERE YEAR(o.created_at) = :year AND MONTH(o.created_at) = :month", nativeQuery = true)
//    List<BigDecimal> getTotalOrderAmountByMonth(@Param("year") int year, @Param("month") int month);
//
//    // Tổng tiền theo năm
//    @Query(value = "SELECT SUM(o.total_price) " +
//            "FROM ananas.orders o " +
//            "WHERE YEAR(o.created_at) = :year", nativeQuery = true)
//    List<BigDecimal> getTotalOrderAmountByYear(@Param("year") int year);

    // tim user theo don hang
//    @Query(value = "SELECT u.* " +
//            "FROM ananas.user u " +
//            "INNER JOIN ananas.orders o ON u.user_id = o.user_id " +
//            "WHERE o.id = :orderId", nativeQuery = true)
//    User findUserEmailByOrderId(@Param("orderId") Integer orderId);
}
