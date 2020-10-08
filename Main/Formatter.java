package Main;

import DataStructure.InterpreterException;
import DataStructure.Table;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Formatter { //responsible for correctly writing response to client through .out
    private BufferedWriter formatOut;
    final static char EOT = 4;

    public Formatter(BufferedWriter out) {
        formatOut = out;
    }

    //for formatting a whole table
    public void formatResponseTable(Table tableToFormat) throws IOException {
        int i, j;
        ArrayList<ArrayList<String>> currentTable = tableToFormat.getTable();
        int columns = currentTable.size();
        int rows = currentTable.get(0).size();
        String currentWord;

        for (i = 0; i < rows; i++) {
            for (j = 0; j < columns; j++) {
                currentWord = currentTable.get(j).get(i);

                if (checkStringLiteral(currentWord)) {
                    formatOut.write(currentWord.substring(1,currentWord.length()-1) + " ");
                    formatOut.flush();
                } else {
                    formatOut.write(currentWord + " ");
                    formatOut.flush();
                }
            }

            formatOut.write("\n");
            formatOut.flush();
        }

        formatOut.write(EOT+"\n");//end transmission once at format complete
        formatOut.flush();
        tableToFormat.printTable();
    }

    public void printError(Exception error) throws IOException {
        System.out.println("formatting error");
        formatOut.write("ERROR:"+error+"\n");
        formatOut.flush();
        formatOut.write(EOT+"\n");
        formatOut.flush();
    }

    public void successfulResponse() throws IOException {
        System.out.println("formatting response");
        formatOut.write("OK"+"\n");
        formatOut.flush();
        formatOut.write(EOT+"\n");
        formatOut.flush();
    }

    public void formatResponseByColumn(ArrayList<String> columnNamesToFormatBy, Table tableToFormat) throws IOException, InterpreterException {
        int i, j;
        ArrayList<ArrayList<String>> currentTable = tableToFormat.getTable();
        ArrayList<Integer> columnIndexes = new ArrayList<>();
        ArrayList<String> tableColumnNames = tableToFormat.getColumnNames();
        int rows = currentTable.get(0).size();
        String currentWord;

        for (i = 0; i < columnNamesToFormatBy.size(); i++) {
            columnIndexes.add(resolveColumnIndexByName(tableColumnNames,columnNamesToFormatBy.get(i)));
        }

        System.out.println(columnIndexes.toString());

        for (i = 0; i < rows; i++) { //for each row
            for (j = 0; j < columnIndexes.size(); j++) { //for each selected column
                int currentColumnIndex = columnIndexes.get(j);
                currentWord = currentTable.get(currentColumnIndex).get(i);

                if (checkStringLiteral(currentWord)) {
                    formatOut.write(currentWord.substring(1,currentWord.length()-1)+" ");
                    formatOut.flush();
                } else {
                    formatOut.write(currentTable.get(currentColumnIndex).get(i) + " ");
                    formatOut.flush();
                }
            }
            formatOut.write("\n"); //next row
            formatOut.flush();
        }
        formatOut.write(EOT+"\n");
        formatOut.flush();
        tableToFormat.printTable();
    }

    private boolean checkStringLiteral(String value) {
        if (value.startsWith("'")) {
            return true;
        }
        return false;
    }

    public int resolveColumnIndexByName(ArrayList<String>columnNamesAvailable, String columnName) throws InterpreterException {
        int columnIndexByName = 0;

        System.out.println(columnNamesAvailable);

        if (columnNamesAvailable.contains(columnName)) {
            columnIndexByName = columnNamesAvailable.indexOf(columnName);
            return columnIndexByName;

        } else {
            throw new InterpreterException(" attribute name does not exist");
        }

    }
}
