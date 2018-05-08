@Test
Feature: Customer activities can be capture and modified by api

  @scenario
  Scenario: Customer can add, get and delete a valid activity
    Given that the customer is logged in to the app
    And the database is empty
    And the customer posts a suggestion
    Then the application should return 200
    And the application should display the new suggestion for that customer
    And the application should have the suggestion available

    When the customer updates the added suggestion
    Then the API should send back 201


    When the customer tries to delete the item
    Then the API should return 204

    When the customer tries to delete an item with an invalid id
    Then the API should send back a 404 response


    When the customer provides incorrect credentials
    And the customer tries to post a suggestion
    Then the application should send 401

   When the customer posts a suggestion with malformed json
   Then the application should send back 400
