package Main;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Tokeniser { //note to self internal methods must be changed to private!
    private String currentCommand;
    private String[] tokensArray;
    private ArrayList<String> tokensList; //reference to array list

    public Tokeniser() {

    }

    public void setCommand(String command) {
        currentCommand = command;
    }

    public ArrayList<String> getTokens() {
        return tokensList;
    }

    public void tokeniseCommand(String command) throws ParserException {
        setCommand(command);
        splitByDelimiters();
        trimTokens(tokensArray);
        createArrayList();
        removeNullTokens();
        stringLiteralHandler();
        checkEndOfCommand();
        StringLiteralValidate();
    }

    public void splitByDelimiters() {
        String delimiters = "(?=[ ,;()])|(?<=[,;()])"; //regex with look ahead/look behind for characters in BNF to delimit by
        tokensArray = currentCommand.split(delimiters); //split into tokens by split regex
    }

    public void trimTokens(String[] tokenArray) { //regex results in spaces before words, so we want to take them out
        int i;

        for (i = 0; i < tokenArray.length; i++) {
            tokenArray[i] = tokenArray[i].trim();
        }

    }

    public void createArrayList() {
        tokensList = new ArrayList<String>(Arrays.asList(tokensArray)); //new arraylist, turn the existing array into a arraylist for easy removal/manipulation
    }

    public void removeNullTokens() { //regex for splitting leaves null tokens, we want to take them out
        int i;

        for (i = 0; i < tokensList.size(); i++) {
            if (tokensList.get(i).equals("")) {
                System.out.println("empty string, deleting");
                tokensList.remove(i);
                i--;
            }
        }

    }

    public void stringLiteralHandler() { //we want to handle string literals into one token
        int i, stringLiteralIndex = 0, listSize = tokensList.size();
        boolean stringLiteralFlag = false;
        String stringLiteral="";

        for (i = 0; i < listSize; i++) {
            String currentWord = tokensList.get(i);

            //for every word that is part of the literal, we want to join to the first one we found, and remove the copies
            if (stringLiteralFlag == true) {
                tokensList.remove(stringLiteralIndex); //java strings are immutable, we have to make a new String to place into token list
                tokensList.add(stringLiteralIndex, String.join(" ", stringLiteral, currentWord)); //join what we already have to new part of string literal

                stringLiteral = tokensList.get(stringLiteralIndex); //set new reference to our new String, in the correct place
                tokensList.remove(currentWord); //remove duplicate partial word

                listSize--; //adjust new list size, and correct position of next word
                i--;
            }

            if (currentWord.substring(0,1).equals("'")) { //beginning of string
                stringLiteralFlag = true;
                stringLiteralIndex = i; //mark index where we found the string start
                stringLiteral = currentWord; //mark reference string partial we will join on
                System.out.println("string literal begins at "+stringLiteralIndex);
            }

            if (currentWord.substring((currentWord.length()-1)).equals("'")) {
                System.out.println("string literal ends");
                stringLiteralFlag = false;
            }

        }
    }

    public void StringLiteralValidate() throws ParserException {
        int i;

        for (i = 0; i < tokensList.size(); i++) {
            String currentWord = tokensList.get(i);

            if (currentWord.startsWith("'")) {
                if (!currentWord.endsWith("'")) {
                    throw new ParserException(" missing ' on end of string literal");
                }
            }
        }
    }

    public void checkEndOfCommand() throws ParserException {
        if (!tokensList.get(tokensList.size()-1).equals(";")) {
            throw new ParserException("ERROR: missing ; at end of statement");
        }
    }

// PRINTING COMMANDS FOR DEBUGGING

    public void printCommand(String command) { //for debugging
        System.out.println(command);
    }

    public void printTokens(String[] tokens) { //for debugging
        int i;

        System.out.println("PRINTING ARRAY");
        for (i = 0; i < tokens.length; i++) {
            System.out.println(tokens[i]);
        }
    }

    public void printListTokens(ArrayList<String> tokens) { //for debug
        int i;

        System.out.println("PRINTING ARRAYLIST");
        for (i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i));
        }
    }
}