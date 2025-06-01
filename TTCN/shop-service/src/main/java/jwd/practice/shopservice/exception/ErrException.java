package jwd.practice.shopservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrException {
    USER_EXISTED(101,"User existed"),
    PRODUCT_NOT_EXISTED(404, "Product not existed"),
    VOUCHER_EXISTED(101,"Voucher existed"),
    VOUCHER_NOT_EXISTED(101,"Voucher not existed"),
    VOUCHER_ERROR_ARCHIVE(812,"Voucher not archived"),
    VOUCHER_OUT_OF_DATE(812,"Voucher out of date"),
    VOUCHER_ARCHIVE_EMPTY(822,"User doesn't have voucher"),
    VOUCHER_ARCHIVE_EXISTED(822,"Voucher already archived"),
    NOT_ENOUGH_STOCK(110, "Not enough stock"),
    VALIDATION_ERROR(102,"Enter the correct requirements!"),
    ORDER_NOT_EXISTED(101,"Order not existed"),
    ORDER_EXISTED(101,"Order existed"),
    ORDER_ERROR_STATUS(100, "Not change order with status DELIVERED or CANCELED"),
    ORDER_ERROR_FIND_PRODUCT(100, "Not find product"),
    NOT_UPDATE_ORDER(404, "Đơn hàng đã thanh toán hoặc đang vận chuyển!"),
    USER_NOT_EXISTED(102,"User not existed"),
    INVALID_KEY(105,"Invalid key"),
    EMAIL_NOT_EXISTED(106,"Email not existed"),
    EMAIL_EXISTED(107,"Email existed"),
    NOT_FILE(108,"Not file"),
    DIRECTORY_CREATION_FAILED(109,"DIRECTORY_CREATION_FAILED"),
    FILE_STORAGE_FAILED(110, "File storage failed"),
    INVALID_DATE_FORMAT(112, "Invalid createdAt format"),
    ORDER_STATUS_INVALID(100, "status không hợp lệ"),
    UNAUTHENTICATED(1000, "Unauthenticated" ),;
    private int code;
    private String message;
}
