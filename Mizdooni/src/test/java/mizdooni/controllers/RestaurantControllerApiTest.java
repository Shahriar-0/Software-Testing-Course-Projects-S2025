package mizdooni.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalTime;
import java.util.*;
import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.response.PagedList;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import mizdooni.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Restaurant mockRestaurant;

    @BeforeEach
    void setUp() {
        reset(restaurantService);

        Address address = new Address("Country", "City", "Street");
        mockRestaurant =
            new Restaurant(
                "Mock Restaurant",
                null, // Manager not required for tests
                "Italian",
                LocalTime.of(9, 0),
                LocalTime.of(23, 0),
                "A great restaurant",
                address,
                "imageLink"
            );
    }

    @Test
    @DisplayName("Test Get Restaurant Success")
    void testGetRestaurant_Success() throws Exception {
        int restaurantId = 1;

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);

        mockMvc
            .perform(get("/restaurants/{restaurantId}", restaurantId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("restaurant found"))
            .andExpect(jsonPath("$.data.name").value("Mock Restaurant"))
            .andExpect(jsonPath("$.data.type").value("Italian"));
    }

    @Test
    @DisplayName("Test Get Restaurant Not Found")
    void testGetRestaurant_NotFound() throws Exception {
        int restaurantId = 1;

        doThrow(new ResponseException(HttpStatus.NOT_FOUND, "restaurant not found"))
            .when(restaurantService)
            .getRestaurant(restaurantId);

        mockMvc
            .perform(get("/restaurants/{restaurantId}", restaurantId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("restaurant not found"));
    }

    @Test
    @DisplayName("Test Get Restaurants Success Single Restaurant")
    void testGetRestaurants_Success_SingleRestaurant() throws Exception {
        int page = 1;
        List<Restaurant> restaurants = List.of(mockRestaurant);
        PagedList<Restaurant> pagedRestaurants = new PagedList<>(restaurants, 1, 1);

        when(restaurantService.getRestaurants(eq(page), any()))
            .thenReturn(pagedRestaurants);

        mockMvc
            .perform(get("/restaurants").param("page", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("restaurants listed"))
            .andExpect(jsonPath("$.data.page").value(1))
            .andExpect(jsonPath("$.data.pageList[0].name").value("Mock Restaurant"))
            .andExpect(jsonPath("$.data.pageList[0].type").value("Italian"))
            .andExpect(jsonPath("$.data.pageList[0].address.country").value("Country"))
            .andExpect(jsonPath("$.data.pageList[0].address.city").value("City"))
            .andExpect(jsonPath("$.data.pageList[0].address.street").value("Street"));
    }

    @Test
    @Disabled("TODO: fix this test")
    @DisplayName("Test Get Restaurants Success Multiple Restaurants")
    void testGetRestaurants_Success_MultipleRestaurants() throws Exception {
        int page = 1;
        Restaurant mockRestaurant2 = new Restaurant("Mock Restaurant 2", null, "Italian", null, null, null, null, null);
        List<Restaurant> restaurants = List.of(mockRestaurant, mockRestaurant2);
        PagedList<Restaurant> pagedRestaurants = new PagedList<>(restaurants, 2, 1);

        when(restaurantService.getRestaurants(eq(page), any()))
            .thenReturn(pagedRestaurants);

        mockMvc
            .perform(get("/restaurants").param("page", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("restaurants listed"))
            .andExpect(jsonPath("$.data.page").value(2))
            .andExpect(jsonPath("$.data.pageList[1].name").value("Mock Restaurant"))
            .andExpect(jsonPath("$.data.pageList[1].type").value("Italian"))
            .andExpect(jsonPath("$.data.pageList[1].address.country").value("Country"))
            .andExpect(jsonPath("$.data.pageList[1].address.city").value("City"))
            .andExpect(jsonPath("$.data.pageList[1].address.street").value("Street"))
            .andExpect(jsonPath("$.data.pageList[0].name").value("Mock Restaurant 2"))
            .andExpect(jsonPath("$.data.pageList[0].type").value("Italian"))
            .andExpect(jsonPath("$.data.pageList[0].address.country").value("Country"))
            .andExpect(jsonPath("$.data.pageList[0].address.city").value("City"))
            .andExpect(jsonPath("$.data.pageList[0].address.street").value("Street"));
    }

    @Test
    @DisplayName("Test Get Managers Restaurants Success")
    void testGetManagersRestaurants_Success() throws Exception {
        int managerId = 1;

        List<Restaurant> restaurants = List.of(mockRestaurant);

        when(restaurantService.getManagerRestaurants(managerId)).thenReturn(restaurants);

        mockMvc
            .perform(get("/restaurants/manager/{managerId}", managerId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("manager restaurants listed"))
            .andExpect(jsonPath("$.data[0].name").value("Mock Restaurant"))
            .andExpect(jsonPath("$.data[0].type").value("Italian"))
            .andExpect(jsonPath("$.data[0].address.country").value("Country"))
            .andExpect(jsonPath("$.data[0].address.city").value("City"))
            .andExpect(jsonPath("$.data[0].address.street").value("Street"));
    }

    @Test
    @DisplayName("Test Get Managers Restaurants Empty List")
    void testGetManagersRestaurants_NotFound() throws Exception {
        int managerId = 1;

        when(restaurantService.getManagerRestaurants(managerId)).thenReturn(List.of());

        mockMvc
            .perform(get("/restaurants/manager/{managerId}", managerId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("manager restaurants listed"));
    }

    @Test
    @DisplayName("Test Add Restaurant Success")
    void testAddRestaurant_Success() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "New Restaurant");
        params.put("type", "Italian");
        params.put("startTime", "09:00");
        params.put("endTime", "23:00");
        params.put("description", "A nice place");
        params.put("image", "imageLink");
        params.put("address", Map.of("country", "Country", "city", "City", "street", "Street"));

        when(
            restaurantService.addRestaurant(
                anyString(),
                anyString(),
                any(),
                any(),
                anyString(),
                any(),
                anyString()
            )
        ).thenReturn(42);

        mockMvc
            .perform(
                post("/restaurants")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("restaurant added"))
            .andExpect(jsonPath("$.data").value(42));
    }

    @Test
    @DisplayName("Test Add Restaurant Missing Parameters")
    void testAddRestaurant_MissingParameters() throws Exception {
        Map<String, Object> params = new HashMap<>();

        mockMvc
            .perform(
                post("/restaurants")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("parameters missing"));
    }

    @Test
    @DisplayName("Test Add Restaurant Invalid Parameters")
    void testAddRestaurant_InvalidParameters() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", 13);
        params.put("type", "Italian");
        params.put("startTime", "09:00");
        params.put("endTime", "23:00");
        params.put("description", "A nice place");
        params.put("image", "imageLink");
        params.put("address", Map.of("country", "Country", "city", "City", "street", "Street"));

        mockMvc
            .perform(
                post("/restaurants")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("bad parameter type"));
    }

    @Test
    @DisplayName("Test Add Restaurant Duplication")
    void testAddRestaurant_Duplication() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Mock Restaurant");
        params.put("type", "Italian");
        params.put("startTime", "09:00");
        params.put("endTime", "23:00");
        params.put("description", "A nice place");
        params.put("image", "imageLink");
        params.put("address", Map.of("country", "Country", "city", "City", "street", "Street"));

        doThrow(new ResponseException(HttpStatus.BAD_REQUEST, "restaurant name is taken"))
            .when(restaurantService)
            .addRestaurant(anyString(), anyString(), any(), any(), anyString(), any(), anyString());

        mockMvc
            .perform(
                post("/restaurants")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("restaurant name is taken"));
    }

    @Test
    @DisplayName("Test Add Restaurant Manager Not Found")
    void testAddRestaurant_ManagerNotFound() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "New Restaurant");
        params.put("type", "Italian");
        params.put("startTime", "09:00");
        params.put("endTime", "23:00");
        params.put("description", "A nice place");
        params.put("image", "imageLink");
        params.put("address", Map.of("country", "Country", "city", "City", "street", "Street"));

        doThrow(new ResponseException(HttpStatus.NOT_FOUND, "manager not found"))
            .when(restaurantService)
            .addRestaurant(anyString(), anyString(), any(), any(), anyString(), any(), anyString());

        mockMvc
            .perform(
                post("/restaurants")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("manager not found"));
    }

    @Test
    @DisplayName("Test Add Restaurant Invalid Working Time")
    void testAddRestaurant_InvalidWorkingTime() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "New Restaurant");
        params.put("type", "Italian");
        params.put("startTime", "09:00");
        params.put("endTime", "23:00");
        params.put("description", "A nice place");
        params.put("image", "imageLink");
        params.put("address", Map.of("country", "Country", "city", "City", "street", "Street"));

        doThrow(new ResponseException(HttpStatus.BAD_REQUEST, "invalid working time"))
            .when(restaurantService)
            .addRestaurant(anyString(), anyString(), any(), any(), anyString(), any(), anyString());

        mockMvc
            .perform(
                post("/restaurants")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("invalid working time"));
    }

    @Test
    @DisplayName("Test Validate Restaurant Name Success")
    void testValidateRestaurantName_Success() throws Exception {
        String name = "Unique Restaurant";

        when(restaurantService.restaurantExists(name)).thenReturn(false);

        mockMvc
            .perform(get("/validate/restaurant-name").param("data", name))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("restaurant name is available"));
    }

    @Test
    @DisplayName("Test Validate Restaurant Name Taken")
    void testValidateRestaurantName_Taken() throws Exception {
        String name = "Taken Restaurant";

        when(restaurantService.restaurantExists(name)).thenReturn(true);

        mockMvc
            .perform(get("/validate/restaurant-name").param("data", name))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("restaurant name is taken"));
    }

    @Test
    @DisplayName("Test Get Restaurant Types Success")
    void testGetRestaurantTypes_Success() throws Exception {
        Set<String> types = Set.of("Italian", "Mexican", "Indian");

        when(restaurantService.getRestaurantTypes()).thenReturn(types);

        mockMvc
            .perform(get("/restaurants/types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("restaurant types"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0]").value("Italian"));
    }

    @Test
    @DisplayName("Test Get Restaurant Locations Success")
    void testGetRestaurantLocations_Success() throws Exception {
        Map<String, Set<String>> locations = Map.of(
            "Country",
            Set.of("City1", "City2"),
            "AnotherCountry",
            Set.of("CityA", "CityB")
        );

        when(restaurantService.getRestaurantLocations()).thenReturn(locations);

        mockMvc
            .perform(get("/restaurants/locations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("restaurant locations"))
            .andExpect(jsonPath("$.data.Country").isArray())
            .andExpect(jsonPath("$.data.Country[0]").value("City1"))
            .andExpect(jsonPath("$.data.Country[1]").value("City2"))
            .andExpect(jsonPath("$.data.AnotherCountry").isArray())
            .andExpect(jsonPath("$.data.AnotherCountry[0]").value("CityA"))
            .andExpect(jsonPath("$.data.AnotherCountry[1]").value("CityB"));
    }
}
