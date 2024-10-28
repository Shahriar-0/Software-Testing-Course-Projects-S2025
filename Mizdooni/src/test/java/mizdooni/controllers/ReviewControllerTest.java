package mizdooni.controllers;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mizdooni.model.Address;
import mizdooni.model.Rating;
import mizdooni.model.Restaurant;
import mizdooni.model.Review;
import mizdooni.model.User;
import mizdooni.response.PagedList;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import mizdooni.service.ReviewService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class ReviewControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private Restaurant mockRestaurant;
    private PagedList<Review> mockPagedReviews;

    @Before
    public void setUp() {
        Address address = new Address("Country", "City", null);
        User manager = new User(
            "manager",
            "pass",
            "email@example.com",
            address,
            User.Role.manager
        );
        User client = new User(
            "client",
            "pass",
            "email@example.com",
            address,
            User.Role.client
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

        Review review1 = new Review(client, new Rating(), "Good", null);
        List<Review> reviewList = Collections.singletonList(review1);
        mockPagedReviews = new PagedList<>(reviewList, 1, 1);
    }

    @Test
    @DisplayName("Test Get Reviews Success")
    public void testGetReviewsSuccess() throws Exception {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        when(reviewService.getReviews(1, 1)).thenReturn(mockPagedReviews);

        Response response = reviewController.getReviews(1, 1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, ControllersTestUtils.getField(response, "status"));
        assertTrue((Boolean) ControllersTestUtils.getField(response, "success"));
        assertEquals(
            "reviews for restaurant (1): Mock Restaurant",
            ControllersTestUtils.getField(response, "message")
        );
        PagedList<Review> responseData = (PagedList<Review>) ControllersTestUtils.getField(
            response,
            "data"
        );
        assertNotNull(responseData);
        assertEquals(mockPagedReviews, responseData);
    }

    @Test
    @DisplayName("Test Get Reviews Restaurant Not Found")
    public void testGetReviewsRestaurantNotFound() {
        when(restaurantService.getRestaurant(1)).thenReturn(null);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reviewController.getReviews(1, 1)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("restaurant not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test Add Review Success")
    public void testAddReviewSuccess() throws Exception {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Great place!");
        Map<String, Number> ratingMap = new HashMap<>();
        ratingMap.put("food", 4.5);
        ratingMap.put("service", 4.0);
        ratingMap.put("ambiance", 4.5);
        ratingMap.put("overall", 4.3);
        params.put("rating", ratingMap);

        Response response = reviewController.addReview(1, params);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, ControllersTestUtils.getField(response, "status"));
        assertTrue((Boolean) ControllersTestUtils.getField(response, "success"));
        assertEquals(
            "review added successfully",
            ControllersTestUtils.getField(response, "message")
        );
    }

    @Test
    @DisplayName("Test Add Review Missing Parameters")
    public void testAddReviewMissingParameters() {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Great place!");

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reviewController.addReview(1, params)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Test Add Review Invalid Rating Type")
    public void testAddReviewInvalidRatingType() {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Great place!");
        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("food", "five"); // Invalid type
        ratingMap.put("service", 4.0);
        ratingMap.put("ambiance", 4.5);
        ratingMap.put("overall", 4.3);
        params.put("rating", ratingMap);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reviewController.addReview(1, params)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test Add Review Restaurant Not Found")
    public void testAddReviewRestaurantNotFound() {
        when(restaurantService.getRestaurant(1)).thenReturn(null);

        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Great place!");
        Map<String, Number> ratingMap = new HashMap<>();
        ratingMap.put("food", 4.5);
        ratingMap.put("service", 4.0);
        ratingMap.put("ambiance", 4.5);
        ratingMap.put("overall", 4.3);
        params.put("rating", ratingMap);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reviewController.addReview(1, params)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("restaurant not found", exception.getMessage());
    }
}
