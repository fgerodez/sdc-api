package com.sdc.fgerodez.product.api;

import com.sdc.fgerodez.product.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product productDataDtoToProduct(ProductDataDTO productDataDTO);

    void updateProductFromDTO(ProductDataDTO productDataDTO, @MappingTarget Product product);
}
