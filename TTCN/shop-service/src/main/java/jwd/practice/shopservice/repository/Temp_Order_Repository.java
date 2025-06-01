package jwd.practice.shopservice.repository;

import jwd.practice.shopservice.entity.TempOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface Temp_Order_Repository extends JpaRepository<TempOrder,Integer>, JpaSpecificationExecutor<TempOrder> {
    TempOrder findByTxnRef(String txnref);
}
