package jwd.practice.shopservice.repository;

import jwd.practice.shopservice.entity.voucher.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface Voucher_Repository extends JpaRepository<Voucher, Integer>, JpaSpecificationExecutor<Voucher> {
    Voucher findVoucherByCode(String code);

    Voucher findVoucherByVoucherId(Integer id);
}
