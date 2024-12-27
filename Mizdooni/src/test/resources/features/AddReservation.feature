Feature: Add reservation to user profile

  Scenario: Add a new reservation
    Given a user exists with a restaurant existing
    And the user has no existing reservations
    When I add a new reservation to the user's profile
    Then the reservation should be added successfully
    And the reservation count should increase by 1
