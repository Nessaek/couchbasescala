@Test
Feature: Customer activities can be capture and modified by api

  @scenario
  Scenario: Customer can add, get and delete a valid activity
    Given that the customer is logged in to the app
    And the customer posts a suggestion
    Then the application should return 200
    And the application should display the new suggestion for that customer

    When the customer provides incorrect credentials
    And the customer posts a suggestion
    Then the application should send 401

    When the customer posts a suggestion with malformed json
    Then the application should send back 400
##    And the application should include the new suggestion
##
##  When the customer updates the added suggestion
##  Then the API should return 201
##
##
##
##   When the customer removes the added suggestion
##    Then the API should return 204
##
##       When the customer retrieves their suggestions list
##    Then the API should return 200
##    And the list will be empty
#
#


#    And the activity has the added series

#    When the user removes the added activity
#    Then the API should return 204
#
#    When the customer retrieves their activity
#    Then the API should return 200
