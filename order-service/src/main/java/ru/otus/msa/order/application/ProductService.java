package ru.otus.msa.order.application;

import org.springframework.data.domain.Page;

import com.otus.msa.product.api.kafka.dto.ProductEvent;
import ru.otus.msa.order.adapter.out.pg.entity.ProductEntity;
import ru.otus.msa.order.api.http.ProductDto;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);

    ProductEntity createProduct(ProductEvent productDto);

    Page<ProductDto> getProducts(Integer page);
}
