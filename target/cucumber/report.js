$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("features/CouchFeature.feature");
formatter.feature({
  "line": 2,
  "name": "User can create, read, update and delete an item from a couchbase database",
  "description": "",
  "id": "user-can-create,-read,-update-and-delete-an-item-from-a-couchbase-database",
  "keyword": "Feature",
  "tags": [
    {
      "line": 1,
      "name": "@Test"
    }
  ]
});
formatter.scenario({
  "line": 4,
  "name": "User can add, get and delete a valid activity",
  "description": "",
  "id": "user-can-create,-read,-update-and-delete-an-item-from-a-couchbase-database;user-can-add,-get-and-delete-a-valid-activity",
  "type": "scenario",
  "keyword": "Scenario",
  "tags": [
    {
      "line": 3,
      "name": "@scenario"
    }
  ]
});
formatter.step({
  "line": 5,
  "name": "the server is running",
  "keyword": "Given "
});
formatter.step({
  "line": 6,
  "name": "a user provides a user ID token",
  "keyword": "And "
});
formatter.step({
  "line": 7,
  "name": "the customer adds an activity to the planner",
  "keyword": "When "
});
formatter.step({
  "line": 8,
  "name": "the API should return 200",
  "keyword": "Then "
});
formatter.match({});
formatter.result({
  "status": "undefined"
});
formatter.match({});
formatter.result({
  "status": "undefined"
});
formatter.match({});
formatter.result({
  "status": "undefined"
});
formatter.match({});
formatter.result({
  "status": "undefined"
});
});