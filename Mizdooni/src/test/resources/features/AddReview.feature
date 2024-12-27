Feature: Add a review to a restaurant

  Scenario: Add multiple reviews to a restaurant by the same user replacing the old ones
    Given a user exists
    And a restaurant existing
    When the user adds a review
    And the user adds a new review for the same restaurant
    Then the old review should be replaced with the new review
    Then the review list of restaurant should be 1

  Scenario: Adding a review to a restaurant
    Given a user exists
    And a restaurant existing
    When the user adds a review
    Then the review is in the restaurant review list

  Scenario: Adding review by different users
    Given a user exists
    And another user exists as well
    And a restaurant existing
    When the user adds a review
    And user2 adds a review
    Then the review list of restaurant should be 2