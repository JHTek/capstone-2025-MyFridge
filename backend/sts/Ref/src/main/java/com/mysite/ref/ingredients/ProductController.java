package com.mysite.ref.ingredients;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.ref.dto.ProductDto;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/expiring")
    public List<ProductDto> getExpiringProducts(
            @RequestParam("userId") String userId,
            @RequestParam("days") int alertDays) {
        
        return productService.getExpiringProducts(userId, alertDays);
    }
}