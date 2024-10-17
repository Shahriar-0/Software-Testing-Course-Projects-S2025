package mizdooni.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RestaurantTest {

    private Restaurant restaurant;
    private Rating rating1;
    private Rating rating2;

    @Mock
    private Table table1;

    @Mock
    private Table table2;

    @Mock
    private Review review1;

    @Mock
    private Review review2;

    @Mock
    private User user1;

    @Mock
    private User user2;

    @Mock
    private Address address;

    @Mock
    private User manager;

    @BeforeEach
    void setUp() {
        restaurant =
            new Restaurant(
                "Test Restaurant",
                manager,
                "Italian",
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                "A cozy Italian place.",
                address,
                "imageLink"
            );
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test Adding Tables to Restaurant")
    void testAddTable() {
        restaurant.addTable(table1);
        restaurant.addTable(table2);

        List<Table> tables = restaurant.getTables();
        assertEquals(2, tables.size());
        assertEquals(table1, tables.get(0));
        assertEquals(table2, tables.get(1));
    }

    @Test
    @DisplayName("Test getTable: Existing Table Number")
    void testGetTableExisting() {
        when(table1.getTableNumber()).thenReturn(1);
        when(table2.getTableNumber()).thenReturn(2);

        restaurant.addTable(table1);
        restaurant.addTable(table2);

        assertEquals(table2, restaurant.getTable(2));
    }

    @Test
    @DisplayName("Test getTable: Non-Existing Table Number")
    void testGetTableNonExisting() {
        when(table1.getTableNumber()).thenReturn(1);
        restaurant.addTable(table1);

        assertNull(restaurant.getTable(5));
    }

    @Test
    @DisplayName("Test Adding Reviews")
    void testAddingReview() {
        when(review1.getUser()).thenReturn(user1);
        when(review2.getUser()).thenReturn(user2);

        restaurant.addReview(review1);
        restaurant.addReview(review2);

        List<Review> reviews = restaurant.getReviews();
        assertEquals(2, reviews.size());
        assertEquals(review1, reviews.get(0));
        assertEquals(review2, reviews.get(1));
    }

    @Test
    @DisplayName("Test Replacing Reviews")
    void testReplacingReview() {
        when(review1.getUser()).thenReturn(user1);
        when(review2.getUser()).thenReturn(user1);

        restaurant.addReview(review1);
        restaurant.addReview(review2);

        List<Review> reviews = restaurant.getReviews();
        assertEquals(1, reviews.size()); // Only one review (replaced)
        assertEquals(review2, reviews.get(0));
    }

    @Test
    @DisplayName("Test getMaxSeatsNumber: Multiple Tables")
    void testGetMaxSeatsNumberMultipleTables() {
        when(table1.getSeatsNumber()).thenReturn(4);
        when(table2.getSeatsNumber()).thenReturn(6);

        restaurant.addTable(table1);
        restaurant.addTable(table2);

        assertEquals(6, restaurant.getMaxSeatsNumber());
    }

    @Test
    @DisplayName("Test getMaxSeatsNumber: No Tables")
    void testGetMaxSeatsNumberNoTables() {
        assertEquals(0, restaurant.getMaxSeatsNumber());
    }

    @ParameterizedTest
    @CsvSource(
        {
            // food1, service1, ambiance1, overall1, food2, service2, ambiance2, overall2
            "4.0, 3.5, 4.5, 4.0, 4.5, 4.0, 4.5, 4.0", // Two reviews, averaged
            "3.0, 2.5, 3.5, 3.0, 0.0, 0.0, 0.0, 0.0" // One review with 0 values in the second one
        }
    )
    @DisplayName("Test getAverageRating: Reviews Provided")
    void testGetAverageRating(
        double food1,
        double service1,
        double ambiance1,
        double overall1,
        double food2,
        double service2,
        double ambiance2,
        double overall2
    ) {
        rating1 = new Rating(food1, service1, ambiance1, overall1);
        rating2 = new Rating(food2, service2, ambiance2, overall2);

        when(review1.getRating()).thenReturn(rating1);
        when(review2.getRating()).thenReturn(rating2);

        when(review1.getUser()).thenReturn(user1);
        when(review2.getUser()).thenReturn(user2);

        restaurant.addReview(review1);
        restaurant.addReview(review2);

        Rating avgRating = restaurant.getAverageRating();

        assertEquals((food1 + food2) / 2, avgRating.food);
        assertEquals((service1 + service2) / 2, avgRating.service);
        assertEquals((ambiance1 + ambiance2) / 2, avgRating.ambiance);
        assertEquals((overall1 + overall2) / 2, avgRating.overall);
    }

    @Test
    @DisplayName("Test getAverageRating: No Reviews")
    void testGetAverageRatingNoReviews() {
        Rating avgRating = restaurant.getAverageRating();

        assertEquals(0.0, avgRating.food);
        assertEquals(0.0, avgRating.service);
        assertEquals(0.0, avgRating.ambiance);
        assertEquals(0.0, avgRating.overall);
    }

    @ParameterizedTest
    @CsvSource({ "4.1, 4", "4.6, 5", "4.5, 5", "3.0, 3", "0.0, 0", "-1.0, 0", "5.0, 5", "5.5, 5" })
    @DisplayName("Test getStarCount: Reviews Provided")
    void testGetStarCount(double overallRating, int expectedStarCount) {
        rating1 = new Rating();
        rating1.overall = overallRating;

        when(review1.getRating()).thenReturn(rating1);
        when(review1.getUser()).thenReturn(user1);

        restaurant.addReview(review1);

        assertEquals(expectedStarCount, restaurant.getStarCount());
    }

    @Test
    @DisplayName("Test getStarCount: No Reviews")
    void testGetStarCountNoReviews() {
        assertEquals(0, restaurant.getStarCount());
    }
}
