package ru.otus.msa.order.adapter.in.http;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
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
