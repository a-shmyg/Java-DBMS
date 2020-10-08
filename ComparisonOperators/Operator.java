package ComparisonOperators;

import DataStructure.InterpreterException;

import java.io.Serializable;

public abstract class Operator implements Serializable {
    public abstract boolean performComparison(String value1, String value2) throws InterpreterException;
}
