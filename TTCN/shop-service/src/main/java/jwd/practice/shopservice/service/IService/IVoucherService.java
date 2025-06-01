package jwd.practice.shopservice.service.IService;


import jwd.practice.shopservice.dto.request.VoucherArchive;
import jwd.practice.shopservice.dto.request.VoucherResquest;
import jwd.practice.shopservice.dto.response.ResultPaginationDTO;
import jwd.practice.shopservice.dto.response.VoucherResponse;
import jwd.practice.shopservice.entity.voucher.Voucher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IVoucherService {

     ResultPaginationDTO getAllVouchersForAdmin(Specification<Voucher> specification, Pageable pageable);

     VoucherResponse getVouchersForUser(String code);

     Voucher createVoucher(VoucherResquest voucherResquest);

     Voucher updateVoucher(VoucherResquest voucherResquest);

     boolean deleteVoucher(String code);

     boolean checkVoucher(String code, BigDecimal priceBefore);

     BigDecimal applyVoucher(Voucher voucher, BigDecimal priceBefore);

     VoucherArchive archiveVoucherByUser(VoucherArchive voucherArchive);

     List<VoucherResponse> getVoucherOfUser(int userId);

     List<Voucher> getVoucherOk();

    BigDecimal getSumDiscount(String code, BigDecimal price);

    List<Integer> getVoucherUserByVoucherId(Integer voucherId);

    Boolean deleteVoucherUser(String code);
}
