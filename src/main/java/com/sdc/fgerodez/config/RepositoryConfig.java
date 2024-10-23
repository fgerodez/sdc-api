package com.sdc.fgerodez.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdc.fgerodez.product.domain.ProductRepository;
import com.sdc.fgerodez.product.infrastructure.FileProductRepository;
import com.sdc.fgerodez.product.infrastructure.serializer.JacksonProductSerializer;
import com.sdc.fgerodez.product.infrastructure.storage.NioProductStorage;
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
        var serializer = new JacksonProductSerializer(objectMapper, StandardCharsets.UTF_8);

        var fileChannel = FileChannel.open(
                Paths.get(filePath),
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE
        );

        var nioProductStorage = new NioProductStorage(fileChannel, serializer);

        return new FileProductRepository(nioProductStorage);
    }
}
