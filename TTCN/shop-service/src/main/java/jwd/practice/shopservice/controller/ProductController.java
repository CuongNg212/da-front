package jwd.practice.shopservice.controller;


import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import jwd.practice.shopservice.config.ProductSpecification;
import jwd.practice.shopservice.dto.request.ProductCreateRequest;
import jwd.practice.shopservice.dto.response.ProductImagesResponse;
import jwd.practice.shopservice.dto.response.ProductResponse;
import jwd.practice.shopservice.dto.response.ResultPaginationDTO;
import jwd.practice.shopservice.entity.Product;
import jwd.practice.shopservice.entity.ProductVariant;
import jwd.practice.shopservice.exception.IdException;
import jwd.practice.shopservice.service.Service.ExportService;
import jwd.practice.shopservice.service.Service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class ProductController {

    ProductService productService;
    ExportService exportService;


    @PostMapping("/product") //
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductCreateRequest productCreateRequest)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productCreateRequest));
    }

    //lay ra một ẩn pham
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponse> getOneProduct(@PathVariable int id) throws IdException
    {
        if(!this.productService.exisById(id))
        {
            throw new IdException("id sản phẩm không tồn tại");
        }
        return ResponseEntity.ok(productService.getOneProduct(id));
    }

    //lay ra toan bo san pham
    @GetMapping("/product")
    public ResponseEntity<ResultPaginationDTO> getAllProduct(@Filter Specification<Product> spec , Pageable pageable)
    {
        return ResponseEntity.ok(productService.getAllProduct(spec, pageable));
    }
    // tim liem san pham
    @GetMapping("/product/name")
    public ResponseEntity<ResultPaginationDTO> getAllProduct(@RequestParam String search, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.searchByName(search);
        return ResponseEntity.ok(productService.getAllProduct(spec, pageable));
    }

    //lay ra top san pham ban chay
    @GetMapping("/product/topseller")
    public ResponseEntity<List< ProductResponse>> getTopSeller()
    {
        return ResponseEntity.ok(productService.getTopSeller());
    }

    //lay ra so luong san pham trong mot danh muc
    @GetMapping("/product/category/{id}")
    public ResponseEntity<Integer> getNumberProductOfCategory(@PathVariable int id)
    {
        return ResponseEntity.ok(this.productService.getNumberProductOfCategory(id)) ;
    }

    //cap nhat san pham va cac bien the cua no
    @PutMapping("product/{id}") // can sua tinh nang sua cac bien the cua san pham
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable int id, @RequestBody ProductCreateRequest productCreateRequest) throws IdException
    {
        if(!this.productService.exisById(id))
            throw new IdException("id sản phẩm không tồn tại");
        return ResponseEntity.ok(productService.updateProduct(id,productCreateRequest));
    }


    //xoa san pham
    @DeleteMapping("product/{id}") //luu y khi xoa san pham can xoa tat ca cac bien the cua no truoc moi có the xoa duoc

    public ResponseEntity<String> deleteProduct(@PathVariable int id) throws IdException
    {
        if(!this.productService.exisById(id))
            throw new IdException("không tồn tại id sản phẩm");
        this.productService.deleteProduct(id);
        return ResponseEntity.ok("xóa thành công sản phẩm có id: "+id);
    }

    //them anh cho san pham
    @PutMapping("/product/add_images/{id}")
    public ResponseEntity<String> addImages(@PathVariable int id, @RequestParam(name = "image")MultipartFile[] files) throws IdException, IOException {
        if(!this.productService.exisById(id))
            throw new IdException("id sản phẩm không tồn tại");
        this.productService.uploadImages(id,files);
        return ResponseEntity.ok("them thanh cong");
    }

    //lay ra toan bo anh cua san pham
    @GetMapping("/product/images/{id}")
    public ResponseEntity<List<ProductImagesResponse>> getAllImages(@PathVariable int id) throws IdException
    {
        if (!this.productService.exisById(id))
            throw new IdException("id sản phẩm không tồn tại");
        return ResponseEntity.ok(this.productService.getAllImages(id));
    }


    //phương thức xóa ảnh của sản phaam
    @DeleteMapping("/product/images/{id}")
    public ResponseEntity<String> deleteImages(@PathVariable int id) throws IdException
    {
        if (!this.productService.imagesExisById(id))
            throw new IdException("id ảnh không tồn tại");
        this.productService.deleteImages(id);


        return ResponseEntity.ok("xoa thành công");
    }

    @GetMapping("/product/price-range")
    public ResponseEntity<Map<String, Double>> getPriceRange() {
        Double maxPrice = productService.getMaxPrice();
        Double minPrice = productService.getMinPrice();

        Map<String, Double> response = new HashMap<>();
        response.put("maxPrice", maxPrice);
        response.put("minPrice", minPrice);

        return ResponseEntity.ok(response);
    }

// các phương thức liên quan đến biến thể của sản phẩm
    //phương thức lấy ra tất cả các biến thể của sản phẩm
    @GetMapping("/product/variants/{id}")
    public ResponseEntity<List<ProductVariant>> getAllProductvariants(@PathVariable int id) throws IdException
    {
        if(!this.productService.exisById(id))
        {
            throw new IdException("id sản phẩm không tồn tại");
        }
        return ResponseEntity.ok(this.productService.getAllProductVariants(id));
    }


    @GetMapping("/product/sum")
    public ResponseEntity<Integer> getSumOfProduct(@RequestParam(name = "productId") int productId, @RequestParam(name = "color") String color, @RequestParam(name = "size") int size) throws IdException{
        return ResponseEntity.ok(productService.getNumberOfProductBySizeAndColor(productId,color, size));
    }

    @GetMapping("product/sumVariant")
    public ResponseEntity<Integer> getSumOfProductVariant(@RequestParam(name = "variant_id") int variant_id) throws IdException{
        return ResponseEntity.ok(productService.getNumberOfProductVariant(variant_id));
    }

    @GetMapping("chart/product/sum")
    public List<Map<String, Object>> getSumOfProductAdmin(){
        return productService.getProductNameAndStock();
    }


    @GetMapping("/product/stock")
    public List<Map<String, Object>> getProductNameAndStockAndCategoryName(){
        return productService.getProductNameAndStockAndCategoryName();
    }

    @GetMapping("/product/chart")
    public ResponseEntity<Object> getChartProduct() {
        List<Map<String, Object>> result = productService.getMonthlyStatisticsForCurrentYear();
        return ResponseEntity.ok(result); // Trả về kết quả dưới dạng JSON
    }

    @PostMapping("product/download")
    public ResponseEntity<byte[]> exportToExel(@RequestParam(name = "id") int id)
    {
        byte[] fileContent = exportService.exportToExcel(id);

        // Set content type for Excel
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=products.xlsx");

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    @GetMapping("product/top-sale")
    public ResponseEntity<?> getTopSaleProducts(@RequestParam("filter") String filter) {
        List<Product> products = productService.getTopSaleProducts(filter);
        return ResponseEntity.ok(Map.of("result", products));
    }

    @GetMapping("product/least-sale")
    public ResponseEntity<?> getLeastSaleProducts(@RequestParam("filter") String filter) {
        List<Product> products = productService.getLeastSaleProducts(filter);
        return ResponseEntity.ok(Map.of("result", products));
    }

}
