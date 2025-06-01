package jwd.practice.shopservice.service.IService;


import jwd.practice.shopservice.entity.TempOrder;

public interface ITempOrderService {

    void save(TempOrder tempOrder);

    TempOrder findByTxnRef(String vnpTxnRef);
}
