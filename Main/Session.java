package Main;

import DataStructure.Database;
import DataStructure.InterpreterException;
import DataStructure.Table;

import java.io.*;
import java.util.ArrayList;

public class Session { //class deals with persistence, writing to disk, reading from disk, keeping current DB file in memory, and communication between file system and the tables
    private Database currentDatabase; //keep track of our currentDB as an object
    private String databaseName; //name of DB user selected
    private Formatter formatResponse; //this will display output to the user correctly

    public Session() {
        //session constructor
    }

    public Database getCurrentDatabase() {
        return currentDatabase;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String newDatabaseName) {
        databaseName = newDatabaseName;
    }

    public void clearCurrentDatabase() {
        databaseName = null;
        currentDatabase = null;
    }

    public void setFormatResponse(Formatter newFormatter) {
        formatResponse = newFormatter;
    }

    public void getFormattedTable(Table tableToFormat) {
        try {
            if (tableToFormat != null) {
                formatResponse.formatResponseTable(tableToFormat);
            }
        } catch (IOException error) {
            System.out.println("internal error");
        }
    }

    public void getFormattedTableByColumn(Table tableToFormat, ArrayList<String>attributesToFormatBy) throws InterpreterException, IOException {
        try {
            if (tableToFormat != null) {
                formatResponse.formatResponseByColumn(attributesToFormatBy,tableToFormat);
            }
        } catch (IOException error) {
            System.out.println("internal error");
        }
    }

    public void getFormatResponse(String formatRequest)  {
        try {
            if (formatRequest.equals("OK")) {
                formatResponse.successfulResponse();
            }
        } catch (IOException err) {
            System.out.println("internal error");
        }
    }

    //need a method for after creating a new DB structure in memory, but not yet on disk
    public void loadNewDatabaseFromMemory(Database newDatabase) {
        currentDatabase = newDatabase;
        databaseName = newDatabase.getDatabaseName();
    }

    //delete a database file from root directory
    public void dropDatabaseByName(String databaseName) {
        File databaseFile = new File(databaseName+".db");

        if (databaseFile.delete()) {
            System.out.println("successfully dropped database");
        } else {
            System.out.println("database could not be deleted, file not exist?");
        }
    }

    //deserialize object from root folder
    public void loadNewDatabaseFromFile(String databaseName) throws InterpreterException {
        System.out.println("loading in new DB");

        try {
            FileInputStream fileIn = new FileInputStream(databaseName + ".db"); //finds file, checks it exists
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            System.out.println("DE-SERIALIZING"); //for debugging

            currentDatabase = (Database) objectIn.readObject(); //read in bytes, cast to Database object
            objectIn.close();

        } catch (IOException err) {
            throw new InterpreterException("database file does not exist");

        } catch (ClassNotFoundException err) {
            throw new InterpreterException("serialization error");
        }
    }

    //serialize object to root folder
    public void serializeDatabaseToFile() {
        if (currentDatabase != null) { //null guard to make sure we don't serialize before user has selected a database into memory

            try {
                FileOutputStream fileOut = new FileOutputStream(databaseName + ".db");
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

                System.out.println("SERIALIZING"); //for debugging

                objectOut.writeObject(currentDatabase);
                objectOut.flush();
                objectOut.close();
            } catch (IOException err) {
                System.out.println(err);
            }
        }
    }
}
