package Commands;

import DataStructure.Database;
import DataStructure.InterpreterException;
import DataStructure.Table;
import Main.Session;

import java.io.IOException;
import java.util.ArrayList;

public class Select extends Interpreter {
    public Select(Session currentSession) {
        super(currentSession);
    }

    @Override
    public void executeQuery() throws InterpreterException, IOException {
        currentDatabase = session.getCurrentDatabase();

        if (currentDatabase != null) {
            currentTable = currentDatabase.getTableByName(structureName);

            if (currentTable == null) {
                throw new InterpreterException("table does not exist");
            }
        } else {
            throw new InterpreterException("no database selected");
        }


        if (conditionalExpressions.size() > 0) {
            currentTable = selectByCondition(popConditionFromStack());
            currentTable.printTable();
        }

        //if no expression present assume we use the whole table

        if (commandAttributes.get(0).equals("*")) {
            //format current table (either temp or original) by everything
            session.getFormattedTable(currentTable);

        } else {
            //format by column list
            session.getFormattedTableByColumn(currentTable, commandAttributes);

        }


    }



    public Table selectByCondition(ArrayList<String> expression) throws InterpreterException { //returns a temporary table object
        //parser will check condition is 3 long, no need to verify that here again
        Database currentDatabase = session.getCurrentDatabase();
        Table currentTable = currentDatabase.getTableByName(structureName);
        Table tableByCondition = new Table("tempTable");

        tableByCondition.setTableReference(currentTable.getRowsByCondition(expression.get(0), expression.get(1), expression.get(2)));
        tableByCondition.setColumnNames(currentTable.getColumnNames());

        return tableByCondition;
    }
}
