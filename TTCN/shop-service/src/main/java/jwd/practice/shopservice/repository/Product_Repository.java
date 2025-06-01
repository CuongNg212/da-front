package jwd.practice.shopservice.repository;

import jwd.practice.shopservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Product_Repository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {


    List<Product> findTop4ByOrderBySoldQuantityDesc();

    @Query("select count (p) from Product p where p.category.categoryId = :id")
    int getNumberProductOfCategory(int id);

    @Query("SELECT MAX(p.price) FROM Product p")
    Double findMaxPrice();

    @Query("SELECT MIN(p.price) FROM Product p")
    Double findMinPrice();

    @Query(value = "SELECT c.category_name, SUM(pv.stock) AS total_stock " +
            "FROM shop.product_variant pv " +
            "INNER JOIN shop.product p ON pv.product_id = p.product_id " +
            "INNER JOIN shop.category c ON p.category_id = c.category_id " +
            "GROUP BY c.category_name",
            nativeQuery = true)
    List<Object[]> getProductNameAndStock();

    @Query(value = "SELECT c.category_name, p.product_name, SUM(pv.stock) AS total_stock " +
            "FROM shop.product_variant pv " +
            "INNER JOIN shop.product p ON pv.product_id = p.product_id " +
            "INNER JOIN shop.category c ON p.category_id = c.category_id " +
            "GROUP BY p.product_name, c.category_name",
            nativeQuery = true)
    List<Object[]> getProductNameAndStockAndCategoryName();

    @Query(value = "SELECT MONTH(p.created_at) AS month, " +
            "SUM(v.stock) AS totalStock, " +
            "SUM(p.sold_quantity) AS totalSold " +
            "FROM shop.product p " +
            "INNER JOIN shop.product_variant v ON p.product_id = v.product_id " +
            "WHERE YEAR(p.created_at) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(p.created_at) " +
            "ORDER BY MONTH(p.created_at)", nativeQuery = true)
    List<Object[]> findMonthlyStatisticsForCurrentYear();




    @Query(value = "SELECT * FROM shop.product WHERE DATE(sale_at) = CURDATE() ORDER BY sold_quantity DESC LIMIT 3", nativeQuery = true)
    List<Product> findTopSalesByDay();

    @Query(value = "SELECT * FROM shop.product WHERE WEEK(sale_at) = WEEK(CURDATE()) AND YEAR(sale_at) = YEAR(CURDATE()) ORDER BY sold_quantity DESC LIMIT 3", nativeQuery = true)
    List<Product> findTopSalesByWeek();

    @Query(value = "SELECT * FROM shop.product WHERE MONTH(sale_at) = MONTH(CURDATE()) AND YEAR(sale_at) = YEAR(CURDATE()) ORDER BY sold_quantity DESC LIMIT 3", nativeQuery = true)
    List<Product> findTopSalesByMonth();

    @Query(value = "SELECT * FROM shop.product WHERE YEAR(sale_at) = YEAR(CURDATE()) ORDER BY sold_quantity DESC LIMIT 3", nativeQuery = true)
    List<Product> findTopSalesByYear();

    @Query(value = "SELECT * FROM shop.product WHERE DATE(sale_at) = CURDATE() ORDER BY sold_quantity ASC LIMIT 3", nativeQuery = true)
    List<Product> findLeastSalesByDay();

    @Query(value = "SELECT * FROM shop.product WHERE WEEK(sale_at) = WEEK(CURDATE()) AND YEAR(sale_at) = YEAR(CURDATE()) ORDER BY sold_quantity ASC LIMIT 3", nativeQuery = true)
    List<Product> findLeastSalesByWeek();

    @Query(value = "SELECT * FROM shop.product WHERE MONTH(sale_at) = MONTH(CURDATE()) AND YEAR(sale_at) = YEAR(CURDATE()) ORDER BY sold_quantity ASC LIMIT 3", nativeQuery = true)
    List<Product> findLeastSalesByMonth();

    @Query(value = "SELECT * FROM shop.product WHERE YEAR(sale_at) = YEAR(CURDATE()) ORDER BY sold_quantity ASC LIMIT 3", nativeQuery = true)
    List<Product> findLeastSalesByYear();

}
