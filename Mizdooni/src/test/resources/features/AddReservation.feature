Feature: Add reservation to user

  Scenario: Add a new reservation
    Given a user exists
    And a restaurant existing
    And the user has no existing reservations
    When the user adds a reservation to the restaurant
    Then the reservation should be added successfully
    And the reservation count should increase by 1

  Scenario: Adding multiple reservations to the same restaurant
    Given a user exists
    And a restaurant existing
    When the user adds a reservation to the restaurant
    And the user adds a reservation to the restaurant
    Then the size of the reservation list of user should be 2
