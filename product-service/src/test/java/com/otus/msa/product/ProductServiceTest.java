package com.otus.msa.product;

import com.otus.msa.product.adapter.in.http.CreateProductDto;
import com.otus.msa.product.adapter.out.pg.entity.CurrencyEnum;
import com.otus.msa.product.adapter.out.pg.entity.ProductEntity;
import com.otus.msa.product.domain.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ProductServiceTest extends BaseContainerTest {
    @Autowired
    private ProductService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Тест на поиск продукта по имени")
    void testFindProducts() {
        ProductEntity productEntity = service.getOne(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        Assertions.assertNotNull(productEntity);
    }

    @Test
    @DisplayName("Создание продукта")
    @WithMockUser
    void testCreateProduct() throws Exception {
        CreateProductDto productDto = CreateProductDto.builder()
                .userId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .price(BigDecimal.valueOf(1000))
                .name("Палатка")
                .description("<UNK>")
                .depositSum(BigDecimal.valueOf(100))
                .quantity(13)
                .currency(CurrencyEnum.RUB)
                .imgUrl("/////")
                .build();
        mockMvc.perform(post("/product-service/api/v1/product")
                        .content(new ObjectMapper().writeValueAsString(productDto))
                        .header("X-Request-Id", UUID.randomUUID())
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJTWHNtb2FZV2hYV1BhTWw1YmgtOFZYZXBoVlpqX0FvUnpmd2F4LTdvQlVJIn0.eyJleHAiOjE3NjQ0OTM1NTcsImlhdCI6MTc2NDQ5MzI1NywianRpIjoib25ydHJvOjc3Y2NmZWYzLWRiZmUtNmRkMC1kOWRlLWE4NTAwMWYzNTk5ZCIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4Ni9yZWFsbXMvb3R1cy1tc2EiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiMzU3YWQ4ZmItZDRiNS00ZDJiLWFkMjYtODQzNjA3YTI4OGViIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidXNlci1zZXJ2aWNlIiwic2lkIjoiNzNkZWIxY2UtMzgzOS00MzFjLWE4YWUtNGQwNjI5MDdkZGQ2IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1vdHVzLW1zYSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiLQnNCw0YDQuNGPINCc0LDRgNC40Y8iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJtYXJ1c2lrMCIsImdpdmVuX25hbWUiOiLQnNCw0YDQuNGPIiwiZmFtaWx5X25hbWUiOiLQnNCw0YDQuNGPIiwiZW1haWwiOiJtYXJ1c2lrMEB0ZXN0Lm1haWwifQ.twML73pdW1ctbO0PV-oP20fGKJNdV_q9nh2B47LGpjs25u1E19F6UyskQURFfKEBs2beEdgcqcNfPv0eh0b9ncxcCSYW-Pp5XxeoWLEldo2DfynZ9OLeItxvTvC7C9d-gpCKM4rYQUzC1M8O8ZxQPhga6wUCxTqHuAgWK_eHg5JjxFKdqOmLkYF9-hSWZ0FQgdLDc-CFeBInMDaJIce6LNp9vNH7L94XL7vyx8rGINiKbRQU9zhPpLZCnnir5tG8Z1BUTg2Qv5JjotE-gg09jwLTmrdAw8lbBDwv7KG6ieOIlKRRoClIupITImzgzrXxQApgWHy3gHOzN0BkFjxfgA")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful());
    }
}
