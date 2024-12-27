package mizdooni.steps;

import io.cucumber.java.en.*;
import static org.junit.Assert.*;
import mizdooni.model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class AddReservationSteps {
    private User user;
    private User manager;
    private Reservation reservation;
    private Restaurant restaurant;
    private Table table;
    private int initialCount;

    @Given("a user exists with a restaurant existing")
    public void a_user_exists_with_a_restaurant_existing() {
        user = new User("Mahdi","1234", "mahdi@gmail.com",
        new Address("Iran", "Tehran", "Vanak"), User.Role.client); // Assuming User constructor is available
        manager = new User("shahriar", "1234", "shahriar@gmail.com",
                new Address("Iran", "Tehran", "Sepah"), User.Role.manager);
        restaurant = new Restaurant("Italian Restaurant", manager, "Italian", LocalTime.now(), LocalTime.now().plusHours(2),
                "desc", new Address("Iran", "Tehran", "Vanak"),
                "link");
    }

    @And("the user has no existing reservations")
    public void the_user_has_no_existing_reservations() {
        initialCount = user.getReservations().size(); // Assuming getter for reservations
    }

    @When("I add a new reservation to the user's profile")
    public void i_add_a_new_reservation_to_the_user_s_profile() {
        reservation = new Reservation(user, restaurant, new Table(1,1,3),
                LocalDateTime.now().plusHours(1)); // Assuming Reservation constructor and setters as needed
        user.addReservation(reservation);
    }

    @Then("the reservation should be added successfully")
    public void the_reservation_should_be_added_successfully() {
        assertTrue(user.getReservations().contains(reservation));
    }

    @And("the reservation count should increase by 1")
    public void the_reservation_count_should_increase_by_1() {
        assertEquals(initialCount + 1, user.getReservations().size());
    }
}
