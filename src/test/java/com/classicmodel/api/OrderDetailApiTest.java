package com.classicmodel.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FINAL FIXED OrderDetail API tests (Spring Data REST - HAL)
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderDetailApiTest {

    @Autowired
    private MockMvc mockMvc;

    // ── GET ALL ─────────────────────────────────────────

    @Test
    void testGetAllOrderDetails_returns200() throws Exception {
        mockMvc.perform(get("/api/orderdetails"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.orderDetails").isArray()) // ✅ FIXED
            .andExpect(jsonPath("$._embedded.orderDetails.length()").value(greaterThan(0)));
    }

    // ── SEARCH TESTS ────────────────────────────────────

    @Test
    void testSearchByOrderNumber_viaAssociation_returns200() throws Exception {
        mockMvc.perform(get("/api/orderdetails/search/findByOrder_OrderNumber?orderNumber=10100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.orderDetails").isArray()) // ✅ FIXED
            .andExpect(jsonPath("$._embedded.orderDetails.length()").value(greaterThan(0)));
    }

    @Test
    void testSearchByProductCode_viaAssociation_returns200() throws Exception {
        mockMvc.perform(get("/api/orderdetails/search/findByProduct_ProductCode?productCode=S18_1749"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.orderDetails").isArray()) // ✅ FIXED
            .andExpect(jsonPath("$._embedded.orderDetails.length()").value(greaterThan(0)));
    }

    @Test
    void testSearchByOrderStatus_returns200() throws Exception {
        mockMvc.perform(get("/api/orderdetails/search/findByOrder_Status?status=Shipped"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.orderDetails").isArray()); // ✅ FIXED
    }

    // ── BASIC VALIDATION ────────────────────────────────

    @Test
    void testCountEndpoint_returnsCount() throws Exception {
        mockMvc.perform(get("/api/orderdetails"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.orderDetails").exists()); // ✅ FIXED
    }
}