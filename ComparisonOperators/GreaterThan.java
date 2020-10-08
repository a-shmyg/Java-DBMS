package ComparisonOperators;

import DataStructure.InterpreterException;

import java.io.Serializable;

public class GreaterThan extends Operator implements Serializable {

    @Override
    public boolean performComparison(String value1, String value2) throws InterpreterException {
        try {
            if (Float.parseFloat(value1) > Float.parseFloat(value2)) {
                return true;
            }
            return false;
        } catch (NumberFormatException err) {
            throw new InterpreterException("expecting a number for comparison");
        }
    }
}
