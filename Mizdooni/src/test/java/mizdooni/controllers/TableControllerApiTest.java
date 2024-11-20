package mizdooni.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.model.Table;
import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.service.RestaurantService;
import mizdooni.service.TableService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TableControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private TableService tableService;

    @Autowired
    private ObjectMapper objectMapper;

    private Restaurant mockRestaurant;

    @BeforeEach
    void setUp() {
        reset(restaurantService, tableService);

        Address address = new Address("Country", "City", null);
        User manager = new User(
            "manager",
            "pass",
            "email@example.com",
            address,
            User.Role.manager
        );
        mockRestaurant =
            new Restaurant(
                "Mock Restaurant",
                manager,
                "Italian",
                LocalTime.of(9, 0),
                LocalTime.of(23, 0),
                "Nice place",
                address,
                "imageLink"
            );
    }

    @Test
    void testGetTablesSuccess() throws Exception {
        int restaurantId = 1;
        List<Table> tables = Arrays.asList(
            new Table(1, 4, 2),
            new Table(2, 6, 4)
        );

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);
        when(tableService.getTables(restaurantId)).thenReturn(tables);

        mockMvc.perform(get("/tables/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("tables listed"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].tableNumber").value(1))
                .andExpect(jsonPath("$.data[0].seatsNumber").value(2))
                .andExpect(jsonPath("$.data[1].tableNumber").value(2))
                .andExpect(jsonPath("$.data[1].seatsNumber").value(4));
    }
}
