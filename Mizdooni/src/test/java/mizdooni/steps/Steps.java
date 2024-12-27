package mizdooni.steps;

import io.cucumber.java.en.*;
import static org.junit.Assert.*;
import mizdooni.model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Steps {
    private User user;
    private User user2;
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

    @Then("the review list of restaurant should be {int}")
    public void the_review_list_of_restaurant_should_be(int num){
        assertEquals(num, restaurant.getReviews().size());
    }

    @Then("the review is in the restaurant review list")
    public void the_review_is_in_the_restaurant_review_list(){
        assertEquals(review_1, restaurant.getReviews().getFirst());
    }

    @Given("another user exists as well")
    public void another_user_exists_as_well(){
        user2 = new User("alireza", "1234", "alireza@gmail.com",
                new Address("Iran", "Tehran", "Marzdarana"), User.Role.client);
    }

    @And("user2 adds a review")
    public void user2_adds_a_review(){
        restaurant.addReview(new Review(user2, new Rating(2,2,2,2), "bad",
                LocalDateTime.now()));
    }

    @Then("the average rating is computed correctly as food {double}, service {double}, ambience {double} and overall {double}")
    public void the_average_rating_is_computed_correctly(double food, double service, double ambience, double overall){
        Rating ratings = restaurant.getAverageRating();
        double delta = 0.01;
        assertEquals(food, ratings.food, delta);
        assertEquals(service, ratings.service, delta);
        assertEquals(ambience, ratings.overall, delta);
        assertEquals(overall, ratings.ambiance, delta);
    }
}
