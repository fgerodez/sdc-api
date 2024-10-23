package com.sdc.fgerodez.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdc.fgerodez.product.domain.ProductRepository;
import com.sdc.fgerodez.product.infrastructure.SimpleProductRepository;
import com.sdc.fgerodez.product.infrastructure.persister.FileProductPersister;
import com.sdc.fgerodez.product.infrastructure.serializer.JacksonProductSerializer;
import com.sdc.fgerodez.product.infrastructure.storage.LocalProductStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@EnableJpaRepositories
@EnableJpaAuditing
@Configuration
public class RepositoryConfig {

    @Bean
    public ProductRepository productRepository(ObjectMapper objectMapper, @Value("${file.db.path}") String filePath) throws IOException {
        var serializer = new JacksonProductSerializer(objectMapper);

        var fileChannel = FileChannel.open(
                Paths.get(filePath),
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE
        );

        var productStorage = new LocalProductStorage(fileChannel, serializer, StandardCharsets.UTF_8);

        var productPersister = new FileProductPersister(productStorage);

        return new SimpleProductRepository(productPersister);
    }
}
