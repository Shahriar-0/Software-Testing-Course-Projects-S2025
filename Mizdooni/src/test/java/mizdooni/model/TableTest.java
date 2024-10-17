package mizdooni.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class TableTest {

    private Table table;
    private Restaurant restaurant;
    private Reservation reservation1;
    private Reservation reservation2;

    @BeforeEach
    void setUp() {
        restaurant = mock(Restaurant.class);
        reservation1 = mock(Reservation.class);
        reservation2 = mock(Reservation.class);
        table = new Table(1, 123, 4);
    }

    @Test
    @DisplayName("Test Add Reservation to Table")
    void testAddReservation() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        when(reservation1.getDateTime()).thenReturn(futureDate);
        when(reservation1.isCancelled()).thenReturn(false);

        table.addReservation(reservation1);

        List<Reservation> reservations = table.getReservations();
        assertEquals(1, reservations.size());
        assertEquals(reservation1, reservations.get(0));
    }

    @ParameterizedTest
    @CsvSource({
        "2024-10-20T10:00:00, false",  // No reservation added, expect false
        "2020-10-20T10:00:00, true"    // Reservation added for this time, expect true
    })
    @DisplayName("Test Table Reservation Status at Specific Time")
    void testIsReservedAtSpecificTime(String dateTime, boolean expectedIsReserved) {
        LocalDateTime testDateTime = LocalDateTime.parse(dateTime);

        if (expectedIsReserved) {
            when(reservation1.getDateTime()).thenReturn(testDateTime);
            when(reservation1.isCancelled()).thenReturn(false);
            table.addReservation(reservation1);
        }

        assertEquals(expectedIsReserved, table.isReserved(testDateTime));
    }

    @Test
    @DisplayName("Test Multiple Reservations and Cancellation")
    void testMultipleReservationsAndCancellation() {
        LocalDateTime reservationTime1 = LocalDateTime.now().plusDays(1);
        LocalDateTime reservationTime2 = LocalDateTime.now().plusDays(2);

        when(reservation1.getDateTime()).thenReturn(reservationTime1);
        when(reservation2.getDateTime()).thenReturn(reservationTime2);
        when(reservation1.isCancelled()).thenReturn(false);
        when(reservation2.isCancelled()).thenReturn(false);

        table.addReservation(reservation1);
        table.addReservation(reservation2);

        assertTrue(table.isReserved(reservationTime1));
        assertTrue(table.isReserved(reservationTime2));

        when(reservation1.isCancelled()).thenReturn(true);

        assertFalse(table.isReserved(reservationTime1)); // Should return false because reservation1 is cancelled
        assertTrue(table.isReserved(reservationTime2));  // Should still be reserved
    }

    @Test
    @DisplayName("Test Reservation at Specific Time")
    void testReservationAtSpecificTime() {
        LocalDateTime reservationTime = LocalDateTime.now().plusDays(1);

        when(reservation1.getDateTime()).thenReturn(reservationTime);
        when(reservation1.isCancelled()).thenReturn(false);

        table.addReservation(reservation1);

        assertTrue(table.isReserved(reservationTime));
    }

    @Test
    @DisplayName("Test Reservation Cancellation")
    void testReservationCancellation() {
        LocalDateTime reservationTime = LocalDateTime.now().plusDays(1);

        when(reservation1.getDateTime()).thenReturn(reservationTime);
        when(reservation1.isCancelled()).thenReturn(false);

        table.addReservation(reservation1);

        assertTrue(table.isReserved(reservationTime));

        // Simulate cancellation
        when(reservation1.isCancelled()).thenReturn(true);

        assertFalse(table.isReserved(reservationTime));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5})
    @DisplayName("Test Setting Table Number")
    void testSetTableNumber(int tableNumber) {
        table.setTableNumber(tableNumber);
        assertEquals(tableNumber, table.getTableNumber());
    }

    @Test
    @DisplayName("Test Null Date Handling in isReserved")
    void testIsReservedWithNullDate() {
        assertThrows(NullPointerException.class, () -> {
            table.isReserved(null);
        });
    }

    @Test
    @DisplayName("Edge Case: Test Reservation at Midnight")
    void testReservationAtMidnight() {
        LocalDateTime midnight = LocalDateTime.of(2024, 10, 20, 0, 0);
        when(reservation1.getDateTime()).thenReturn(midnight);
        when(reservation1.isCancelled()).thenReturn(false);

        table.addReservation(reservation1);

        assertTrue(table.isReserved(midnight));
    }

    @Test
    @DisplayName("Test Multiple Reservations at Same Time")
    void testMultipleReservationsAtSameTime() {
        LocalDateTime reservationTime = LocalDateTime.now().plusDays(1);

        when(reservation1.getDateTime()).thenReturn(reservationTime);
        when(reservation2.getDateTime()).thenReturn(reservationTime);
        when(reservation1.isCancelled()).thenReturn(false);
        when(reservation2.isCancelled()).thenReturn(false);

        table.addReservation(reservation1);
        table.addReservation(reservation2);

        assertTrue(table.isReserved(reservationTime));
        assertEquals(2, table.getReservations().size());

        // Cancel one reservation, check the other is still active
        when(reservation1.isCancelled()).thenReturn(true);
        assertTrue(table.isReserved(reservationTime));
        assertEquals(1, table.getReservations().stream().filter(r -> !r.isCancelled()).count());

        when(reservation2.isCancelled()).thenReturn(true);
        assertFalse(table.isReserved(reservationTime));
        assertEquals(0, table.getReservations().stream().filter(r -> !r.isCancelled()).count());
    }
}
