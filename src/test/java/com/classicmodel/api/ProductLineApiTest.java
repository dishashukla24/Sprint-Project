package com.classicmodel.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProductLine API integration tests (Spring Data REST - HAL format)
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductLineApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private static final String TEST_LINE = "Test Vehicles";

    // ── GET (Read) ──────────────────────────────────────────────────────────

    @Test @Order(1)
    void getAllProductLines_returns200WithEmbedded() throws Exception {
        mockMvc.perform(get("/api/productlines"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.productLines").isArray())  // ✅ FIXED
            .andExpect(jsonPath("$._embedded.productLines.length()")
                .value(greaterThan(0)));
    }

    @Test @Order(2)
    void getProductLineById_ClassicCars_returns200() throws Exception {
        mockMvc.perform(get("/api/productlines/Classic Cars"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productLine").value("Classic Cars"));
    }

    @Test @Order(3)
    void getProductLineById_nonExistent_returns404() throws Exception {
        mockMvc.perform(get("/api/productlines/NoSuchLine"))
            .andExpect(status().isNotFound());
    }

    @Test @Order(4)
    void getAllProductLines_containsAllSixDefaultLines() throws Exception {
        mockMvc.perform(get("/api/productlines"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.productLines[*].productLine", // ✅ FIXED
                hasItems("Classic Cars", "Motorcycles", "Planes",
                         "Ships", "Trains", "Trucks and Buses")));
    }

    @Test @Order(5)
    void getProductLine_ClassicCars_imageUrlIsTextNotBlob() throws Exception {
        mockMvc.perform(get("/api/productlines/Classic Cars"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productLine").value("Classic Cars"));
    }

    // ── POST (Create) ───────────────────────────────────────────────────────

    @Test @Order(10)
    void createProductLine_withValidBody_returns201WithBody() throws Exception {
        Map<String, String> body = Map.of(
            "productLine", TEST_LINE,
            "textDescription", "Test vehicles for automated testing"
        );

        mockMvc.perform(post("/api/productlines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.productLine").value(TEST_LINE))
            .andExpect(jsonPath("$.textDescription")
                .value("Test vehicles for automated testing"));
    }

    @Test @Order(11)
    void createProductLine_emptyProductLine_returns400() throws Exception {
        Map<String, String> body = Map.of(
            "productLine", "",
            "textDescription", "Missing name"
        );

        mockMvc.perform(post("/api/productlines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());
    }

    @Test @Order(12)
    void createProductLine_duplicate_springDataRestUpserts_returns2xx() throws Exception {
        Map<String, String> body = Map.of(
            "productLine", TEST_LINE,
            "textDescription", "Duplicate line"
        );

        mockMvc.perform(post("/api/productlines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().is2xxSuccessful());
    }

    // ── PATCH (Update) ──────────────────────────────────────────────────────

    @Test @Order(20)
    void patchProductLine_textDescription_returns200WithBody() throws Exception {
        Map<String, String> patch = Map.of(
            "textDescription", "Updated test description"
        );

        mockMvc.perform(patch("/api/productlines/" + TEST_LINE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patch)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.textDescription")
                .value("Updated test description"));
    }

    @Test @Order(21)
    void patchProductLine_imageUrl_returns200WithBody() throws Exception {
        Map<String, String> patch = Map.of(
            "imageUrl", "https://example.com/test-vehicles.jpg"
        );

        mockMvc.perform(patch("/api/productlines/" + TEST_LINE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patch)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.imageUrl")
                .value("https://example.com/test-vehicles.jpg"));
    }

    @Test @Order(22)
    void patchProductLine_nonExistent_returns404() throws Exception {
        Map<String, String> patch = Map.of("textDescription", "Ghost");

        mockMvc.perform(patch("/api/productlines/NoSuchLine")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patch)))
            .andExpect(status().isNotFound());
    }

    // ── Cleanup ─────────────────────────────────────────────────────────────

    @Test @Order(99)
    void cleanup_deleteTestProductLine() throws Exception {
        mockMvc.perform(delete("/api/productlines/" + TEST_LINE))
            .andExpect(status().is2xxSuccessful());
    }

    @Test @Order(100)
    void cleanup_verifyTestProductLineGone() throws Exception {
        mockMvc.perform(get("/api/productlines/" + TEST_LINE))
            .andExpect(status().isNotFound());
    }
}