package ComparisonOperators;

import java.io.Serializable;

public class Equals extends Operator implements Serializable {

    public boolean performComparison(String value1, String value2) {
        try {
            if (Float.parseFloat(value1) == Float.parseFloat(value2)) {
                return true;
            }
            return false;
        } catch (NumberFormatException err) {
            System.out.println("not a number");
            if (value1.equals(value2)) {
                return true;
            }
            return false;
        }
    }
}
