package ru.otus.msa.order.adapter.out.pg;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otus.msa.product.api.kafka.dto.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.msa.order.adapter.out.pg.entity.ProductEntity;
import ru.otus.msa.order.adapter.out.pg.repository.ProductEntityRepository;
import ru.otus.msa.order.api.http.ProductDto;
import ru.otus.msa.order.application.ProductMapper;
import ru.otus.msa.order.application.ProductService;
import ru.otus.msa.order.application.exception.CreateProductException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final int DEFAULT_PAGE_SIZE = 15;

    private final ProductEntityRepository products;

    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        ProductEntity entity = productMapper.toEntity(productDto);
        return Optional.of(products.save(entity))
                .map(savedEntity -> {
                    log.info("Добавлен продукт {}", savedEntity);
                    return productMapper.toDto(savedEntity);
                })
                .orElseThrow(CreateProductException::new);
    }

    @Override
    public Page<ProductDto> getProducts(Integer page) {
        return products
                .findAll(PageRequest.of(Optional.ofNullable(page).orElse(0),
                                        DEFAULT_PAGE_SIZE,
                                        Sort.by(Sort.Direction.DESC, "id")))
                .map(productMapper::toDto);
    }

    @Override
    @Transactional
    public ProductEntity createProduct(ProductEvent productDto) {
        ProductEntity toSave = productMapper.toEntity(productDto);
        return products.findByProductId(productDto.productId())
                .map(product -> {
                    toSave.setId(product.getId());
                    return products.save(toSave);
                })
                .orElseGet(() -> products.save(toSave));
    }
}
