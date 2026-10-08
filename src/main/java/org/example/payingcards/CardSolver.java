package org.example.payingcards;

import java.util.ArrayList;
import java.util.List;

/** Searches all pair combinations and grouping orders using the four cards. */
public final class CardSolver {
    private CardSolver() { }
    private record Term(double value, String expression) { }

    public static String solve(List<Integer> cards) {
        if (cards.size() != 4) throw new IllegalArgumentException("Four cards are required.");
        List<Term> terms = new ArrayList<>();
        for (int card : cards) terms.add(new Term(card, Integer.toString(card)));
        return search(terms);
    }

    private static String search(List<Term> terms) {
        if (terms.size() == 1) {
            return Math.abs(terms.get(0).value() - 24.0) < 0.0000001
                    ? terms.get(0).expression() : null;
        }
        for (int i = 0; i < terms.size(); i++) {
            for (int j = i + 1; j < terms.size(); j++) {
                Term a = terms.get(i), b = terms.get(j);
                List<Term> rest = new ArrayList<>();
                for (int k = 0; k < terms.size(); k++) {
                    if (k != i && k != j) rest.add(terms.get(k));
                }
                List<Term> combinations = new ArrayList<>();
                combinations.add(combine(a, b, '+', a.value() + b.value()));
                combinations.add(combine(a, b, '*', a.value() * b.value()));
                combinations.add(combine(a, b, '-', a.value() - b.value()));
                combinations.add(combine(b, a, '-', b.value() - a.value()));
                if (b.value() != 0.0) combinations.add(combine(a, b, '/', a.value() / b.value()));
                if (a.value() != 0.0) combinations.add(combine(b, a, '/', b.value() / a.value()));
                for (Term term : combinations) {
                    rest.add(term);
                    String solution = search(rest);
                    rest.remove(rest.size() - 1);
                    if (solution != null) return solution;
                }
            }
        }
        return null;
    }

    private static Term combine(Term a, Term b, char operator, double value) {
        return new Term(value, "(" + a.expression() + operator + b.expression() + ")");
    }
}
