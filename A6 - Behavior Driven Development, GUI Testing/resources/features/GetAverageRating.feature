Feature: Calculate average rating of a restaurant

  Scenario: Compute average rating from multiple reviews
    Given a user exists
    And another user exists as well
    And a restaurant existing
    When the user adds a review
    And user2 adds a review
    Then the average rating is computed correctly as food 2.5, service 2.5, ambience 2.5 and overall 2.5

  Scenario: User changes review and average changes as well
    Given a user exists
    And a restaurant existing
    When the user adds a review
    And the user adds a new review for the same restaurant
    Then the average rating is computed correctly as food 4, service 4, ambience 4 and overall 4

  Scenario: Compute average rating from one review
    Given a user exists
    And a restaurant existing
    When the user adds a review
    Then the average rating is computed correctly as food 3, service 3, ambience 3 and overall 3