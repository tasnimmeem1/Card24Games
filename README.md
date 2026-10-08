# Card 24 Game

A JavaFX game where the player uses four randomly selected card values exactly once to create an expression that equals 24.

## Rules

- Number cards 2–10 use their face values.
- Ace = 1, Jack = 11, Queen = 12, King = 13.
- Use all four card values exactly once.
- Allowed operations: +, -, *, and /.
- Parentheses can be used to group calculations.

## Features

- Every displayed set has at least one solution.
- Verify checks the card values and whether the result equals 24.
- Invalid expressions and results are shown in dialogs.
- Refresh generates new cards and clears the expression.
- Solve fills the expression field with a valid solution.
- Background changes the background picture.
- CSS provides styling and LED effects.

## Technologies

- Java 25
- JavaFX 21.0.6
- Maven
- FXML
- CSS

## How to Run

1. Install JDK 25.
2. Download or clone this repository.
3. Open pom.xml as a project in IntelliJ IDEA.
4. Allow Maven to load the dependencies.
5. Run CardApplication.

You can also run from the project folder:

Windows:
    .\mvnw.cmd javafx:run

macOS or Linux:
    chmod +x mvnw
    ./mvnw javafx:run

## Author

Shahla T. Meem
