package mizdooni.controllers;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mizdooni.exceptions.ReservationCannotBeCancelled;
import mizdooni.exceptions.ReservationNotFound;
import mizdooni.exceptions.UserNotFound;
import mizdooni.model.Address;
import mizdooni.model.Reservation;
import mizdooni.model.Restaurant;
import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.ReservationService;
import mizdooni.service.RestaurantService;
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
public class ReservationControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private Restaurant mockRestaurant;
    private Reservation mockReservation;
    private Reservation mockReservation2;
    private User manager;
    private User client;
    private Address address;

    private static final LocalDate VALID_DATE = LocalDate.of(2024, 10, 25);
    private static final LocalDateTime VALID_DATETIME = LocalDateTime.of(
        2024,
        10,
        25,
        19,
        0
    );
    private static final String VALID_DATETIME_STRING = "2024-10-25 19:00";
    private static final LocalDate VALID_DATE_2 = LocalDate.of(2024, 10, 26);
    private static final LocalDateTime VALID_DATETIME_2 = LocalDateTime.of(
        2024,
        10,
        26,
        19,
        0
    );
    private static final String VALID_DATETIME_2_STRING = "2024-10-26 19:00";

    @Before
    public void setUp() {
        manager =
            new User(
                "manager",
                "pass",
                "email@example.com",
                new Address("Country", "City", "Street"),
                User.Role.manager
            );
        client =
            new User(
                "client",
                "pass",
                "email@example.com",
                new Address("Country", "City", "Street"),
                User.Role.client
            );
        address = new Address("Country", "City", "Street");
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
        mockReservation = new Reservation(client, mockRestaurant, null, VALID_DATETIME);
        mockReservation2 =
            new Reservation(client, mockRestaurant, null, VALID_DATETIME_2);
    }

    @Test
    @DisplayName("Test Get Reservations Success with Date Provided")
    public void testGetReservationsSuccessWithDate() throws Exception {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        List<Reservation> reservations = Collections.singletonList(mockReservation);
        when(reservationService.getReservations(1, 2, VALID_DATE))
            .thenReturn(reservations);

        Response response = reservationController.getReservations(
            1,
            2,
            VALID_DATE.toString()
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, ControllersTestUtils.getField(response, "status"));
        assertTrue((Boolean) ControllersTestUtils.getField(response, "success"));
        assertEquals(
            "restaurant table reservations",
            ControllersTestUtils.getField(response, "message")
        );
        assertEquals(reservations, ControllersTestUtils.getField(response, "data"));
    }

    @Test
    @DisplayName(
        "Test Get Reservations Success with Date Provided with Multiple Reservations"
    )
    public void testGetReservationsSuccessWithDateWithMultipleReservations()
        throws Exception {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        List<Reservation> reservations = List.of(mockReservation, mockReservation2);
        when(reservationService.getReservations(1, 2, VALID_DATE))
            .thenReturn(Collections.singletonList(reservations.get(0)));

        Response response = reservationController.getReservations(
            1,
            2,
            VALID_DATE.toString()
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, ControllersTestUtils.getField(response, "status"));
        assertTrue((Boolean) ControllersTestUtils.getField(response, "success"));
        assertEquals(
            "restaurant table reservations",
            ControllersTestUtils.getField(response, "message")
        );
        assertEquals(
            Collections.singletonList(reservations.get(0)),
            ControllersTestUtils.getField(response, "data")
        );
    }

    @Test
    @DisplayName("Test Get Reservations Success without Date")
    public void testGetReservationsSuccessWithoutDate() throws Exception {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        List<Reservation> reservations = List.of(mockReservation, mockReservation2);
        when(reservationService.getReservations(1, 2, null)).thenReturn(reservations);

        Response response = reservationController.getReservations(1, 2, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, ControllersTestUtils.getField(response, "status"));
        assertTrue((Boolean) ControllersTestUtils.getField(response, "success"));
        assertEquals(
            "restaurant table reservations",
            ControllersTestUtils.getField(response, "message")
        );
        assertEquals(reservations, ControllersTestUtils.getField(response, "data"));
    }

    @Test
    @DisplayName("Test Get Reservations Restaurant Not Found")
    public void testGetReservationsRestaurantNotFound() {
        when(restaurantService.getRestaurant(1)).thenReturn(null);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reservationController.getReservations(1, 2, null)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Test Get Reservations Invalid Date Format")
    public void testGetReservationsInvalidDateFormat() {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reservationController.getReservations(1, 2, "invalid-date")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test Get Customer Reservations Success")
    public void testGetCustomerReservationsSuccess() throws Exception {
        List<Reservation> reservations = Collections.singletonList(mockReservation);
        when(reservationService.getCustomerReservations(1)).thenReturn(reservations);

        Response response = reservationController.getCustomerReservations(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, ControllersTestUtils.getField(response, "status"));
        assertTrue((Boolean) ControllersTestUtils.getField(response, "success"));
        assertEquals(
            "user reservations",
            ControllersTestUtils.getField(response, "message")
        );
        assertEquals(reservations, ControllersTestUtils.getField(response, "data"));
    }

    @Test
    @DisplayName("Test Get Customer Reservations User Not Found")
    public void testGetCustomerReservationsUserNotFound() throws Exception {
        doThrow(ResponseException.class)
            .when(reservationService)
            .getCustomerReservations(1);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reservationController.getCustomerReservations(1)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    @DisplayName("Test Get Available Times Success")
    public void testGetAvailableTimesSuccess() throws Exception {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        List<LocalTime> availableTimes = Collections.singletonList(LocalTime.of(19, 0));
        when(reservationService.getAvailableTimes(1, 4, VALID_DATE))
            .thenReturn(availableTimes);

        Response response = reservationController.getAvailableTimes(
            1,
            4,
            VALID_DATE.toString()
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, ControllersTestUtils.getField(response, "status"));
        assertTrue((Boolean) ControllersTestUtils.getField(response, "success"));
        assertEquals(
            "available times",
            ControllersTestUtils.getField(response, "message")
        );
        assertEquals(availableTimes, ControllersTestUtils.getField(response, "data"));
    }

    @Test
    @DisplayName("Test Get Available Times Restaurant Not Found")
    public void testGetAvailableTimesRestaurantNotFound() {
        when(restaurantService.getRestaurant(1)).thenReturn(null);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reservationController.getAvailableTimes(1, 4, VALID_DATE.toString())
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Test Get Available Times Invalid Date Format")
    public void testGetAvailableTimesInvalidDateFormat() {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reservationController.getAvailableTimes(1, 4, "invalid-date")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test Add Reservation Success")
    public void testAddReservationSuccess() throws Exception {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        when(reservationService.reserveTable(1, 4, VALID_DATETIME))
            .thenReturn(mockReservation);

        Map<String, String> params = new HashMap<>();
        params.put("people", "4");
        params.put("datetime", VALID_DATETIME_STRING);

        Response response = reservationController.addReservation(1, params);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, ControllersTestUtils.getField(response, "status"));
        assertTrue((Boolean) ControllersTestUtils.getField(response, "success"));
        assertEquals(
            "reservation done",
            ControllersTestUtils.getField(response, "message")
        );
        assertEquals(mockReservation, ControllersTestUtils.getField(response, "data"));
    }

    @Test
    @DisplayName("Test Add Reservation Missing Parameters")
    public void testAddReservationMissingParameters() {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        Map<String, String> params = new HashMap<>();
        params.put("people", "4"); // Missing "datetime"

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reservationController.addReservation(1, params)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Test Add Reservation Invalid Parameter Type")
    public void testAddReservationInvalidParameterType() {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        Map<String, String> params = new HashMap<>();
        params.put("people", "four"); // Invalid type for people
        params.put("datetime", VALID_DATETIME.toString());

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reservationController.addReservation(1, params)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test Cancel Reservation Success")
    public void testCancelReservationSuccess() throws Exception {
        doNothing().when(reservationService).cancelReservation(1);

        Response response = reservationController.cancelReservation(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, ControllersTestUtils.getField(response, "status"));
        assertTrue((Boolean) ControllersTestUtils.getField(response, "success"));
        assertEquals(
            "reservation cancelled",
            ControllersTestUtils.getField(response, "message")
        );
    }

    @Test
    @DisplayName("Test Cancel Reservation Failure - User Not Found")
    public void testCancelReservationFailureUserNotFound() throws Exception {
        doThrow(new UserNotFound()).when(reservationService).cancelReservation(1);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reservationController.cancelReservation(1)
        );

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Test Cancel Reservation Failure - Reservation Not Found")
    public void testCancelReservationFailureReservationNotFound() throws Exception {
        doThrow(new ReservationNotFound()).when(reservationService).cancelReservation(1);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reservationController.cancelReservation(1)
        );

        assertEquals("Reservation not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Test Cancel Reservation Failure - Cannot Be Cancelled")
    public void testCancelReservationFailureCannotBeCancelled() throws Exception {
        doThrow(new ReservationCannotBeCancelled())
            .when(reservationService)
            .cancelReservation(1);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> reservationController.cancelReservation(1)
        );

        assertEquals("Reservation cannot be cancelled.", exception.getMessage());
    }
}
