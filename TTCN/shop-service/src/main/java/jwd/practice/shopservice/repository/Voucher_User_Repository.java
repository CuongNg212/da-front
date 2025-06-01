package jwd.practice.shopservice.repository;

import jwd.practice.shopservice.entity.voucher.Voucher_User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface Voucher_User_Repository extends JpaRepository<Voucher_User, Integer> {

    @Query("select vu from Voucher_User vu where vu.userId = :userId")
    List<Voucher_User> findVoucherByUserId(@Param("userId") int userId);

    @Query("select vu from Voucher_User vu where vu.userId = :userId and vu.voucher.voucherId =:voucherId")
    Voucher_User findVoucherByUserAndVoucher(@Param("userId") int userId, @Param("voucherId") Integer voucherId);

    // List<Voucher_User> findByVoucher_VoucherId(int voucherId);

    @Query(nativeQuery = true, value = "select voucher_user_id from voucher_user where voucher_id =:voucherId")
    List<Integer> findVoucherUserByVoucherId(@Param("voucherId") int voucherId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM voucher_user WHERE voucher_user_id IN (:list)")
    void deleteListVoucherUser(@Param("list") List<Integer> list);

}
