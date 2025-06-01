package jwd.practice.shopservice.mapper;


import jwd.practice.shopservice.dto.request.VoucherResquest;
import jwd.practice.shopservice.dto.response.VoucherResponse;
import jwd.practice.shopservice.entity.voucher.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IVoucherMapper {

    VoucherResponse voucherToVoucherResponse(Voucher voucher);

    void updateVoucher(@MappingTarget Voucher voucher, VoucherResquest voucherResquest);

    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(target = "discountType", ignore = true)
    Voucher voucherRequestToVoucher(VoucherResquest voucherResquest);
}
