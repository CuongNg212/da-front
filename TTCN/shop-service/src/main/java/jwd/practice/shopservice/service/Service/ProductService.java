package jwd.practice.shopservice.service.Service;


import jwd.practice.shopservice.dto.ProductVatriantDTO;
import jwd.practice.shopservice.dto.request.ProductCreateRequest;
import jwd.practice.shopservice.dto.response.ProductImagesResponse;
import jwd.practice.shopservice.dto.response.ProductResponse;
import jwd.practice.shopservice.dto.response.ResultPaginationDTO;
import jwd.practice.shopservice.entity.Category;
import jwd.practice.shopservice.entity.Product;
import jwd.practice.shopservice.entity.ProductVariant;
import jwd.practice.shopservice.entity.Product_Image;
import jwd.practice.shopservice.mapper.IProductImageMapper;
import jwd.practice.shopservice.mapper.IProductMapper;
import jwd.practice.shopservice.mapper.IProductVariantMapper;
import jwd.practice.shopservice.repository.*;
import jwd.practice.shopservice.service.IService.IProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService implements IProductService {

    private static final String UPLOAD_DIR = "upload/product";
    Product_Repository productRepository;
    Product_Image_Repository productImageRepository;
    Category_Repository categoryRepository;
    IProductMapper productMapper;
    IProductImageMapper productImageMapper;
    ProductVariant_Repository productVariantRepository;
    IProductVariantMapper productVariantMapper;
    Cart_Item_Repository cartItemRepository;
    @Override
    public ProductResponse createProduct(ProductCreateRequest productCreateRequest) {

        Product createProduct = this.productMapper.toProduct(productCreateRequest);
        Category category = this.categoryRepository.findByCategoryName(productCreateRequest.getCategory());
        createProduct.setCategory(category);

        Product product =  this.productRepository.save(createProduct);
        product.setSaleAt(LocalDateTime.now());
        List<ProductVatriantDTO> productVatriantDTOList = productCreateRequest.getVariants();
        productVatriantDTOList.stream().forEach(item ->{
            ProductVariant productVariant = new ProductVariant();
            productVariant.setSize(item.getSize());
            productVariant.setColor(item.getColor());
            productVariant.setStock(item.getStock());
            productVariant.setProduct(product);
            this.productVariantRepository.save(productVariant);
        });

        return this.productMapper.toProductResponse(createProduct);
    }

    @Override
    public ProductResponse getOneProduct(int id) {

        Product product = this.productRepository.findById(id).get();
        return productMapper.toProductResponse(product);
    }

    @Override
    public ResultPaginationDTO getAllProduct(Specification<Product> spec, Pageable pageable) {
        Page<Product> productPage = this.productRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(productPage.getTotalPages());

        mt.setTotal(productPage.getTotalElements());

        rs.setMeta(mt);
        List<Product> productList = productPage.getContent();
        List<ProductResponse> productResponseList = this.productMapper.toProductResponseList(productPage.getContent());
        rs.setResult(productResponseList);

        return rs;

    }


    @Override
    @Transactional
    public ProductResponse updateProduct(int id, ProductCreateRequest productCreateRequest) {
        Product product = this.productRepository.findById(id).get();
        Product updateProduct = this.productMapper.toProduct(productCreateRequest);
        product.setProductName(productCreateRequest.getProductName());
        Category updateCategory = this.categoryRepository.findByCategoryName(productCreateRequest.getCategory());
        product.setCategory(updateCategory);
        product.setDescription(productCreateRequest.getDescription());
        product.setDiscount(productCreateRequest.getDiscount());
        product.setMaterial(productCreateRequest.getMaterial());
        product.setPrice(productCreateRequest.getPrice());
        this.productRepository.save(product);

        // Xóa các cart_item liên quan
        this.productVariantRepository.findProductVariantsByProduct(product).forEach(item->{
            this.cartItemRepository.deleteByProductVariant(item);
        });
        this.productVariantRepository.deleteProductVariantsByProduct(product);
        List<ProductVatriantDTO> productVatriantDTOList = productCreateRequest.getVariants();
        productVatriantDTOList.stream().forEach(item ->{
            ProductVariant productVariant = new ProductVariant();
            productVariant.setSize(item.getSize());
            productVariant.setColor(item.getColor());
            productVariant.setStock(item.getStock());
            productVariant.setProduct(product);
            this.productVariantRepository.save(productVariant);
        });
        return this.productMapper.toProductResponse(product);

    }

    @Override
    public boolean exisById(int id) {
        return this.productRepository.existsById(id);
    }



    @Override
    @Transactional
    public void deleteProduct(int id)  {
//        List<ProductVariant> productVariants = this.productVariantRepository.findProductVariantsByProduct(this.productRepository.findById(id).get());
//        if (productVariants.size() != 0)
//            this.productVariantRepository.deleteProductVariantsByProduct(this.productRepository.findById(id).get());

        this.productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void uploadImages(int id, MultipartFile[] files) throws IOException {
        Product product = this.productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        this.productImageRepository.deleteProduct_ImagesByProduct(product);

        // Kiểm tra và tạo thư mục lưu trữ nếu chưa có
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        for (MultipartFile file : files) {
            // Lưu từng file ảnh
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // Lưu thông tin ảnh vào database
            Product_Image image = new Product_Image();
            image.setImageUrl(fileName);
            image.setProduct(product);
            this.productImageRepository.save(image);
        }
    }


    @Override
    public List<ProductImagesResponse> getAllImages(int id) {
        Product product = this.productRepository.findById(id).get();
        List<Product_Image> list = this.productImageRepository.findAllByProduct(product);
        return this.productImageMapper.toProductImagesResponseList(list);
    }

    @Override
    public ProductImagesResponse getImageById(int id){
        Product product = this.productRepository.findById(id).get();
        Product_Image image = this.productImageRepository.findById(id).get();
        return this.productImageMapper.toProductImagesResponse(image);
    }

    @Override
    @Transactional
    public void deleteImages(int id) {
        this.productImageRepository.deleteById(id);
    }

    @Override
    public List<ProductVariant> getAllProductVariants(int id) {

        return this.productVariantRepository.findProductVariantsByProduct(this.productRepository.findById(id).get());
    }

    @Override
    public List<ProductResponse> getTopSeller() {
        return this.productMapper.toProductResponseList(this.productRepository.findTop4ByOrderBySoldQuantityDesc());
    }

    @Override
    public Boolean imagesExisById(int id) {
        return this.productImageRepository.existsById(id);
    }

    @Override
    public int getNumberProductOfCategory(int id) {
        return this.productRepository.getNumberProductOfCategory(id);
    }

    @Override
    public Double getMaxPrice() {
        return this.productRepository.findMaxPrice();
    }

    @Override
    public Double getMinPrice() {
        return this.productRepository.findMinPrice();
    }

    // dem so luong hang
    @Override
    public int getNumberOfProductBySizeAndColor(int productId, String color, int size ) {
        try {
            return this.productVariantRepository.getSumOfProduct(productId, color, size);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getNumberOfProductVariant(int productVariantId ) {
        try {
            return this.productVariantRepository.getSumOfProductVariant(productVariantId);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public List<Map<String, Object>> getProductNameAndStock() {
        List<Object[]> results = productRepository.getProductNameAndStock();

        //Map<String, Object> productData = new HashMap<>();

//        if (results != null && !results.isEmpty()) {
//            Object[] row = results.get(0); // Unwrap the first result
//            productData.put("product_name", row[0]);
//            productData.put("total_stock", ((Number) row[1]).intValue());
//        } else {
//            productData.put("error", "No data found");
//        }
//        return productData;

        List<Map<String, Object>> productList = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("category_name", row[0]); // Product Name
            productData.put("total_stock", ((Number) row[1]).intValue()); // Total Stock
            productList.add(productData);
        }
        return productList;
    }


    @Override
    public List<Map<String, Object>> getProductNameAndStockAndCategoryName() {
        List<Object[]> results = productRepository.getProductNameAndStockAndCategoryName();

        List<Map<String, Object>> productList = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("category_name", row[0]); // Category Name
            productData.put("product_name", row[1]);
            productData.put("total_stock", ((Number) row[2]).intValue()); // Total Stock
            productList.add(productData);
        }
        return productList;
    }

    @Override
    public List<Map<String, Object>> getMonthlyStatisticsForCurrentYear() {
        // Gọi query để lấy dữ liệu từ DB
        List<Object[]> results = productRepository.findMonthlyStatisticsForCurrentYear();

        // Chuyển đổi kết quả từ query thành Map
        Map<Integer, Map<String, Object>> monthlyData = new HashMap<>();
        for (Object[] row : results) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("month", row[0]);
            productData.put("totalStock", ((Number) row[1]).intValue());
            productData.put("totalSold", ((Number) row[2]).intValue());
            monthlyData.put((Integer) row[0], productData);
        }

        // Tạo danh sách đầy đủ 12 tháng
        List<Map<String, Object>> monthlyStatistics = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            if (monthlyData.containsKey(i)) {
                monthlyStatistics.add(monthlyData.get(i));
            } else {
                Map<String, Object> emptyData = new HashMap<>();
                emptyData.put("month", i);
                emptyData.put("totalStock", 0);
                emptyData.put("totalSold", 0);
                monthlyStatistics.add(emptyData);
            }
        }

        return monthlyStatistics;
    }

    @Override
    public List<Product> getTopSaleProducts(String filter) {
        switch (filter) {
            case "day":
                return productRepository.findTopSalesByDay();
            case "week":
                return productRepository.findTopSalesByWeek();
            case "month":
                return productRepository.findTopSalesByMonth();
            case "year":
                return productRepository.findTopSalesByYear();
            default:
                throw new IllegalArgumentException("Invalid filter");
        }
    }

    @Override
    public List<Product> getLeastSaleProducts(String filter) {
        switch (filter) {
            case "day":
                return productRepository.findLeastSalesByDay();
            case "week":
                return productRepository.findLeastSalesByWeek();
            case "month":
                return productRepository.findLeastSalesByMonth();
            case "year":
                return productRepository.findLeastSalesByYear();
            default:
                throw new IllegalArgumentException("Invalid filter");
        }
    }


}
