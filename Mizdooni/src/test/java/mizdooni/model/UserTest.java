package mizdooni.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserTest {

    private User user;
    private Address address;

    @Mock
    private Restaurant restaurant;

    @Mock
    private Reservation reservation1;

    @Mock
    private Reservation reservation2;

    @BeforeEach
    void setUp() {
        address = new Address("Country", "City", "123 Main St");
        user =
            new User(
                "mahdies",
                "password123",
                "mahdi@example.com",
                address,
                User.Role.client
            );
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({ "password123, true", "wrongpassword, false" })
    @DisplayName("Test Password Verification")
    void testCheckPassword(String inputPassword, boolean expected) {
        assertEquals(expected, user.checkPassword(inputPassword));
    }

    @Test
    @DisplayName("Test Add Reservation to User")
    void testAddReservation() {
        LocalDateTime now = LocalDateTime.now();
        when(reservation1.getDateTime()).thenReturn(now.plusDays(1));
        when(reservation1.isCancelled()).thenReturn(false);

        user.addReservation(reservation1);

        assertEquals(1, user.getReservations().size());
        assertEquals(reservation1, user.getReservations().get(0));
    }

    @Test
    @DisplayName("Test Multiple Reservations with Incrementing Reservation Numbers")
    void testAddMultipleReservations() {
        user.addReservation(reservation1);
        user.addReservation(reservation2);

        assertEquals(2, user.getReservations().size());
        verify(reservation1).setReservationNumber(0);
        verify(reservation2).setReservationNumber(1);
    }

    @Test
    @DisplayName("Test Get Reservation By Number after Cancellation")
    void testGetReservationByNumber() {
        when(reservation1.getReservationNumber()).thenReturn(0);
        when(reservation1.isCancelled()).thenReturn(false);
        when(reservation2.getReservationNumber()).thenReturn(1);
        when(reservation2.isCancelled()).thenReturn(true);

        user.addReservation(reservation1);
        user.addReservation(reservation2);

        assertEquals(reservation1, user.getReservation(0));
        assertNull(user.getReservation(1)); // Cancelled reservation should return null
    }

    @ParameterizedTest
    @CsvSource(
        {
            // Format: reservation1Cancelled, reservation1DateOffset, reservation2Cancelled, reservation2DateOffset, expected
            "false, -2, false, -1, true", // Two valid reservations, should return true
            "true, -2, false, -1, true", // One cancelled, one valid reservation, should return true
            "true, -2, true, -1, false", // Two cancelled reservations, should return false
            "true, -2, false, 1, false", // One past cancelled, one future, should return false
            "false, -2, true, -3, true", // One valid, one cancelled, should return true
            "true, -2, true, -1, false", // Two past cancelled, should return false
            "false, -2, true, 1, true", // One valid past, one future cancelled, should return true
            "false, -1, false, -3, true", // Both past, not cancelled, should return true
            "true, -1, false, -3, true", // One cancelled past, one valid past, should return true
            "true, 1, false, 2, false", // Both in future, one cancelled, should return false
            "false, -1, false, 1, true" // One valid past, one future, should return true
        }
    )
    @DisplayName("Test Check Reserved Restaurant with multiple reservation conditions")
    void testCheckReservedRestaurant(
        boolean reservation1Cancelled,
        int reservation1DateOffset,
        boolean reservation2Cancelled,
        int reservation2DateOffset,
        boolean expected
    ) {
        LocalDateTime reservation1Time = LocalDateTime.now().plusDays(reservation1DateOffset);
        LocalDateTime reservation2Time = LocalDateTime.now().plusDays(reservation2DateOffset);

        when(reservation1.getDateTime()).thenReturn(reservation1Time);
        when(reservation1.isCancelled()).thenReturn(reservation1Cancelled);
        when(reservation1.getRestaurant()).thenReturn(restaurant);

        when(reservation2.getDateTime()).thenReturn(reservation2Time);
        when(reservation2.isCancelled()).thenReturn(reservation2Cancelled);
        when(reservation2.getRestaurant()).thenReturn(restaurant);

        user.addReservation(reservation1);
        user.addReservation(reservation2);

        assertEquals(expected, user.checkReserved(restaurant));
    }

    @Test
    @DisplayName("Test Null Reservation Handling")
    void testNullReservation() {
        assertNull(user.getReservation(999)); // Non-existent reservation number should return null
    }

    @Test
    @DisplayName("Test Check Reserved on Empty Reservation List")
    void testCheckReservedEmptyList() {
        assertFalse(user.checkReserved(restaurant));
    }

    @Test
    @DisplayName("Test Get Reservation on Empty Reservation List")
    void testGetReservationEmptyList() {
        assertNull(user.getReservation(0));
    }

    @Test
    @DisplayName("Test Adding Duplicate Reservations")
    void testAddDuplicateReservations() {
        user.addReservation(reservation1);
        user.addReservation(reservation1);

        assertEquals(2, user.getReservations().size());
        assertSame(user.getReservations().get(0), user.getReservations().get(1));
    }

    @Test
    @DisplayName("Test Canceling Duplicate Reservations")
    void testCancelDuplicateReservations() {
        user.addReservation(reservation1);
        user.addReservation(reservation1);

        when(reservation1.isCancelled()).thenReturn(true);
        when(reservation1.getRestaurant()).thenReturn(restaurant);

        assertFalse(user.checkReserved(restaurant));
    }
}
