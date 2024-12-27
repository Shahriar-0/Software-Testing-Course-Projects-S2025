Feature: Calculate average rating of a restaurant

  Scenario: Compute average rating from multiple reviews
    Given a restaurant has multiple reviews
    When I calculate the average rating
    Then the average should reflect the sum divided by the number of reviews
