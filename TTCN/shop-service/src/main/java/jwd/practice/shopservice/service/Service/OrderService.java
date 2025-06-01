package jwd.practice.shopservice.service.Service;


import jwd.practice.shopservice.dto.request.EmailRequest;
import jwd.practice.shopservice.dto.request.OrderCreate;
import jwd.practice.shopservice.dto.request.OrderUpdateUser;
import jwd.practice.shopservice.dto.request.Order_Items_Create;
import jwd.practice.shopservice.dto.response.ApiResponse;
import jwd.practice.shopservice.dto.response.OrderResponse;
import jwd.practice.shopservice.dto.response.ResultPaginationDTO;
import jwd.practice.shopservice.dto.response.UserResponse;
import jwd.practice.shopservice.entity.*;
import jwd.practice.shopservice.entity.order.Order;
import jwd.practice.shopservice.entity.order.OrderSpecification;
import jwd.practice.shopservice.entity.order.OrderStatus;
import jwd.practice.shopservice.entity.order.PaymentStatus;
import jwd.practice.shopservice.entity.voucher.Voucher;
import jwd.practice.shopservice.exception.AppException;
import jwd.practice.shopservice.exception.ErrException;
import jwd.practice.shopservice.mapper.IOrderMapper;
import jwd.practice.shopservice.mapper.httpClient.AuthClient;
import jwd.practice.shopservice.mapper.httpClient.NotificationClient;
import jwd.practice.shopservice.repository.*;
import jwd.practice.shopservice.service.IService.IOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService implements IOrderService {

    Order_Repository orderRepository;
    Voucher_Repository voucherRepository;
    NotificationClient notificationClient;
    ProductVariant_Repository productVariantRepository;
    AuthClient authClient;
    Product_Repository productRepository;

    VoucherService voucherService;
    Temp_Order_Repository tempOrderRepository;

    IOrderMapper orderMapper;
    Cart_Repository cartRepository;
    Cart_Item_Repository cartItemRepository;
    @Override
    public ResultPaginationDTO getOrdersForAdmin(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        ResultPaginationDTO re  = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(orders.getTotalElements());
        mt.setPages(orders.getTotalPages());
        re.setMeta(mt);
        re.setResult(orderMapper.listOrderToOrderResponse(orders.getContent()));
       return re;
    }

    @Override
    public ResultPaginationDTO getOrderByUsername(String username, Pageable pageable) {
        OrderSpecification specification =  new OrderSpecification(username, null, null);
        Page<Order> orders = orderRepository.findAll(specification, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(orders.getTotalElements());
        mt.setPages(orders.getTotalPages());
        res.setMeta(mt);
        res.setResult(orderMapper.listOrderToOrderResponse(orders.getContent()));
        return res;
    }
    @Override
    public ResultPaginationDTO getOrderByUserId(int userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(orders.getTotalElements());
        mt.setPages(orders.getTotalPages());
        res.setMeta(mt);
        res.setResult(orderMapper.listOrderToOrderResponse(orders.getContent()));
        return res;
    }

    @Override
    public OrderResponse getOrderByOrderId(Integer orderId) {
        Order order = orderRepository.findByOrderId(orderId);
        if(order == null) {
            throw new AppException(ErrException.ORDER_NOT_EXISTED);
        }
        return orderMapper.orderToOrderResponse(order);
    }


    @Override
    public BigDecimal getSumByDay(String date) {
        LocalDate localDate = LocalDate.parse(date);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        return orderRepository.getTotalOrderAmountByDate(sqlDate);
    }


    @Override
    public BigDecimal getSumByMonth(int month, int year) {
        return orderRepository.getTotalOrderAmountByMonth(month, year);
    }

    @Override
    public BigDecimal getSumByYear(int year) {
        return orderRepository.getTotalOrderAmountByYear(year);
    }

    @Override
    public OrderResponse createOrder(int userId, OrderCreate orderCreate) {
        Order order = new Order();

        // Lấy thông tin user từ Profile Service
        UserResponse userProfile = authClient.getUserById(userId).getResult();

        // Xử lý voucher
        if (orderCreate.getCode() != null) {
            Voucher voucher = voucherRepository.findVoucherByCode(orderCreate.getCode());
            if (voucher == null) throw new AppException(ErrException.VOUCHER_NOT_EXISTED);
            order.setVoucher(voucher);
        } else {
            order.setVoucher(null);
        }

        List<Order_Items_Create> items = orderCreate.getOrderItems();
        BigDecimal sum_before = BigDecimal.ZERO;

        List<Order_Item> orderItems = new ArrayList<>();
        for (Order_Items_Create item : items) {
            ProductVariant productVariant = productVariantRepository.findById(item.getProductVariantId())
                    .orElseThrow(() -> new AppException(ErrException.ORDER_ERROR_FIND_PRODUCT));

            Order_Item orderItem = new Order_Item();
            orderItem.setProductVariant(productVariant);
            orderItem.setOrder(order);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(BigDecimal.valueOf(productVariant.getProduct().getPrice())
                    .subtract(BigDecimal.valueOf(productVariant.getProduct().getPrice())
                            .multiply(BigDecimal.valueOf(productVariant.getProduct().getDiscount())
                                    .divide(BigDecimal.valueOf(100)))));

            orderItems.add(orderItem);

            // Cập nhật số lượng đã bán
            productVariant.getProduct().setSoldQuantity(productVariant.getProduct().getSoldQuantity() + item.getQuantity());
            productRepository.save(productVariant.getProduct());

            // Cập nhật stock
            int stock = productVariant.getStock();
            if (stock < item.getQuantity()) throw new AppException(ErrException.NOT_ENOUGH_STOCK);
            productVariant.setStock(stock - item.getQuantity());
            productVariantRepository.save(productVariant);

            sum_before = sum_before.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }
        order.setTotalAmount(sum_before);

        // Áp dụng voucher nếu có
        BigDecimal sum_after;
        if (order.getVoucher() != null && voucherService.checkVoucher(order.getVoucher().getCode(), sum_before)) {
            BigDecimal discount_voucher = voucherService.applyVoucher(order.getVoucher(), sum_before);
            order.setDiscount_voucher(discount_voucher);
            sum_after = sum_before.subtract(discount_voucher);
            order.getVoucher().setUsageLimit(order.getVoucher().getUsageLimit() - 1);
            voucherRepository.save(order.getVoucher());
        } else {
            order.setVoucher(null);
            sum_after = sum_before;
        }

        StringBuilder description = new StringBuilder();
        description.append(orderCreate.getDescription());
        order.setDescription(description.toString());
        order.setTotalPrice(sum_after);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setPaymentMethod(orderCreate.getPaymentMethod());
        order.setRecipientName(orderCreate.getRecipientName());
        order.setRecipientPhone(orderCreate.getRecipientPhone());
        order.setRecipientAddress(orderCreate.getRecipientAddress());
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        order.setOrderItems(orderItems);
        order.setUserId(userId);
        orderRepository.save(order);

        // Cập nhật saleAt nếu cần
        for (Order_Item orderItem : orderItems) {
            Product product = orderItem.getProductVariant().getProduct();
            product.setSaleAt(LocalDateTime.now());
            productRepository.save(product);
        }

        // Gửi email thông báo cho user
        if (userProfile.getEmail() != null && !userProfile.getEmail().isEmpty()) {
            String subject = "You have placed your order successfully";
            String text = "\nDear " + userProfile.getUsername() + ",\nYour order is being prepared. Thank you for your order <3";
            EmailRequest emailRequest = new EmailRequest(userProfile.getEmail(), subject, text);
            ApiResponse<String> response = notificationClient.sendEmail(emailRequest);
        }

        return orderMapper.orderToOrderResponse(order);
    }



    // Cập nhật đơn hàng
    @Transactional
    @Override
    public OrderResponse updateOrder(Integer orderId, OrderUpdateUser orderUpdateUser)
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrException.ORDER_NOT_EXISTED));

        // Kiểm tra trạng thái đơn hàng
        if (order.getPaymentStatus() == PaymentStatus.PAID || order.getStatus() == OrderStatus.DELIVERED) {
            throw new AppException(ErrException.NOT_UPDATE_ORDER);
        }

        // Cập nhật thông tin đơn hàng
        order.setPaymentMethod(orderUpdateUser.getPaymentMethod());
        order.setRecipientName(orderUpdateUser.getRecipientName());
        order.setRecipientPhone(orderUpdateUser.getRecipientPhone());
        order.setRecipientAddress(orderUpdateUser.getRecipientAddress());

//        // Xóa các sản phẩm cũ
//        List<Order_Item> dataEntity = order.getOrderItems();
//        for (Order_Item temp : dataEntity) {
//            int quantity = temp.getQuantity();
//            int nowQuantity = temp.getProductVariant().getProduct().getSoldQuantity();
//            temp.getProductVariant().getProduct().setSoldQuantity(nowQuantity - quantity);
//            productRepository.save(temp.getProductVariant().getProduct());
//
//            int stock = temp.getProductVariant().getStock();
//            temp.getProductVariant().setStock(stock + temp.getQuantity());
//            productVariantRepository.save(temp.getProductVariant());
//        }
//        dataEntity.clear(); // Xóa danh sách các sản phẩm cũ
//
//        //
//        List<Order_Items_Create> newDataRequest = orderUpdateUser.getOrderItems();
//        BigDecimal sum_before = BigDecimal.valueOf(0);
//        BigDecimal sum_after;
//        for (Order_Items_Create item : newDataRequest) {
//            ProductVariant productVariant = productVariantRepository.findById(item.getProductVariantId()).get();
//            Order_Item orderItem = new Order_Item();
//            orderItem.setProductVariant(productVariant);
//            orderItem.setQuantity(item.getQuantity());
//            orderItem.setPrice(BigDecimal.valueOf(productVariant.getProduct().getPrice())
//                    .multiply(BigDecimal.valueOf(productVariant.getProduct().getDiscount())
//                            .divide(BigDecimal.valueOf(100))));
//            orderItem.setOrder(order);
//            order.addOrderItem(orderItem);
//            // Tính toán giá trị Total
//            sum_before = sum_before.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
//
//            // Cap nhat so luong da ban
//            int totalQuantity = productVariant.getProduct().getSoldQuantity();
//            productVariant.getProduct().setSoldQuantity(totalQuantity + item.getQuantity());
//            productRepository.save(productVariant.getProduct());
//
//            // Cap nhat stock
//            int stock = productVariant.getStock();
//            if(stock < item.getQuantity()) throw new AppException(ErrException.NOT_ENOUGH_STOCK);
//            productVariant.setStock(stock - item.getQuantity());
//            productVariantRepository.save(productVariant);
//        }
//        if(order.getVoucher() != null)
//        {
//            order.getVoucher().setUsageLimit(order.getVoucher().getUsageLimit()+1);
//            voucherRepository.save(order.getVoucher());
//        }
//        order.setTotalAmount(sum_before);
//        if(orderUpdateUser.getCode() != null)
//        {
//            Voucher voucher = voucherRepository.findVoucherByCode(orderUpdateUser.getCode());
//            if(voucher == null) throw new AppException(ErrException.VOUCHER_NOT_EXISTED);
//            order.setVoucher(voucher);
//        }else order.setVoucher(null);
        StringBuilder description = new StringBuilder();
//        if(order.getVoucher() != null && voucherService.checkVoucher(order.getVoucher().getCode(), sum_before))
//        {
//            BigDecimal dis_vour = voucherService.applyVoucher(order.getVoucher(), sum_before);
//            Integer vou = dis_vour.setScale(0, RoundingMode.HALF_EVEN).intValue();
//            order.setDiscount_voucher(BigDecimal.valueOf(vou));
//            sum_after = sum_before.subtract(BigDecimal.valueOf(vou));
//            order.getVoucher().setUsageLimit(order.getVoucher().getUsageLimit()-1);
//            voucherRepository.save(order.getVoucher());
//        }
//        else
//        {
//            order.setVoucher(null);
//            order.setDiscount_voucher(null);
//            sum_after = sum_before;
//        }
//        order.setTotalPrice(sum_after);
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        description.append(orderUpdateUser.getDescription());
        order.setDescription(description.toString());
        orderRepository.save(order);
        return orderMapper.orderToOrderResponse(order);
    }

    // Trong thực tế không nên xóa hẳn đơn hàng
    @Override
    public boolean deleteOrder(Integer orderId) {
        // Tìm đơn hàng theo ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrException.ORDER_NOT_EXISTED));

        // Kiểm tra trạng thái đơn hàng, ví dụ: không cho phép xóa nếu trạng thái là "đã giao" hoặc "đã thanh toán"
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new AppException(ErrException.ORDER_ERROR_STATUS);
        }
        orderRepository.delete(order);

        return true; // Trả về true nếu xóa thành công
    }

    private void updateProductSaleAt(Order order) {
        List<Order_Item> orderItems = order.getOrderItems(); // Assuming this fetches items in the order
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        for (Order_Item item : orderItems) {
            ProductVariant productVariant = item.getProductVariant(); // Assuming this gets the associated ProductVariant
            Product product = productVariant.getProduct(); // Access the associated Product
            product.setSaleAt(LocalDateTime.now());
            productRepository.save(product); // Save the updated Product entity
        }
    }

    private void resetProductSaleAt(Order order) {
        List<Order_Item> orderItems = order.getOrderItems();

        for (Order_Item orderItem : orderItems) {
            ProductVariant productVariant = orderItem.getProductVariant();
            Product product = productVariant.getProduct();

            product.setSaleAt(LocalDateTime.ofInstant(product.getCreatedAt(), ZoneOffset.UTC));
            productRepository.save(product);
        }
    }


    @Override
    public OrderResponse changeOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrException.ORDER_NOT_EXISTED));
        if(order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new AppException(ErrException.ORDER_ERROR_STATUS);
        }
        if(status.equalsIgnoreCase(OrderStatus.SHIPPED.name())) {
            order.setStatus(OrderStatus.SHIPPED);
            updateProductSaleAt(order);
        }
        if(status.equalsIgnoreCase(OrderStatus.PENDING.name())) order.setStatus(OrderStatus.PENDING);
        if(status.equalsIgnoreCase(OrderStatus.DELIVERED.name())) {
            order.setStatus(OrderStatus.DELIVERED);
            updateProductSaleAt(order);
        }
        if(status.equalsIgnoreCase(OrderStatus.CANCELED.name()))
        {
            order.setStatus(OrderStatus.CANCELED);
            resetProductSaleAt(order);
        }
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return orderMapper.orderToOrderResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse changePaymentStatus (Integer orderId, String paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrException.ORDER_NOT_EXISTED));
        if(order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new AppException(ErrException.ORDER_ERROR_STATUS);
        }
        if(paymentStatus.equalsIgnoreCase(PaymentStatus.PAID.name())) {
            order.setPaymentStatus(PaymentStatus.PAID);
            updateProductSaleAt(order);
        }
        if(paymentStatus.equalsIgnoreCase(PaymentStatus.UNPAID.name())) {
            order.setPaymentStatus(PaymentStatus.UNPAID);
            resetProductSaleAt(order);
        }
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return orderMapper.orderToOrderResponse(orderRepository.save(order));
    }

    @Override
    public boolean cancelOrder (Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrException.ORDER_NOT_EXISTED));
        if(order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new AppException(ErrException.ORDER_ERROR_STATUS);
        }
        List<Order_Item> orderItems = order.getOrderItems();
        for (Order_Item orderItem : orderItems) {
            int quantity  = orderItem.getProductVariant().getProduct().getSoldQuantity();
            orderItem.getProductVariant().getProduct().setSoldQuantity(quantity - orderItem.getQuantity());
            productRepository.save(orderItem.getProductVariant().getProduct());

            int stock = orderItem.getProductVariant().getStock();
            orderItem.getProductVariant().setStock(stock + orderItem.getQuantity());
            productVariantRepository.save(orderItem.getProductVariant());
        }
        order.setStatus(OrderStatus.CANCELED);
        resetProductSaleAt(order);
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        orderRepository.save(order);
        return true;
    }

    @Override
    public ResultPaginationDTO getOrderByUserNameAndStatusOrder(String username, String status, Pageable pageable)
    {
        OrderSpecification spec =  new OrderSpecification(username, status, null);
        Page<Order> orders = orderRepository.findAll(spec, pageable);
        ResultPaginationDTO re  = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(orders.getTotalElements());
        mt.setPages(orders.getTotalPages());
        re.setMeta(mt);
        re.setResult(orderMapper.listOrderToOrderResponse(orders.getContent()));
        return re;
    }

    @Override
    public ResultPaginationDTO getOrderByStatusOrder( String status, Pageable pageable)
    {
        OrderSpecification spec =  new OrderSpecification(null, status, null);
        Page<Order> orders = orderRepository.findAll(spec, pageable);
        ResultPaginationDTO re  = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(orders.getTotalElements());
        mt.setPages(orders.getTotalPages());
        re.setMeta(mt);
        re.setResult(orderMapper.listOrderToOrderResponse(orders.getContent()));
        return re;
    }

    @Override
    public ResultPaginationDTO  getOrderByUserNameAndPaymentStatus(String username, String paymentStatus, Pageable pageable)
    {
        OrderSpecification spe =  new OrderSpecification(username, null, paymentStatus);
        Page<Order> orders = orderRepository.findAll(spe, pageable);
        ResultPaginationDTO re  = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(orders.getTotalElements());
        mt.setPages(orders.getTotalPages());
        re.setMeta(mt);
        re.setResult(orderMapper.listOrderToOrderResponse(orders.getContent()));
        return re;
    }

    @Override
    public ResultPaginationDTO getOrderByPaymentStatus(String payMentStatus, Pageable pageable)
    {
        OrderSpecification spe =  new OrderSpecification(null, null, payMentStatus);
        Page<Order> orders = orderRepository.findAll(spe, pageable);
        ResultPaginationDTO re  = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(orders.getTotalElements());
        mt.setPages(orders.getTotalPages());
        re.setMeta(mt);
        re.setResult(orderMapper.listOrderToOrderResponse(orders.getContent()));
        return re;
    }

    @Override
    @Transactional
    public void handleAfterCreateOrder(int orderId) {
        Order order = this.orderRepository.findByOrderId(orderId);
        Cart cartDelete = this.cartRepository.findByUserId(order.getUserId());
        this.cartItemRepository.deleteByCart(cartDelete);
        this.cartRepository.deleteByUserId(order.getUserId());
    }

    @Override
    public ResultPaginationDTO getAllTempOrder(Specification<TempOrder> spec, Pageable pageable) {
        Page<TempOrder> tempOrderPage = this.tempOrderRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(tempOrderPage.getTotalPages());
        mt.setTotal(tempOrderPage.getTotalElements());

        rs.setMeta(mt);
        List<TempOrder> tempOrderList = tempOrderPage.getContent();
        rs.setResult(tempOrderList);
        return rs;
    }

    @Override
    public List<TempOrder> getAllTemp() {
      return   this.tempOrderRepository.findAll();
    }

    @Override
    public Order findOrderByOrderId(Integer orderId) {
        return orderRepository.findByOrderId(orderId);
    }
}
