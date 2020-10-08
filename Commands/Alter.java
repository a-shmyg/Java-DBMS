package Commands;

import DataStructure.Database;
import DataStructure.InterpreterException;
import DataStructure.Table;
import Main.Session;

public class Alter extends Interpreter {
    public Alter(Session currentSession) {
        super(currentSession);
    }

    @Override
    public void executeQuery() throws InterpreterException {
        System.out.println("executing query from alter");

        if (alterType.equals("drop")) {
            removeColumnByAttribute(structureName);
        } else {
            addColumnByAttribute(structureName);
        }

    }

    private void addColumnByAttribute(String structureName) throws InterpreterException {
        Database currentDatabase = session.getCurrentDatabase(); //this is in dire need of a refactor once over
        Table currentTable = currentDatabase.getTableByName(structureName);

        if (currentTable == null) {
            throw new InterpreterException("table does not exist");
        }

        currentTable.addColumnNameToTable(commandAttributes.get(0));
        session.serializeDatabaseToFile();
        session.getFormatResponse("OK");
    }

    private void removeColumnByAttribute(String structureName) throws InterpreterException {
        Database currentDatabase = session.getCurrentDatabase();
        Table currentTable = currentDatabase.getTableByName(structureName);

        if (currentTable == null) {
            throw new InterpreterException("table does not exist");
        }

        currentTable.removeColumnByName(commandAttributes.get(0));
        session.serializeDatabaseToFile();
        session.getFormatResponse("OK");
    }
}
