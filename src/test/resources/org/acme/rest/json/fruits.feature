Feature: Able to interact with fruits

  Scenario: Get fruits
    Given I use Chrome browser
    When I get fruits
    Then I should have at least 3 total
    And the names are:
    |Banana   |
    |Pineapple|
    |Apple    |

  Scenario: Create fruit
    Given I use Chrome browser
    When I add to fruits with name Strawberry and description Lovely fruit
    Then it should be created
