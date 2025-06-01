package jwd.practice.shopservice.service.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class VnpayService {
    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.url}")
    private String vnp_Url;

    @Value("${vnpay.returnUrl}")
    private String return_Url;

    @Value("${vnpay.ipAddress}")
    private String vnpIpAddr;
    public String code;

    public String getHashSecret() {
        return vnp_HashSecret;
    }



    public String createPaymentURL(String orderInfo, long amount ) throws UnsupportedEncodingException {
        String vnpVersion = "2.1.0";
        String vnpCommand = "pay";
        String vnpCurrCode = "VND";
        String vnpLocale = "vn"; // Hoặc "en"
        String vnpTxnRef = String.valueOf(System.currentTimeMillis()); // Mã tham chiếu giao dịch
        code = vnpTxnRef;
        String vnpOrderType = "billpayment"; // Ví dụ mã danh mục hàng hóa
        String vnpCreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String vnpExpireDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis() + 10 * 60 * 1000)); // Hết hạn trong 30 phút

        // Tạo Map tham số
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnpVersion);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", vnp_TmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amount * 100)); // Chuyển đổi VND
        vnpParams.put("vnp_CurrCode", vnpCurrCode);
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", vnpOrderType);
        vnpParams.put("vnp_ReturnUrl", return_Url);
        vnpParams.put("vnp_CreateDate", vnpCreateDate);
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);
        vnpParams.put("vnp_IpAddr", vnpIpAddr);
        vnpParams.put("vnp_Locale", vnpLocale);
        vnpParams.put("vnp_TxnRef", vnpTxnRef);

        // Tạo chuỗi hash để kiểm tra
        StringBuilder hashData = new StringBuilder();
        vnpParams.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    try {
                        if (entry.getValue() != null) {
                            hashData.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                });

        // Loại bỏ ký tự '&' cuối cùng
        String queryString = hashData.substring(0, hashData.length() - 1);

        // Tính toán mã hash
        String secureHash = HMACSHA512(vnp_HashSecret, queryString);
        vnpParams.put("vnp_SecureHash", secureHash);

        // Tạo URL
        StringBuilder paymentUrl = new StringBuilder(vnp_Url + "?");
        vnpParams.entrySet().forEach(entry -> {
            try {
                paymentUrl.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

        // Loại bỏ ký tự '&' cuối cùng
        return paymentUrl.substring(0, paymentUrl.length() - 1);
    }

    public String HMACSHA512(String key, String data) {
        try {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            sha512_HMAC.init(secret_key);

            byte[] bytes = sha512_HMAC.doFinal(data.getBytes());
            StringBuilder hash = new StringBuilder();
            for (byte aByte : bytes) {
                hash.append(String.format("%02x", aByte));
            }
            return hash.toString();
        } catch (Exception e) {
            return "";
        }
    }
}

