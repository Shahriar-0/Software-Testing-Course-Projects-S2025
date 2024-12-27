Feature: Add a review to a restaurant

  Scenario: Add a new review by a user
    Given a user has previously reviewed a restaurant
    When the user adds a new review for the same restaurant
    Then the old review should be replaced with the new review
