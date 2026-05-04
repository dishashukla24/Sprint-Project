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
class CustomerApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private static final int TEST_CUST_NUM = 99901;

   

    @Test @Order(1)
    void getAllCustomers_returns200WithEmbedded() throws Exception {
        mockMvc.perform(get("/api/customers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.customers").isArray())
            .andExpect(jsonPath("$._embedded.customers.length()").value(greaterThan(0)));
    }

    @Test @Order(2)
    void getCustomerById_whenExists_returns200() throws Exception {
        mockMvc.perform(get("/api/customers/103"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerNumber").value(103));
    }

    @Test @Order(3)
    void getCustomerById_whenNotExists_returns404() throws Exception {
        mockMvc.perform(get("/api/customers/999999"))
            .andExpect(status().isNotFound());
    }

    @Test @Order(4)
    void searchByCountry_France_returns200WithArray() throws Exception {
        mockMvc.perform(get("/api/customers/search/findByCountry?country=France"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.customers").isArray())
            .andExpect(jsonPath("$._embedded.customers.length()").value(greaterThan(0)));
    }

    @Test @Order(5)
    void searchByCustomerNameContaining_returns200() throws Exception {
        mockMvc.perform(get("/api/customers/search/findByCustomerNameContaining?name=Auto"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.customers").isArray());
    }

    @Test @Order(6)
    void searchBySalesRepEmployeeNumber_returns200() throws Exception {
        mockMvc.perform(get("/api/customers/search/findBySalesRepEmployee_EmployeeNumber?salesRepEmployeeNumber=1165"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.customers").isArray());
    }

 

    @Test @Order(10)
    void createCustomer_withValidBody_returns201WithBody() throws Exception {
        Map<String, Object> body = Map.of(
            "customerNumber",  TEST_CUST_NUM,
            "customerName",    "Test Co Ltd",
            "contactLastName", "Tester",
            "contactFirstName","Alice",
            "phone",           "555-0001",
            "addressLine1",    "1 Test Lane",
            "city",            "Testville",
            "country",         "Testland"
        );
        mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.customerNumber").value(TEST_CUST_NUM))
            .andExpect(jsonPath("$.customerName").value("Test Co Ltd"));
    }

    @Test @Order(11)
    void createCustomer_missingCustomerName_returns400() throws Exception {
        Map<String, Object> body = Map.of(
            "customerNumber",  99902,
            "contactLastName", "Missing",
            "contactFirstName","Name",
            "phone",           "555-0002",
            "addressLine1",    "2 Test St",
            "city",            "Testville",
            "country",         "Testland"
        );
        mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());
    }

    @Test @Order(12)
    void createCustomer_duplicateId_springDataRestUpserts_returns2xx() throws Exception {
        Map<String, Object> body = Map.of(
            "customerNumber",  TEST_CUST_NUM,
            "customerName",    "Dup Co",
            "contactLastName", "Dup",
            "contactFirstName","Dup",
            "phone",           "555-9999",
            "addressLine1",    "Dup St",
            "city",            "Testville",
            "country",         "Testland"
        );
        mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().is2xxSuccessful());
    }

  
    @Test @Order(20)
    void patchCustomer_phone_returns200WithBody() throws Exception {
        Map<String, String> patch = Map.of("phone", "555-9999");
        mockMvc.perform(patch("/api/customers/" + TEST_CUST_NUM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patch)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.phone").value("555-9999"));
    }

    @Test @Order(21)
    void patchCustomer_creditLimit_returns200WithBody() throws Exception {
        Map<String, Object> patch = Map.of("creditLimit", 75000.00);
        mockMvc.perform(patch("/api/customers/" + TEST_CUST_NUM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patch)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.creditLimit").value(75000.00));
    }

    @Test @Order(22)
    void patchCustomer_nonExistent_returns404() throws Exception {
        Map<String, String> patch = Map.of("phone", "000-0000");
        mockMvc.perform(patch("/api/customers/999999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patch)))
            .andExpect(status().isNotFound());
    }

}
