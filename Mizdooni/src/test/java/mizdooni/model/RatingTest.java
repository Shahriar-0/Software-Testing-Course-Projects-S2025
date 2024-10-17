package mizdooni.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RatingTest {

    private Rating rating;

    @BeforeEach
    void setUp() {
        rating = new Rating();
    }

    @ParameterizedTest
    @CsvSource({
        "4.1, 4",   // Rounding down
        "4.6, 5",   // Rounding up
        "4.5, 5",   // Edge case, rounding up
        "3.0, 3",   // Whole number
        "0.0, 0",   // Zero rating
        "-1.0, 0",  // Below zero, should return 0
        "5.0, 5",   // Exactly 5, should return 5
        "5.5, 5",   // Above 5, should be capped at 5
    })
    @DisplayName("Test getStarCount for Different Overall Ratings")
    void testGetStarCount(double overallRating, int expectedStarCount) {
        rating.overall = overallRating;
        assertEquals(expectedStarCount, rating.getStarCount());
    }
}
