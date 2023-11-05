# Cart App

## Features

### Step 1: Shopping cart

- A stateful cart/checkout system for a shop which only sells apples and oranges.
- Apples cost 60p and oranges cost 25p.
- Build a checkout system which takes a list of items scanned at the till and outputs the total cost
  - For example: [ Apple, Apple, Orange, Apple ] => £2.05
- Make reasonable assumptions about the inputs to your solution; for example, many candidates take a list of strings as input

### Step 2: Simple offers
 
- The shop decides to introduce two new offers
  - buy one, get one free on Apples
  - 3 for the price of 2 on Oranges
- Update the checkout functions accordingly

## Various commands

- `sbt compile`: Compile the project
- `sbt run`: Run the project
- `sbt test`: Run unit tests
- `sbt dist`: Create a .zip distribution
- `sbt Docker/publishLocal`: Dockerize the app. Note that a Docker daemon needs to be running in order for this to work.
- `docker run hello:0.1.0-SNAPSHOT`: Run the Dockerized app.

## ReST interface

```shell
curl -X GET http://localhost:8080
```

Send POST requests to http://localhost:8080/v0/checkout with a JSON body containing a list of scanned items (e.g., ["Apple", "Apple", "Orange", "Apple"]).
You will receive the total cost as a response.

```shell
curl -X POST -H "Content-Type: application/json" -d "[\"Apple\", \"Apple\", \"Orange\", \"Apple\"]" http://localhost:8080/v0/checkout
```

## Github action workflow

Generate workflow by adding `sbt-github-actions` plug-in and then running:

```shell
sbt githubWorkflowGenerate
```
