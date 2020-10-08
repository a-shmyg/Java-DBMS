package Commands;

import DataStructure.InterpreterException;
import Main.Session;

public class Use extends Interpreter { //loads a .db file from disk into memory ready for operations
    public Use(Session currentSession) {
        super(currentSession);
    }

    @Override
    public void executeQuery() throws InterpreterException {
        if (session.getCurrentDatabase() != null) {
            session.serializeDatabaseToFile();
        }

        session.clearCurrentDatabase();
        session.loadNewDatabaseFromFile(structureName);
        session.setDatabaseName(structureName);
        session.getFormatResponse("OK");

    }
}
