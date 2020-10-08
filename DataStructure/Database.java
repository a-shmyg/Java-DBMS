package DataStructure;

import DataStructure.Table;

import java.io.*;
import java.util.HashMap;

public class Database implements Serializable {
    //database object contains references to tables, which are serializable. Hence when database is serialized after every
    //operation change, the changes are saved.
    //tables accessed through DB object by name of table, obtained from the parser fields.
    private String databaseName;
    private HashMap<String,Table> tablesMap = new HashMap<>();

    public Database(String name) {
        databaseName = name;
    }

    public void addNewTable(String tableName) {
        Table newTable = new Table(tableName); //pass in the column names if we have them

        tablesMap.put(tableName, newTable);
    }

    public void dropTableByName(String tableName) {
        if (tablesMap.containsKey(tableName)) {
            tablesMap.remove(tableName);
            System.out.println("removed tables");
        } else {
            System.out.println("table does not exist");
        }
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public Table getTableByName(String tableName) {
        if (checkTableExists(tableName)) {
            return tablesMap.get(tableName);
        }
        return null;
    }

    public boolean checkTableExists(String tableName) {
        if (tablesMap.containsKey(tableName)) {
            return true;
        }
        return false;
    }
    //printing for debugging
    public void printAllTables() {
        System.out.println("available tables");
        for (String keys : tablesMap.keySet()) {
            System.out.println(keys + ":"+ tablesMap.get(keys));
        }
    }
}
