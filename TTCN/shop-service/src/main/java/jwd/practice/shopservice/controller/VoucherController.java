package jwd.practice.shopservice.controller;


import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import jwd.practice.shopservice.dto.request.VoucherArchive;
import jwd.practice.shopservice.dto.request.VoucherResquest;
import jwd.practice.shopservice.dto.response.ResultPaginationDTO;
import jwd.practice.shopservice.dto.response.VoucherResponse;
import jwd.practice.shopservice.entity.voucher.Voucher;
import jwd.practice.shopservice.service.IService.IVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/voucher")
public class VoucherController {

    @Autowired
    private IVoucherService voucherService;


    // Xem chi tiết voucher -  getVoucherDetail(String code)
    @GetMapping("/{code}")
    public ResponseEntity<VoucherResponse> getVouchersForUser(@PathVariable String code) {
        return ResponseEntity.ok(voucherService.getVouchersForUser(code));
    }

    @GetMapping("/admin/list")
    public ResponseEntity<ResultPaginationDTO> getAllVouchersForAdmin(@Filter Specification<Voucher> specification, Pageable pageable) {
        return ResponseEntity.ok(voucherService.getAllVouchersForAdmin(specification, pageable));
    }

    // Tạo voucher mới - VoucherResponse createVoucher(VoucherRequest voucherRequest)
    @PostMapping("/admin/create")
    public ResponseEntity<Voucher> createVoucher(@Valid @RequestBody VoucherResquest voucher) {
        Voucher voucher1 = voucherService.createVoucher(voucher);
        return ResponseEntity.ok(voucher1);
    }

    // Cập nhật voucher - void updateVoucher(VoucherUpdateRequest voucherUpdateRequest)
    @PutMapping("/admin/update")
    public  ResponseEntity<Voucher> updateVoucher( @Valid @RequestBody VoucherResquest voucher){
        Voucher voucher1 = voucherService.updateVoucher(voucher);
        return ResponseEntity.ok(voucher1);
    }

    //  Xóa voucher - void deleteVoucher(Long voucherId)
    @DeleteMapping("/admin/delete/{code}")
    public ResponseEntity<String> deleteVoucher(@PathVariable String code) {
        return ResponseEntity.ok(voucherService.deleteVoucher(code)?"Deleted voucher!" : "Deleted voucher failed");
    }

    @PostMapping("/archiveVoucher")
    public ResponseEntity<VoucherArchive> archiveVoucher(@RequestBody VoucherArchive voucher) {
        return ResponseEntity.ok(voucherService.archiveVoucherByUser(voucher));
    }

    @GetMapping("/listVoucherArchive/{userId}")
    public ResponseEntity<List<VoucherResponse>> getVoucherArchives(@PathVariable int userId) {
        return ResponseEntity.ok(voucherService.getVoucherOfUser(userId));
    }

    @GetMapping("/voucherOk")
    public ResponseEntity<List<Voucher>> getVoucherOk() {
        return ResponseEntity.ok(voucherService.getVoucherOk());
    }
    @GetMapping("/getSumDiscount")
    public ResponseEntity<BigDecimal> getSumDiscount(@RequestParam(name = "code" )String code, @RequestParam(name = "price") BigDecimal price )
    {
        return ResponseEntity.ok(this.voucherService.getSumDiscount(code,price));
    }

    @DeleteMapping("/voucheruser/{code}")
    public ResponseEntity<Boolean> getVoucherUser(@PathVariable String code) {
        return ResponseEntity.ok(voucherService.deleteVoucherUser(code));
    }
}
