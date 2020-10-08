package Commands;

import DataStructure.Database;
import DataStructure.InterpreterException;
import DataStructure.Table;
import Main.Session;

import java.util.ArrayList;

public class Create extends Interpreter{


    public Create(Session currentSession) {
        super(currentSession);
    }

    @Override
    public void executeQuery() throws InterpreterException {
        if (structureType.equals("database")) {
            createNewDatabase(structureName);
        } else {
            createNewTableInDatabase(structureName);
        }
    }

    void createNewDatabase(String databaseName) throws InterpreterException {
        Database newDatabase = new Database(databaseName);
        //communicate to the session we have created a new DB and want it to be persistent

        session.loadNewDatabaseFromMemory(newDatabase); //new object we created, load into memory
        session.setDatabaseName(databaseName);
        session.serializeDatabaseToFile(); //create new db file
        session.clearCurrentDatabase(); //user may not necessarily want to work on this db, they must use USE command first
        session.getFormatResponse("OK");
    }

    public void createNewTableInDatabase(String tableName) throws InterpreterException {
        Database currentDatabase = session.getCurrentDatabase();

        if (currentDatabase == null) {
            throw new InterpreterException("no database selected");
        }

        if (!currentDatabase.checkTableExists(tableName)) { //make sure new table is unique
            currentDatabase.addNewTable(tableName);

            if (commandAttributes.size() != 0) {
                Table currentTable = currentDatabase.getTableByName(tableName);
                currentTable.initTable(commandAttributes);
            }

            session.serializeDatabaseToFile(); //save persistent changes to tables to db file
            session.getFormatResponse("OK");
        } else {
            throw new InterpreterException("table name already exists");
        }
    }
}
