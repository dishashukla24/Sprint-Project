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

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OfficeApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private static final String TEST_CODE = "99";

    @Test @Order(1)
    void getAllOffices_returns200WithEmbedded() throws Exception {
        mockMvc.perform(get("/api/offices"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.offices").isArray())
            .andExpect(jsonPath("$._embedded.offices.length()").value(greaterThan(0)));
    }

    @Test @Order(2)
    void getOfficeById_whenExists_returns200() throws Exception {
        mockMvc.perform(get("/api/offices/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.officeCode").value("1"));
    }

    @Test @Order(3)
    void getOfficeById_whenNotExists_returns404() throws Exception {
        mockMvc.perform(get("/api/offices/ZZZZ"))
            .andExpect(status().isNotFound());
    }

    @Test @Order(4)
    void searchByCountry_returns200WithArray() throws Exception {
        mockMvc.perform(get("/api/offices/search/findByCountry?country=USA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.offices").isArray())
            .andExpect(jsonPath("$._embedded.offices.length()").value(greaterThan(0)));
    }

    @Test @Order(5)
    void searchByCity_Tokyo_returns200() throws Exception {
        mockMvc.perform(get("/api/offices/search/findByCity?city=Tokyo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.offices[0].city").value("Tokyo"));
    }

    @Test @Order(6)
    void searchByTerritory_returns200() throws Exception {
        mockMvc.perform(get("/api/offices/search/findByTerritory?territory=NA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.offices").isArray());
    }

    @Test @Order(10)
    void createOffice_withValidBody_returns201WithBody() throws Exception {
        Map<String, String> body = Map.of(
            "officeCode",   TEST_CODE,
            "city",         "Test City",
            "phone",        "+1 000 000 0000",
            "addressLine1", "123 Test St",
            "country",      "Testland",
            "postalCode",   "00000",
            "territory",    "TEST"
        );
        mockMvc.perform(post("/api/offices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.officeCode").value(TEST_CODE))
            .andExpect(jsonPath("$.city").value("Test City"));
    }

    @Test @Order(11)
    void createOffice_missingCity_returns400() throws Exception {
        Map<String, String> body = Map.of(
            "officeCode",   "98",
            "phone",        "+1 000 000 0001",
            "addressLine1", "456 Test Ave",
            "country",      "Testland",
            "postalCode",   "00001",
            "territory",    "TEST"
        );
        mockMvc.perform(post("/api/offices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());
    }

    @Test @Order(12)
    void createOffice_duplicateCode_springDataRestUpserts_returns2xx() throws Exception {
        Map<String, String> body = Map.of(
            "officeCode",   TEST_CODE,
            "city",         "Duplicate City",
            "phone",        "+1 111 111 1111",
            "addressLine1", "Dup St",
            "country",      "Testland",
            "postalCode",   "11111",
            "territory",    "TEST"
        );
        mockMvc.perform(post("/api/offices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().is2xxSuccessful());
    }

    @Test @Order(20)
    void patchOffice_city_returns200WithUpdatedCity() throws Exception {
        Map<String, String> patch = Map.of("city", "Updated City");
        mockMvc.perform(patch("/api/offices/" + TEST_CODE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patch)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.city").value("Updated City"));
    }

    @Test @Order(21)
    void patchOffice_phoneAndTerritory_returns200() throws Exception {
        Map<String, String> patch = Map.of(
            "phone",     "+1 999 999 9999",
            "territory", "UPDATED"
        );
        mockMvc.perform(patch("/api/offices/" + TEST_CODE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patch)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.territory").value("UPDATED"))
            .andExpect(jsonPath("$.phone").value("+1 999 999 9999"));
    }

    @Test @Order(22)
    void patchOffice_nonExistent_returns404() throws Exception {
        Map<String, String> patch = Map.of("city", "Ghost City");
        mockMvc.perform(patch("/api/offices/ZZZZ")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patch)))
            .andExpect(status().isNotFound());
    }

    @Test @Order(99)
    void cleanup_deleteTestOffice() throws Exception {
        mockMvc.perform(delete("/api/offices/" + TEST_CODE))
            .andExpect(status().is2xxSuccessful());
    }
}