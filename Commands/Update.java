package Commands;

import DataStructure.Database;
import DataStructure.InterpreterException;
import DataStructure.Table;
import Main.Session;

import java.util.ArrayList;

public class Update extends Interpreter {
    public Update(Session currentSession) {
        super(currentSession);
    }

    @Override
    public void executeQuery() throws InterpreterException {
        currentDatabase = session.getCurrentDatabase();

        if (currentDatabase != null) {
            currentTable = currentDatabase.getTableByName(structureName);

            if (currentTable == null) {
                throw new InterpreterException("table does not exist");
            }

        } else {
            throw new InterpreterException("no database selected");
        }

        updateTableValues(conditionalExpressions.pop());
    }

    public void updateTableValues(ArrayList<String> expression) throws InterpreterException {
        currentTable.updateRowsByCondition(expression.get(0), expression.get(1), expression.get(2), commandAttributes.get(0), commandValues.get(0));
        session.serializeDatabaseToFile();
        session.getFormatResponse("OK");
    }
}
