package mizdooni.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.model.Table;
import mizdooni.model.User;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import mizdooni.service.TableService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    @DisplayName("Test Get Tables Success")
    void testGetTables_Success() throws Exception {
        int restaurantId = 1;
        List<Table> tables = Arrays.asList(new Table(1, 4, 2), new Table(2, 6, 4));

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);
        when(tableService.getTables(restaurantId)).thenReturn(tables);

        mockMvc
            .perform(get("/tables/{restaurantId}", restaurantId))
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

    @Test
    @DisplayName("Test Get Tables Restaurant Not Found")
    void testGetTables_RestaurantNotFound() throws Exception {
        int restaurantId = 1;

        doThrow(new ResponseException(HttpStatus.NOT_FOUND, "restaurant not found"))
            .when(restaurantService)
            .getRestaurant(restaurantId);

        mockMvc
            .perform(get("/tables/{restaurantId}", restaurantId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("restaurant not found"));
    }

    @Test
    @DisplayName("Test Add Table Success")
    void testAddTable_Success() throws Exception {
        int restaurantId = 1;
        Map<String, String> params = new HashMap<>();
        params.put("seatsNumber", "4");

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);
        doNothing().when(tableService).addTable(restaurantId, 4);

        mockMvc
            .perform(
                post("/tables/{restaurantId}", restaurantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("table added"));
    }

    @Test
    @DisplayName("Test Add Table Missing Parameters")
    void testAddTable_MissingParameters() throws Exception {
        int restaurantId = 1;
        Map<String, String> params = new HashMap<>();

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);

        mockMvc
            .perform(
                post("/tables/{restaurantId}", restaurantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("parameters missing"));
    }

    @Test
    @DisplayName("Test Add Table Invalid Seats Number")
    void testAddTable_InvalidSeatsNumber() throws Exception {
        int restaurantId = 1;
        Map<String, String> params = new HashMap<>();
        params.put("seatsNumber", "invalid"); // Invalid value

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);

        mockMvc
            .perform(
                post("/tables/{restaurantId}", restaurantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("bad parameter type"));
    }

    @Test
    @DisplayName("Test Add Table Restaurant Not Found")
    void testAddTable_RestaurantNotFound() throws Exception {
        int restaurantId = 1;
        Map<String, String> params = new HashMap<>();
        params.put("seatsNumber", "4");

        doThrow(new ResponseException(HttpStatus.NOT_FOUND, "restaurant not found"))
            .when(restaurantService)
            .getRestaurant(restaurantId);

        mockMvc
            .perform(
                post("/tables/{restaurantId}", restaurantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params))
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("restaurant not found"));
    }

    @Test
    @DisplayName("Test Add Table User Not Manager")
    void testAddTable_UserNotManager() throws Exception {
        int restaurantId = 1;
        Map<String, String> params = new HashMap<>();
        params.put("seatsNumber", "4");

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);
        doThrow(new ResponseException(HttpStatus.FORBIDDEN, "user not manager"))
            .when(tableService)
            .addTable(restaurantId, 4);

        mockMvc
            .perform(
                post("/tables/{restaurantId}", restaurantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("user not manager"));
    }

    @Test
    @DisplayName("Test Add Table Invalid Manager Restaurant")
    void testAddTable_InvalidManagerRestaurant() throws Exception {
        int restaurantId = 1;
        Map<String, String> params = new HashMap<>();
        params.put("seatsNumber", "4");

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);
        doThrow(new ResponseException(HttpStatus.FORBIDDEN, "invalid manager restaurant"))
            .when(tableService)
            .addTable(restaurantId, 4);

        mockMvc
            .perform(
                post("/tables/{restaurantId}", restaurantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("invalid manager restaurant"));
    }
}
