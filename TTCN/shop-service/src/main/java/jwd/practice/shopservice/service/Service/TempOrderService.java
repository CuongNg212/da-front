package jwd.practice.shopservice.service.Service;

import jwd.practice.shopservice.entity.TempOrder;
import jwd.practice.shopservice.repository.Temp_Order_Repository;
import jwd.practice.shopservice.service.IService.ITempOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class TempOrderService implements ITempOrderService {
    Temp_Order_Repository tempOrderRepository;
    @Override
    public void save(TempOrder tempOrder) {
        this.tempOrderRepository.save(tempOrder);
    }

    @Override
    public TempOrder findByTxnRef(String vnpTxnRef) {
        return this.tempOrderRepository.findByTxnRef(vnpTxnRef);
    }
}
