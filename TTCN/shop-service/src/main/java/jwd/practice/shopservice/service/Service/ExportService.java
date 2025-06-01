package jwd.practice.shopservice.service.Service;


import jwd.practice.shopservice.entity.Product;
import jwd.practice.shopservice.repository.Product_Repository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExportService {
    Product_Repository productRepository;

    public  byte[] exportToExcel(int id) {
        Product product = this.productRepository.findById(id).get();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("product");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ProductId");
        header.createCell(1).setCellValue("ProductName");
        header.createCell(2).setCellValue("Price");
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(product.getProductId());
        row.createCell(1).setCellValue(product.getProductName());
        row.createCell(2).setCellValue(product.getPrice());
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos);
            return bos.toByteArray();  // return byte array of the Excel file
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
