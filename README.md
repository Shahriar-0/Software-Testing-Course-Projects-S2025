# Software-Testing-Course-S2025

## Introduction

The course projects focus on writing tests for Mizdooni, an reservation system and Fasedyab, a transaction engine written using Spring Boot with a RESTful API.
Each computer assignment (CA) includes writing different tests for Mizdooni and Fasedyab, and also some explanatory questions about software testing topics which are answered in the corresponding report file.
A README file to how to use Mizdooni has been added.

## Assignments

### CA1 (JUnit)

Unit tests were written for the `User`, `Table`, `Rating`, and `Restaurant` models.
The 3 unit test files can be found in `Mizdooni/src/test/java/model`.
Some tests make use of parameterized testing.
The explanatory questions are about private method testing, multi-threaded testing, and finding a sample test code's problems.

### CA2 (Mock Testing)

Tests were written for the `ReservationController`, `ReviewController`, and `AuthenticationController` classes.
The 3 controller test files can be found in `Mizdooni/src/test/java/controllers`.
The tests mock the Mizdooni service class using the **Mockito** framework.
The explanatory questions are about dependency injection, test double types, and classical and mockist testing strategies.

### CA3 (Graph Coverage)

Tests were written for the `TransactionEngine` and `Transaction` classes.
The test files can be found in `Fasedyab/src/test/java/domain`.
The tests try to maximize branch and statement coverage which is calculated using the **JaCoCo** library.
The explanatory questions are about the possibility of 100% branch or statement coverage, drawing the control flow graph of a code, and graph coverage prime and DU paths.

### CA4 (API Testing)

Tests were written for the `TableController` and `RestaurantController` classes.
The 2 test files which end in `ApiTest` can be found in `Mizdooni/src/test/java/controllers`.
The tests use the `@SpringBootTest` annotation in conjunction with **MockMvc** which performs API calls to an instance of the application and validates the JSON response.
The explanatory questions are about logic coverage and input space partitioning.

### CA5 (Mutation Testing)

Using the tests from CA3 (*Fasedyab*), mutation coverage was calculated using the **PITest** library.
Mutation testing is used to check the quality of tests and how much they can detect faults.
The coverage results are analyzed in the report in which we see that one mutant cannot be killed.
A GitHub Actions workflow is also created (`.github/workflows/maven.yml`). The pipeline builds and runs the tests of Mizdooni and Fasedyab projects after every push.

### CA6 (Behavior Driven Development)

Behavior-driven tests are written for `AddReservation`, `AddReview`, and `GetAverageRating` methods of the `User` and `Restaurant` model.
The test scenarios are in `Mizdooni/src/test/resources` and the implementation can be found in `Mizdooni/src/test/java/mizdooni/CucumberTest.java`.
The scenarios are written using the **Cucumber** tool's Gherkin language.
Recorded GUI testing is also performed on **Swagger UI**'s visualization of Mizdooni's API using **Katalon Recorder**.
