Feature: Add a review to a restaurant

  Scenario: Add multiple reviews to a restaurant by the same user replacing the old ones
    Given a user exists
    And a restaurant existing
    When the user adds a review
    And the user adds a new review for the same restaurant
    Then the old review should be replaced with the new review
    And the review list of restaurant should be 1
