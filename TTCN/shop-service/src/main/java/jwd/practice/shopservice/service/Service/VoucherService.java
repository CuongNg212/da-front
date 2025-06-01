package jwd.practice.shopservice.service.Service;


import jwd.practice.shopservice.dto.request.VoucherArchive;
import jwd.practice.shopservice.dto.request.VoucherResquest;
import jwd.practice.shopservice.dto.response.ResultPaginationDTO;
import jwd.practice.shopservice.dto.response.UserResponse;
import jwd.practice.shopservice.dto.response.VoucherResponse;
import jwd.practice.shopservice.entity.voucher.DiscountType;
import jwd.practice.shopservice.entity.voucher.Voucher;
import jwd.practice.shopservice.entity.voucher.Voucher_User;
import jwd.practice.shopservice.exception.AppException;
import jwd.practice.shopservice.exception.ErrException;
import jwd.practice.shopservice.mapper.IVoucherMapper;
import jwd.practice.shopservice.mapper.httpClient.AuthClient;
import jwd.practice.shopservice.repository.Voucher_Repository;
import jwd.practice.shopservice.repository.Voucher_User_Repository;
import jwd.practice.shopservice.service.IService.IVoucherService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherService implements IVoucherService {

    Voucher_Repository voucherRepository;
    IVoucherMapper mapper;
    Voucher_User_Repository voucherUserRepository;
    AuthClient authClient;

    @Override
    public ResultPaginationDTO getAllVouchersForAdmin(Specification<Voucher> specification, Pageable pageable) {
        Page<Voucher> vouchers = voucherRepository.findAll(specification, pageable);
        ResultPaginationDTO paginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setTotal(vouchers.getTotalElements());
        meta.setPages(vouchers.getTotalPages());

        paginationDTO.setMeta(meta);
        paginationDTO.setResult(vouchers.getContent());
        return paginationDTO;
    }

    @Override
    public VoucherResponse getVouchersForUser(String code) {
        Voucher voucher = voucherRepository.findVoucherByCode(code);
        return mapper.voucherToVoucherResponse(voucher);
    }

    @Override
    public Voucher createVoucher(VoucherResquest voucherResquest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("authentication : "+authentication.getName());
        // Kiểm tra xem mã voucher có tồn tại hay không
        if (voucherRepository.findVoucherByCode(voucherResquest.getCode()) != null) {
            throw new AppException(ErrException.VOUCHER_EXISTED);
        }
        if (voucherResquest.getDiscountType().isEmpty()) throw new AppException(ErrException.VALIDATION_ERROR);

        // Nếu không có createdAt trong request thì tự động gán thời gian hiện tại
        if (voucherResquest.getCreatedAt() == null) {
            voucherResquest.setCreatedAt(Timestamp.from(Instant.now()));
        } else {
            // Nếu có createdAt, đảm bảo chuyển đổi đúng định dạng từ chuỗi sang Timestamp (nếu cần)
            try {
                voucherResquest.setCreatedAt(Timestamp.valueOf(voucherResquest.getCreatedAt().toLocalDateTime()));
            } catch (Exception e) {
                throw new AppException(ErrException.INVALID_DATE_FORMAT);
            }
        }

        // Ánh xạ từ VoucherResquest sang entity Voucher
        Voucher voucher = mapper.voucherRequestToVoucher(voucherResquest);
        if (voucherResquest.getDiscountType().equalsIgnoreCase("FIXED")) voucher.setDiscountType(DiscountType.FIXED);
        else if (voucherResquest.getDiscountType().equalsIgnoreCase("PERCENTAGE"))
            voucher.setDiscountType(DiscountType.PERCENTAGE);
        else throw new AppException(ErrException.VALIDATION_ERROR);
        // Lưu voucher vào cơ sở dữ liệu
        return voucherRepository.save(voucher);
    }

    @Override
    public Voucher updateVoucher(VoucherResquest voucherResquest) {
        Voucher voucher = voucherRepository.findVoucherByCode(voucherResquest.getCode());
        if (voucher == null) throw new AppException(ErrException.VOUCHER_NOT_EXISTED);
        mapper.updateVoucher(voucher, voucherResquest);
        return voucherRepository.save(voucher);
    }

    @Override
    public boolean deleteVoucher(String code) {
        Voucher voucher = voucherRepository.findVoucherByCode(code);
        if (voucher == null) throw new AppException(ErrException.VOUCHER_NOT_EXISTED);
        voucherRepository.delete(voucher);
        return true;
    }

    @Override
    public boolean checkVoucher(String code, BigDecimal priceBefore) {
        Voucher voucher = voucherRepository.findVoucherByCode(code);
        if (voucher == null) {
            throw new AppException(ErrException.VOUCHER_NOT_EXISTED);
        }
        // Kiểm tra điều kiện tối thiểu
        if (priceBefore.compareTo(voucher.getMinOrderValue()) < 0)
            return false;
        Date now = new Date(System.currentTimeMillis());

        if (now.before(voucher.getStartDate()) || now.after(voucher.getEndDate()))
            return false; // Chưa được áp dụng or hết hạn áp dụng

        if (voucher.getUsageLimit() != null && voucher.getUsageLimit() <= 0) return false;
        return true;
    }

    @Override
    public BigDecimal applyVoucher(Voucher voucher, BigDecimal priceBefore) {
        // Kiem tra điều kiện tối đa
        BigDecimal sumPriceDiscount;
        if (voucher.getDiscountType() == DiscountType.PERCENTAGE)
            sumPriceDiscount = priceBefore.multiply(voucher.getDiscountValue().divide(BigDecimal.valueOf(100)));

        else sumPriceDiscount = voucher.getDiscountValue().multiply(BigDecimal.valueOf(1000));

        if (sumPriceDiscount.compareTo(voucher.getMaxDiscount()) <= 0) return sumPriceDiscount;
        return voucher.getMaxDiscount();
    }

    // Nhan voucher cua user
    @Override
    public VoucherArchive archiveVoucherByUser(VoucherArchive voucherArchive) {
        if (voucherArchive == null) {
            throw new AppException(ErrException.VOUCHER_ERROR_ARCHIVE);
        }
        UserResponse userResponse = authClient.getUserById(voucherArchive.getUserId()).getResult();
        System.out.println("userResponse : "+userResponse);

        // Kiểm tra voucher có tồn tại hay không
        Voucher voucher = voucherRepository.findVoucherByVoucherId(voucherArchive.getVoucherId());
        if (voucher == null) {
            throw new AppException(ErrException.VOUCHER_NOT_EXISTED);
        }

        // Kiểm tra xem voucher đã được user lưu trước đó chưa
        Voucher_User existingVoucherUser = voucherUserRepository.findVoucherByUserAndVoucher(
                voucherArchive.getUserId(),
                voucherArchive.getVoucherId()
        );
        if (existingVoucherUser != null) {
            throw new AppException(ErrException.VOUCHER_ARCHIVE_EXISTED);
        }

        // Gọi profile-service để kiểm tra user có tồn tại không
//        try {
//            UserResponse userResponse = authClient.getUserById(voucherArchive.getUserId());
//            System.out.println("user" + userResponse);
//        } catch (Exception e) {
//            throw new AppException(ErrException.USER_NOT_EXISTED);
//        }

        // Lưu voucher cho user
        Voucher_User voucherUser = new Voucher_User();
        voucherUser.setUserId(userResponse.getUserId()); // Thay vì user entity, chỉ cần userId
        voucherUser.setVoucher(voucher);
        voucherUserRepository.save(voucherUser);

        return voucherArchive;
    }


    @Override
    public List<VoucherResponse> getVoucherOfUser(int userId) {
        List<Voucher_User> voucherUsers = voucherUserRepository.findVoucherByUserId(userId);
        if(voucherUsers.isEmpty()) return null;
        List<VoucherResponse> voucherResponses = new ArrayList<>();
        for (Voucher_User voucherUser : voucherUsers) {
            voucherResponses.add(mapper.voucherToVoucherResponse(voucherUser.getVoucher()));
        }
        return voucherResponses;
    }

    @Override
    public List<Voucher> getVoucherOk() {
        List<Voucher> vouchers = voucherRepository.findAll();
        List<Voucher> voucherResponses = new ArrayList<>();
        for (Voucher voucher : vouchers) {
            System.out.println("Voucher ID: " + voucher.getVoucherId());
            System.out.println("Start Date: " + voucher.getStartDate());
            System.out.println("End Date: " + voucher.getEndDate());
            System.out.println("Usage Limit: " + voucher.getUsageLimit());

            Date now = new Date(System.currentTimeMillis());
            if (now.after(voucher.getStartDate()) && now.before(voucher.getEndDate()))
            {
                if (voucher.getUsageLimit() > 0)
                {
                    voucherResponses.add(voucher);
                }
            }
        }
        return voucherResponses;
    }

    @Override
    public BigDecimal getSumDiscount(String code, BigDecimal price) {
        Voucher voucher = this.voucherRepository.findVoucherByCode(code);
        if(voucher == null)
            return new BigDecimal(0);
        if(checkVoucher(code, price))
        {
            return this.applyVoucher(voucher,price);
        }
        return new  BigDecimal(0);
    }

    @Override
    public List<Integer> getVoucherUserByVoucherId(Integer voucherId) {
        return voucherUserRepository.findVoucherUserByVoucherId(voucherId);
    }

    @Override
    public Boolean deleteVoucherUser(String code) {
        Voucher voucher = this.voucherRepository.findVoucherByCode(code);
        List<Integer> list = getVoucherUserByVoucherId(voucher.getVoucherId());
        if(!list.isEmpty())
        {
            voucherUserRepository.deleteListVoucherUser(list);
        }
        return true;
    }


}
