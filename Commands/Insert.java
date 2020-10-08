package Commands;

import DataStructure.Database;
import DataStructure.InterpreterException;
import DataStructure.Table;
import Main.Session;

import java.util.ArrayList;

public class Insert extends Interpreter {
    //insert a row into a table present within current database

    public Insert(Session currentSession) {
        super(currentSession);
    }

    @Override
    public void executeQuery() throws InterpreterException {
        currentDatabase = session.getCurrentDatabase();

        if (currentDatabase != null) {
            currentTable = currentDatabase.getTableByName(structureName);
        } else {
            throw new InterpreterException("no database selected");
        }

        insertValuesIntoTable(commandValues, structureName);
    }

    public void insertValuesIntoTable(ArrayList<String> valueList, String tableName) throws InterpreterException {
        if (currentTable == null) { //null guard to make sure table exists
            throw new InterpreterException("table does not exist");
        } else {
            currentTable.addRowToTable(valueList);
        }

        session.serializeDatabaseToFile();
        currentTable.printTable(); //debugging
        session.getFormatResponse("OK");
    }
}
