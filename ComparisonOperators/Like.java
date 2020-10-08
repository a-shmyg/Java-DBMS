package ComparisonOperators;

import DataStructure.InterpreterException;

import java.io.Serializable;

public class Like extends Operator implements Serializable {
    @Override
    public boolean performComparison(String value1, String value2) throws InterpreterException {
        if (!checkStringLiteral(value1) || !checkStringLiteral(value2)) {
            throw new InterpreterException("expecting strings");
        }

        if (compareSubstrings(value1, value2.substring(1,value2.length()-1))) {
            return true;
        }
        return false;

    }

    public boolean compareSubstrings(String value1, String value2) {
        if (value1.contains(value2)) {
            return true;
        }
        return false;
    }

    public boolean checkStringLiteral(String value) {
        if (value.startsWith("'")) { //lexer checks for missing quotes on string literals so no need to check endsWith as well
            return true;
        }

        return false;
    }

}
