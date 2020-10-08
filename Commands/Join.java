package Commands;

import DataStructure.Database;
import DataStructure.Table;
import Main.Session;

public class Join extends Interpreter {
    public Join(Session currentSession) {
        super(currentSession);
    }

    @Override
    public void executeQuery() {

    }

    public void joinTablesByAttribute(String structureName1, String structureName2) {
        Database currentDatabase = session.getCurrentDatabase();
        Table table1 = currentDatabase.getTableByName(structureName1);
        Table table2 = currentDatabase.getTableByName(structureName2);
        Table tempTable = table1.getTableCopy("tempTable");

    }
}
