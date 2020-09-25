Feature: Able to interact with legumes

  Scenario: Get legumes
    Given I use Chrome browser
    When I get legumes
    Then I should have at least 2 total
    And the names are:
      |Carrot  |
      |Zucchini|
