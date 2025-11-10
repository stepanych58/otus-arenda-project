package ru.otus.msa.order.application;

import org.springframework.data.domain.Page;
import ru.otus.msa.order.api.http.ProductDto;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);

    Page<ProductDto> getProducts(Integer page);
}
