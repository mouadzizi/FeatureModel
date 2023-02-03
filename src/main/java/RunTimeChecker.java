import java.util.Stack;

public class RunTimeChecker {
    private static final String OR = "OR";
    private static final String AND = "AND";
    private static final String NOT = "NOT";
    private static final String IMPLIES = "->";

    public static boolean evaluateExpression(String expression) {
        Stack<Boolean> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        expression = expression.replaceAll("\\s", "");
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                operators.push(String.valueOf(c));
            } else if (c == ')') {
                while (!operators.peek().equals("(")) {
                    if(operators.peek().equals("NOT")) {
                        values.push(applyOperator(operators.pop(), false, values.pop()));
                    } else
                        values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            } else if (c == '-' && i + 1 < expression.length() && expression.charAt(i + 1) == '>') {
                operators.push("->");
                i++;
            } else if (c == 'A' && i + 2 < expression.length() && expression.substring(i, i + 3).equals("AND")) {
                operators.push("AND");
                i += 2;
            } else if (c == 'O' && i + 1 < expression.length() && expression.substring(i, i + 2).equals("OR")) {
                operators.push("OR");
                i++;
            } else if (c == 'N' && i + 2 < expression.length() && expression.substring(i, i + 3).equals("NOT")) {
                operators.push("NOT");
                i += 2;
            } else if (c == 'T' && i + 3 < expression.length() && expression.substring(i, i + 4).equals("True")) {
                values.push(true);
                i += 3;
            } else if (c == 'F' && i + 4 < expression.length() && expression.substring(i, i + 5).equals("False")) {
                values.push(false);
                i += 4;
            }
        }

        while (!operators.empty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private static boolean applyOperator(String operator, boolean b1, boolean b2) {
        switch (operator) {
            case "->":
                return !b2 || b1;
            case "AND":
                return b1 && b2;
            case "OR":
                return b1 || b2;
            case "NOT":
                return !b2;
        }
        return false;
    }

}
