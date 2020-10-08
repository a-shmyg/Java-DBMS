package ComparisonOperators;

import java.io.Serializable;

public class NotEqual extends Operator implements Serializable {
    @Override
    public boolean performComparison(String value1, String value2) {
        try {
            if (Float.parseFloat(value1) != Float.parseFloat(value2)) {
                return true;
            }
            return false;
        } catch (NumberFormatException err) {
            System.out.println("not a number");
            if (!value1.equals(value2)) {
                return true;
            }
            return false;
        }
    }
}
