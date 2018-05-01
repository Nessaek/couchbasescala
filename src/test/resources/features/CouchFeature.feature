@Test
Feature: Customer activities can be capture and modified by api

  @scenario
  Scenario: Customer can add, get and delete a valid activity
    Given that the customer is logged in to the app
    And the customer posts a suggestion
    Then the suggestion capture application should return 200
    And the application should display the new suggestion for that customer
    And the application should include the new suggestion

#    And the activity has the added series

#    When the user removes the added activity
#    Then the API should return 204
#
#    When the customer retrieves their activity
#    Then the API should return 200
