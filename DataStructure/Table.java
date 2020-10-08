package DataStructure;

import ComparisonOperators.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Table implements Serializable {
    //contains rows and columns in a 2D array list
    //database class serializes itself and contains references to table objects, which are also serializable

    private ArrayList<String> columnNames = new ArrayList<>();
    private ArrayList<ArrayList<String>> table = new ArrayList<>(); //this is our table structure
    private int columnNum;
    private int rowNum;
    private String tableName;
    private Map<String, Object> comparison;

    public Table(String name) {
        columnNames.add("id"); //add default column;
        table.add(new ArrayList<String>());
        table.get(0).add("id");
        tableName = name;
        rowNum=1; //we have id by default so 1 row (for column names) and 1 column
        columnNum=1;

        //this is a very long line. I had troubles with Map object, and this was the only way it worked.
        comparison = Map.of("<", new LessThan(), "==", new Equals(), ">=", new GreaterEqual(), ">", new GreaterThan(), "<=", new LessEqual(), "!=", new NotEqual(), "like", new Like());
    }

    public ArrayList<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(ArrayList<String> newColumnNames) {
        columnNames = newColumnNames;
    }

    public int getRowNum() {
        return rowNum;
    }

    public int getColumnNum() {
        return columnNum;
    }

    public void setRowNum(int newRowNum) {
        rowNum = newRowNum;
    }

    public void setColumnNum(int newColumnNum) {
        columnNum = newColumnNum;
    }

    public void initTable(ArrayList<String> columnList) {
        int i;

        for (i = 0; i < columnList.size(); i++) {
            //initialise columns at first Y0 index of outer arraylist
            String currentAttribute = columnList.get(i);
            addColumnNameToTable(columnList.get(i));
        }

        printTable();
    }

    public void addRowToTable(ArrayList<String> valuesList) throws InterpreterException {
        int i;

        valuesList.add(0, Integer.toString(rowNum));

        if (valuesList.size() != (columnNum)) { //handling for mismatched values and numbers of columns
            valueListHandler(valuesList, columnNum);
        }

        for (i = 0; i < columnNum; i++) {
            table.get(i).add(valuesList.get(i));
        }

        rowNum++;
    }

    public void addColumnNameToTable(String newColumnName) {
        int i;
        columnNames.add(newColumnName);
        columnNum++;

        table.add(new ArrayList());
        table.get(columnNum-1).add(newColumnName);

        if (rowNum > 1) { //we want rows to add on correctly on same index, so need to pad new column with empty strings
            for (i = 1; i < (rowNum); i++) {
                table.get(columnNum - 1).add("");
            }
        }

    }

    private void valueListHandler(ArrayList<String>valuesList, int expectedColumnNum) throws InterpreterException {
        int i;

        if (valuesList.size() > columnNum) { //if there are too many we don't know which ones are correct, so reject the query
            throw new InterpreterException("DataStructure.Table only has "+columnNum+" columns");
        }

        if (valuesList.size() < columnNum) { //if there are values user does not know, we can just add whitespace and alter later
            int missingColumns = columnNum - valuesList.size();

            for (i = 0; i < missingColumns; i++) {
                valuesList.add("");
            }
        }
    }

    //so formatter can have access to format tables by column
    private int getColumnIndexByName(String columnRequested) throws InterpreterException {
        int columnIndex;

        if (columnNames.contains(columnRequested)) {
            columnIndex = columnNames.indexOf(columnRequested);
            return columnIndex;

        } else {
            throw new InterpreterException(columnRequested+" attribute does not exist");
        }
    }

    //these are multi-row operations that make use of the index operations when conditions are met

    public ArrayList<String> getColumn(String columnName) {
        int i;
        int columnIndex;
        ArrayList<String>columnValues = new ArrayList<>();

        /*map the column name to arraylist index, so we can iterate through each row and just return values
        referenced by the column name specified*/

        try {
            columnIndex = getColumnIndexByName(columnName);

            for (i = 0; i < rowNum; i++) {
                String currentValue = table.get(columnIndex).get(i);
                columnValues.add(currentValue);
            }

        } catch (InterpreterException error) {
            System.err.println("column name does not exist");
        }

        return columnValues;
    }

    public void removeColumnByName(String columnName) throws InterpreterException {
        int i;
        int columnIndex;

        System.out.println(columnNum);
        System.out.println(rowNum);

        columnIndex = getColumnIndexByName(columnName);
        table.remove(columnIndex); //remove column reference
        columnNum--;
    }

    public void updateRowsByCondition(String attribute, String operator, String value, String setAttribute, String newValue) throws InterpreterException {
        int i, j, attributeIndex = 0, setAttributeIndex = 0;
        Operator operation = (Operator) comparison.get(operator);

        System.out.println(setAttribute);
        System.out.println(newValue);

        attributeIndex = getColumnIndexByName(attribute);
        setAttributeIndex = getColumnIndexByName(setAttribute);

        for (i = 1; i < rowNum; i++) { //for every row, update correct cell to desired value
            String currentRowValue = table.get(attributeIndex).get(i);

            if (operation.performComparison(currentRowValue,value)) {
                //cell of columnName we want to change, on the current row which meets condition
                table.get(setAttributeIndex).set(i, newValue);
            }

            System.out.println();
        }
    }

    //for select* query makes sense to return entire thing to formatter
    public ArrayList<ArrayList<String>> getTable() {
        return table;
    }

    public ArrayList<ArrayList<String>> getRowsByCondition(String attribute, String operator, String value) throws InterpreterException {
        int i, j, attributeIndex = 0;
        ArrayList<ArrayList<String>> tableByCondition = new ArrayList<>();
        Operator operation = (Operator) comparison.get(operator);

        attributeIndex = getColumnIndexByName(attribute);

        for (i = 0; i < columnNum; i++) {
            tableByCondition.add(new ArrayList<String>());
            tableByCondition.get(i).add(columnNames.get(i));
        }

        for (i = 1; i < rowNum; i++) { //i is column, j is row, k is our new constructed table
            String currentRowValue = table.get(attributeIndex).get(i);//check desired value against comparison

            if (operation.performComparison(currentRowValue,value)) {
                for (j = 0; j < columnNum; j++) {
                    tableByCondition.get(j).add(table.get(j).get(i));
                }

            }
        }

        return tableByCondition;
    }

    public void removeRowsByCondition(String attribute, String operator, String value) throws InterpreterException {
        int i, j, attributeIndex = 0;
        Operator operation = (Operator) comparison.get(operator);

        attributeIndex = getColumnIndexByName(attribute);

        for (i = 1; i < rowNum; i++) { //start at one because first row is column names
            String currentRowValue = table.get(attributeIndex).get(i);

            if (operation.performComparison(currentRowValue,value)) {
                for (j = 0; j < columnNum; j++) {
                    table.get(j).remove(i);
                }
                rowNum--;
                i--;
            }
        }
    }

    //deep copy of old table into a new object, for join
    public Table getTableCopy(String newTableName) {
        Table tableCopy = new Table(newTableName);
        ArrayList<ArrayList<String>> newTableFormat = new ArrayList<ArrayList<String>>();
        int i, j;

        for (i = 0; i < columnNum; i++) {
            newTableFormat.add(new ArrayList<>());

            for (j = 0; j < rowNum; j++) {
                String currentTableCell = table.get(i).get(j);
                newTableFormat.get(i).add(currentTableCell);
                System.out.println(currentTableCell);
            }
        }

        tableCopy.setTableReference(newTableFormat);

        tableCopy.printTable();
        return tableCopy;
    }

    //this is also for join, required for getTableCopy to work correctly.
    public void setTableReference(ArrayList<ArrayList<String>> newTableReference) {
        table = newTableReference;
    }

    //for debugging
    public void printTable() {
        System.out.println(table.toString());
    }

    public void printColumn(ArrayList<String> column) {
        System.out.println(column.toString());
    }

}
