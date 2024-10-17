package mizdooni.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class TableTest {

    private Table table;
    private User user;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        Address address = new Address("Country", "City", "123 Main St");
        user =
            new User(
                "mahdies",
                "password123",
                "mahdi@example.com",
                address,
                User.Role.client
            );
        restaurant =
            new Restaurant(
                "Test Restaurant",
                user,
                "Italian",
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                "Nice place",
                address,
                "image_link"
            );
        table = new Table(1, restaurant.getId(), 4);
    }

    @Test
    @DisplayName("Test Add Reservation to Table")
    void testAddReservation() {
        Reservation reservation = new Reservation(
            user,
            restaurant,
            table,
            LocalDateTime.now().plusDays(1)
        );
        table.addReservation(reservation);

        List<Reservation> reservations = table.getReservations();
        assertEquals(1, reservations.size());
        assertEquals(reservation, reservations.get(0));
    }

    @ParameterizedTest
    @CsvSource(
        {
            "2024-10-20T10:00:00, false", // No reservation added, expect false
            "2020-10-20T10:00:00, true" // Add reservation at this date, expect true
        }
    )
    @DisplayName("Test Table Reservation Status at Specific Time")
    void testIsReservedAtSpecificTime(String dateTime, boolean expectedIsReserved) {
        LocalDateTime testDateTime = LocalDateTime.parse(dateTime);

        if (expectedIsReserved) {
            Reservation reservation = new Reservation(
                user,
                restaurant,
                table,
                testDateTime
            );
            table.addReservation(reservation);
        }

        assertEquals(expectedIsReserved, table.isReserved(testDateTime));
    }

    @ParameterizedTest
    @CsvSource(
        {
            "2023-10-10T10:00:00, false", // Past reservation should not affect current status
            "2030-12-31T23:59:00, true" // Very far future reservation, should return true
        }
    )
    @DisplayName("Test Past and Future Reservations")
    void testPastAndFutureReservations(String dateTime, boolean expectedIsReserved) {
        LocalDateTime reservationTime = LocalDateTime.parse(dateTime);

        if (expectedIsReserved) {
            Reservation reservation = new Reservation(
                user,
                restaurant,
                table,
                reservationTime
            );
            table.addReservation(reservation);
        }

        assertEquals(expectedIsReserved, table.isReserved(reservationTime));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 2, 5 })
    @DisplayName("Test Setting Table Number")
    void testSetTableNumber(int tableNumber) {
        table.setTableNumber(tableNumber);
        assertEquals(tableNumber, table.getTableNumber());
    }

    @Test
    @DisplayName("Test Multiple Reservations and Cancellation")
    void testMultipleReservationsAndCancellation() {
        LocalDateTime reservationTime1 = LocalDateTime.now().plusDays(1);
        LocalDateTime reservationTime2 = LocalDateTime.now().plusDays(2);

        Reservation reservation1 = new Reservation(
            user,
            restaurant,
            table,
            reservationTime1
        );
        Reservation reservation2 = new Reservation(
            user,
            restaurant,
            table,
            reservationTime2
        );

        table.addReservation(reservation1);
        table.addReservation(reservation2);

        assertTrue(table.isReserved(reservationTime1));
        assertTrue(table.isReserved(reservationTime2));

        reservation1.cancel();
        assertFalse(table.isReserved(reservationTime1));
        assertTrue(table.isReserved(reservationTime2));
    }

    @Test
    @DisplayName("Test Multiple Reservations at Same Time")
    void testMultipleReservationsAtSameTime() {
        LocalDateTime reservationTime = LocalDateTime.now().plusDays(1);
        Reservation reservation1 = new Reservation(
            user,
            restaurant,
            table,
            reservationTime
        );
        Reservation reservation2 = new Reservation(
            user,
            restaurant,
            table,
            reservationTime
        );

        table.addReservation(reservation1);
        table.addReservation(reservation2);

        assertTrue(table.isReserved(reservationTime));
        assertEquals(2, table.getReservations().size());

        reservation1.cancel();
        assertTrue(table.isReserved(reservationTime));
        assertEquals(1, table.getReservations().stream().filter(r -> !r.isCancelled()).count());

        reservation2.cancel();
        assertFalse(table.isReserved(reservationTime));
        assertEquals(0, table.getReservations().stream().filter(r -> !r.isCancelled()).count());
    }

    @Test
    @DisplayName("Test Reservation Cancellation on Table")
    void testReservationCancellation() {
        LocalDateTime reservationTime = LocalDateTime.now().plusDays(1);
        Reservation reservation = new Reservation(
            user,
            restaurant,
            table,
            reservationTime
        );
        table.addReservation(reservation);

        assertTrue(table.isReserved(reservationTime));
        reservation.cancel();
        assertFalse(table.isReserved(reservationTime));

        List<Reservation> reservations = table.getReservations();
        List<Reservation> notCancelledReservations = reservations.stream().filter(r -> !r.isCancelled()).toList();
        assertEquals(0, notCancelledReservations.size());
    }

    @Test
    @DisplayName("Test Null Date Handling in isReserved")
    void testIsReservedWithNullDate() {
        assertThrows(
            NullPointerException.class,
            () -> {
                table.isReserved(null);
            }
        );
    }

    @Test
    @DisplayName("edge case: Test Reservation at Midnight")
    void testReservationAtMidnight() {
        LocalDateTime midnight = LocalDateTime.of(2024, 10, 20, 0, 0);
        Reservation reservation = new Reservation(user, restaurant, table, midnight);
        table.addReservation(reservation);

        assertTrue(table.isReserved(midnight));
    }
}
