package mizdooni.steps;

import io.cucumber.java.en.*;
import static org.junit.Assert.*;
import mizdooni.model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Steps {
    private User user;
    private User manager;
    private Reservation reservation;
    private Restaurant restaurant;
    private Table table;
    private  Review review_1;
    private Review review_2;
    private int initialCount;

    @Given("a user exists")
    public void a_user_exists_with_a_restaurant_existing() {
        user = new User("Mahdi","1234", "mahdi@gmail.com",
        new Address("Iran", "Tehran", "Vanak"), User.Role.client);
    }

    @Given("a restaurant existing")
    public void a_restaurant_existing(){
        manager = new User("shahriar", "1234", "shahriar@gmail.com",
                new Address("Iran", "Tehran", "Sepah"), User.Role.manager);
        restaurant = new Restaurant("Italian Restaurant", manager, "Italian", LocalTime.now(), LocalTime.now().plusHours(2),
                "desc", new Address("Iran", "Tehran", "Vanak"),
                "link");
    }

    @And("the user has no existing reservations")
    public void the_user_has_no_existing_reservations() {
        initialCount = user.getReservations().size();
    }

    @When("the user adds a reservation to the restaurant")
    public void the_user_adds_a_reservation_to_the_restaurant() {
        reservation = new Reservation(user, restaurant, new Table(1,1,3),
                LocalDateTime.now().plusHours(1));
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

    @Then("the size of the reservation list of user should be 2")
    public void the_size_of_the_reservation_list_of_user_should_be_2(){
        assertEquals(user.getReservations().size(), 2);
    }

    @When("the user adds a review")
    public void the_user_adds_a_review(){
        review_1 = new Review(user, new Rating(3,3,3,3), "good", LocalDateTime.now());
        restaurant.addReview(review_1);
    }

    @And("the user adds a new review for the same restaurant")
    public void the_user_adds_a_new_review_for_the_same_restaurant(){
        review_2 = new Review(user, new Rating(4,4,4,4), "better", LocalDateTime.now());
        restaurant.addReview(review_2);
    }

    @Then("the old review should be replaced with the new review")
    public void the_old_review_should_be_replaced_with_the_new_review(){
        assertEquals(review_2, restaurant.getReviews().getFirst());
    }

    @And("the review list of restaurant should be 1")
    public void the_review_list_of_restaurant_should_be_1(){
        assertEquals(1, restaurant.getReviews().size());
    }
}
