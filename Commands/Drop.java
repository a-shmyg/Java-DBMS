package Commands;

import DataStructure.Database;
import DataStructure.InterpreterException;
import DataStructure.Table;
import Main.Session;

public class Drop extends Interpreter {
    public Drop(Session currentSession) {
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

        if (structureType.equals("database")) {
            dropDatabaseByName(structureName);
        } else {
            dropTableByName(structureName);
        }
    }

    private void dropDatabaseByName(String structureName) {
        session.dropDatabaseByName(structureName);
        session.clearCurrentDatabase();
        session.getFormatResponse("OK");
    }

    private void dropTableByName(String structureName) throws InterpreterException {
        if (currentTable != null) {
            currentDatabase.dropTableByName(structureName);
            session.serializeDatabaseToFile();
            session.getFormatResponse("OK");
        } else {
            throw new InterpreterException("table name doesn't exist");
        }
    }
}
