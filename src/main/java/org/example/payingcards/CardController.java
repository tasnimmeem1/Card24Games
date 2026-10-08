package org.example.payingcards;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.scene.layout.VBox;

public class CardController {

    @FXML
    private ImageView card1;

    @FXML
    private ImageView card2;

    @FXML
    private ImageView card3;

    @FXML
    private ImageView card4;

    @FXML
    private TextField expressionField;

    @FXML
    private Label valuesLabel;

    private final List<Integer> cardValues = new ArrayList<>();

    @FXML
    private VBox gameRoot;

    private final String[] backgroundPictures = {
            "png/Ocean.png",
            "png/Sky.png",
            "png/SunRise.png",
            "png/WaterFall.png"
    };

    private int backgroundIndex = -1;
    private String currentSolution;

    @FXML
    public void initialize() {
        refreshCards();
    }

    // Deal four different cards, retrying until the set can make 24.
    @FXML
    private void refreshCards() {
        List<Integer> deck = new ArrayList<>();

        for (int i = 0; i < 52; i++) {
            deck.add(i);
        }

        // Reject unsolvable hands before displaying any cards.
        do {
            Collections.shuffle(deck);
            cardValues.clear();
            for (int i = 0; i < 4; i++) cardValues.add(deck.get(i) % 13 + 1);
            currentSolution = CardSolver.solve(cardValues);
        } while (currentSolution == null);

        ImageView[] views = {card1, card2, card3, card4};
        String[] suits = {"clubs", "diamonds", "hearts", "spades"};

        for (int i = 0; i < views.length; i++) {
            int card = deck.get(i);
            int value = card % 13 + 1;
            String suit = suits[card / 13];

            String rank = switch (value) {
                case 1 -> "ace";
                case 11 -> "jack";
                case 12 -> "queen";
                case 13 -> "king";
                default -> String.valueOf(value);
            };

            String fileName = "png/" + rank + "_of_" + suit + ".png";

            Image image = new Image(
                    Objects.requireNonNull(
                            CardController.class.getResource(fileName),
                            "Missing card image: " + fileName
                    ).toExternalForm()
            );

            views[i].setImage(image);

        }

        valuesLabel.setText("Card values: " + cardValues);
        expressionField.clear();
    }

    // Reveal a solution that uses each displayed card exactly once.
    @FXML
    private void solveCards() {
        if (currentSolution == null) {
            showDialog(Alert.AlertType.WARNING, "No Solution",
                    "No solution exists for these cards. Click Refresh to try new cards.");
            return;
        }
        expressionField.setText(currentSolution);
        showDialog(Alert.AlertType.INFORMATION, "Solution",
                currentSolution + " = 24\nClick Verify to check this solution.");
    }

    // Check both the numbers used and the expression's result.
    @FXML
    private void verifyExpression() {
        String expression = expressionField.getText();

        if (expression == null || expression.isBlank()) {
            showDialog(
                    Alert.AlertType.WARNING,
                    "Missing Expression",
                    "Please enter an expression."
            );
            return;
        }

        try {
            ExpressionParser parser = new ExpressionParser(expression);
            double result = parser.parse();

            // Compare sorted lists to account for repeated card values.
            List<Integer> expected = new ArrayList<>(cardValues);
            List<Integer> used = new ArrayList<>(parser.getNumbers());

            Collections.sort(expected);
            Collections.sort(used);

            if (!expected.equals(used)) {
                showDialog(
                        Alert.AlertType.ERROR,
                        "Incorrect Card Values",
                        "Use each displayed card value exactly once.\n"
                                + "Required values: " + cardValues
                );
                return;
            }

            if (Math.abs(result - 24.0) < 0.0000001) {
                showDialog(
                        Alert.AlertType.INFORMATION,
                        "Correct!",
                        expression + " = 24\nWell done!"
                );
            } else {
                showDialog(
                        Alert.AlertType.WARNING,
                        "Try Again",
                        "Your expression evaluates to " + result
                                + ", which is not 24."
                );
            }
        } catch (IllegalArgumentException exception) {
            showDialog(
                    Alert.AlertType.ERROR,
                    "Invalid Expression",
                    exception.getMessage()
            );
        }
    }
    // Change the background picture without changing the cards.

    @FXML
    private void changeBackground() {
        backgroundIndex++;

        // Return to the original black background after the last picture.
        if (backgroundIndex >= backgroundPictures.length) {
            backgroundIndex = -1;

            gameRoot.setStyle(
                    "-fx-background-image: none;"
                            + "-fx-background-color: #080b12;"
            );
            return;
        }

        String imageUrl = Objects.requireNonNull(
                CardController.class.getResource(
                        backgroundPictures[backgroundIndex]
                ),
                "Background picture not found."
        ).toExternalForm();

        gameRoot.setStyle(
                "-fx-background-image: url('" + imageUrl + "');"
                        + "-fx-background-size: cover;"
                        + "-fx-background-position: center;"
                        + "-fx-background-repeat: no-repeat;"
        );
    }

    private void showDialog(
            Alert.AlertType type, String title, String message) {

        Alert alert = new Alert(type);
        alert.initOwner(expressionField.getScene().getWindow());
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /*
     * Parses card numbers, arithmetic operators, and parentheses.
     * Multiplication and division are evaluated before addition
     * and subtraction.
     */
    private static class ExpressionParser {

        private final String text;
        private final List<Integer> numbers = new ArrayList<>();
        private int position;

        ExpressionParser(String text) {
            this.text = text;
        }

        List<Integer> getNumbers() {
            return numbers;
        }

        double parse() {
            double result = parseExpression();
            skipSpaces();

            if (position != text.length()) {
                throw new IllegalArgumentException(
                        "Unexpected character at position "
                                + (position + 1)
                                + ". Use numbers, +, -, *, /, and parentheses."
                );
            }

            if (!Double.isFinite(result)) {
                throw new IllegalArgumentException(
                        "The expression produced an invalid result."
                );
            }

            return result;
        }

        // Handle addition and subtraction.
        private double parseExpression() {
            double value = parseTerm();

            while (true) {
                if (match('+')) {
                    value += parseTerm();
                } else if (match('-')) {
                    value -= parseTerm();
                } else {
                    return value;
                }
            }
        }

        // Handle multiplication and division.
        private double parseTerm() {
            double value = parseFactor();

            while (true) {
                if (match('*')) {
                    value *= parseFactor();
                } else if (match('/')) {
                    double divisor = parseFactor();

                    if (divisor == 0.0) {
                        throw new IllegalArgumentException(
                                "Division by zero is not allowed."
                        );
                    }

                    value /= divisor;
                } else {
                    return value;
                }
            }
        }

        // Read a card value or an expression inside parentheses.
        private double parseFactor() {
            skipSpaces();

            if (match('(')) {
                double value = parseExpression();

                if (!match(')')) {
                    throw new IllegalArgumentException(
                            "A closing parenthesis is missing."
                    );
                }

                return value;
            }

            int start = position;

            while (position < text.length()
                    && text.charAt(position) >= '0'
                    && text.charAt(position) <= '9') {
                position++;
            }

            if (start == position) {
                throw new IllegalArgumentException(
                        "Expected a card number or '(' at position "
                                + (position + 1) + "."
                );
            }

            String token = text.substring(start, position);

            if (token.length() > 2 || token.charAt(0) == '0') {
                throw new IllegalArgumentException(
                        "Enter card values as whole numbers from 1 to 13."
                );
            }

            int number = Integer.parseInt(token);

            if (number < 1 || number > 13) {
                throw new IllegalArgumentException(
                        "Card values must be between 1 and 13."
                );
            }

            numbers.add(number);
            return number;
        }

        private boolean match(char character) {
            skipSpaces();

            if (position < text.length()
                    && text.charAt(position) == character) {
                position++;
                return true;
            }

            return false;
        }

        private void skipSpaces() {
            while (position < text.length()
                    && Character.isWhitespace(text.charAt(position))) {
                position++;
            }
        }
    }
}