package com.sdc.fgerodez.acceptance;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource("/test.properties")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductApiTests {

    @Autowired
    private MockMvc mvc;

    @Test
    @Order(1)
    void productCreation() throws Exception {
        var firstPayload = """
                    {"code": "testCode", "inventoryStatus": "INSTOCK"}
                """;

        mvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(firstPayload))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("testCode"))
                .andExpect(jsonPath("$.inventoryStatus").value("INSTOCK"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNumber())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNumber());

        var secondPayload = """
                    {"code": "secondProduct", "inventoryStatus": "INSTOCK"}
                """;

        mvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(secondPayload))
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    @Order(2)
    void productEdition() throws Exception {
        var payload = """
                    {"id": 1, "code": "testCodeUpdated", "inventoryStatus": "LOWSTOCK"}
                """;

        mvc.perform(patch("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.code").value("testCodeUpdated"))
                .andExpect(jsonPath("$.inventoryStatus").value("LOWSTOCK"));
    }

    @Test
    @Order(3)
    void productRetrieval() throws Exception {
        mvc.perform(get("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("testCodeUpdated"))
                .andExpect(jsonPath("$.inventoryStatus").value("LOWSTOCK"));
    }

    @Test
    @Order(4)
    void productList() throws Exception {
        mvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Order(5)
    void productDeletion() throws Exception {
        mvc.perform(delete("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        mvc.perform(delete("/products/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }
}
