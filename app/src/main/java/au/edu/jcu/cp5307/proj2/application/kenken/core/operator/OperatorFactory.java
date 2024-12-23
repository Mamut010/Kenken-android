package au.edu.jcu.cp5307.proj2.application.kenken.core.operator;

import java.util.UnknownFormatConversionException;

public final class OperatorFactory {
    private OperatorFactory() {}
    
    public static Operator createOperator(String opStr) throws UnknownFormatConversionException {
        opStr = opStr.trim().toLowerCase();
        return switch(opStr) {
            case "+" -> new Addition();
            case "-" -> new Subtraction();
            case "*", "×", "x" -> new Multiplication();
            case "/", "÷" -> new Division();
            case "=" -> new Equality();
            default -> throw new UnknownFormatConversionException(opStr);
        };
    }
}
