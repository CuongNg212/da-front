package jwd.practice.shopservice.service.IService;


import jwd.practice.shopservice.dto.request.ProductCreateRequest;
import jwd.practice.shopservice.dto.response.ProductImagesResponse;
import jwd.practice.shopservice.dto.response.ProductResponse;
import jwd.practice.shopservice.dto.response.ResultPaginationDTO;
import jwd.practice.shopservice.entity.Product;
import jwd.practice.shopservice.entity.ProductVariant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IProductService {
    ProductResponse createProduct(ProductCreateRequest productCreateRequest);

    ProductResponse getOneProduct(int id);

    ResultPaginationDTO getAllProduct(Specification<Product> spec, Pageable pageable);

    ProductResponse updateProduct(int id, ProductCreateRequest productCreateRequest);

    boolean exisById(int id);

    void deleteProduct(int id);

    void uploadImages(int id, MultipartFile[]  files) throws IOException;

    List<ProductImagesResponse> getAllImages(int id);

    ProductImagesResponse getImageById(int id);

    void deleteImages(int id);

    List<ProductVariant> getAllProductVariants(int id);

    List<ProductResponse> getTopSeller();

    Boolean imagesExisById(int id);

    int getNumberProductOfCategory(int id);

    Double getMaxPrice() ;

    Double getMinPrice();

    int getNumberOfProductBySizeAndColor(int productId, String color, int size);

    List<Map<String, Object>> getProductNameAndStock();

    List<Map<String, Object>> getProductNameAndStockAndCategoryName();

    List<Map<String, Object>> getMonthlyStatisticsForCurrentYear();

    List<Product> getTopSaleProducts(String filter);

    List<Product> getLeastSaleProducts(String filter);
}
