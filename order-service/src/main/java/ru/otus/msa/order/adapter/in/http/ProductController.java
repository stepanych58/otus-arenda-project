package ru.otus.msa.order.adapter.in.http;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.otus.msa.order.api.http.ProductDto;
import ru.otus.msa.order.application.ProductService;

@RestController
@RequestMapping("order-service/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("product")
    public ProductDto createProduct(@RequestBody ProductDto productDto) {
        return productService.createProduct(productDto);
    }

    @GetMapping("product/list")
    public Page<ProductDto> createProduct(@RequestParam("page") Integer page) {
        return productService.getProducts(page);
    }
}
