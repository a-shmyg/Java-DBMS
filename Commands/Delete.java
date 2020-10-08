package Commands;

import DataStructure.Database;
import DataStructure.InterpreterException;
import DataStructure.Table;
import Main.Session;

import java.util.ArrayList;

public class Delete extends Interpreter {
    public Delete(Session currentSession) {
        super(currentSession);
    }

    @Override
    public void executeQuery() throws InterpreterException {
        Database currentDatabase = session.getCurrentDatabase();

        if (currentDatabase != null) {
            currentTable = currentDatabase.getTableByName(structureName);
            if (currentTable == null) {
                throw new InterpreterException("table doesn't exist");
            }
        } else {
            throw new InterpreterException("no database selected");
        }

        deleteRowsByCondition(structureName, conditionalExpressions.pop());
    }

    private void deleteRowsByCondition(String structureName, ArrayList<String> expression) throws InterpreterException {
        Database currentDatabase = session.getCurrentDatabase();
        Table currentTable = currentDatabase.getTableByName(structureName);

        if (currentTable == null) {
            throw new InterpreterException("table does not exist");
        }

        currentTable.removeRowsByCondition(expression.get(0), expression.get(1), expression.get(2));
        session.serializeDatabaseToFile();
        session.getFormatResponse("OK");
    }

}
