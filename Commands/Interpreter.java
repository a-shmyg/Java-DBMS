package Commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import DataStructure.Database;
import DataStructure.InterpreterException;
import DataStructure.Table;
import Main.Session;

public class Interpreter {
    //general class for the command types, takes in a syntax built from the parser, spawns correct command object, transfers control to objec
    protected Session session; //reference to current session Interpreter was passed
    protected Stack<ArrayList<String>> conditionalExpressions;
    protected ArrayList<String> commandAttributes; //columns
    protected ArrayList<String> commandValues; //values to compare/change
    protected String structureName;
    protected String commandName; //type of command
    protected String structureType;
    protected String alterType;
    protected String joinStructureName;
    protected Database currentDatabase;
    protected Table currentTable;

    public Interpreter(Session currentSession) { //we need session as no other way to communicate when we want to load a DB (primarily for use and create)
        session = currentSession;
    }

    public void executeQuery() throws InterpreterException, IOException {
    }

    //setters for building the generic interp object, we will then pass this to specific command object to execute

    public void setJoinStructureName(String newStructureName) {
        joinStructureName = newStructureName;
    }

    public void setAlterType(String newAlterType) {
        alterType = newAlterType;
    }

    public void setConditionalExpressions(Stack<ArrayList<String>> newConditionalExpressions) {
        conditionalExpressions = newConditionalExpressions;
    }

    public void setCommandName(String newCommandName) {
        commandName = newCommandName;
    }

    public void setCommandAttributes(ArrayList<String> newCommandAttributes) {
        commandAttributes = newCommandAttributes;
    }

    public void setCommandValues (ArrayList<String> newCommandValues) {
        commandValues = newCommandValues;
    }

    public void setStructureName(String newStructureName) {
        structureName = newStructureName;
    }

    public void setStructureType(String newStructureType) {
        structureType = newStructureType;
    }

    public void pushConditionToStack(ArrayList<String> condition) { //for executing conditional expressions in the correct order;
        conditionalExpressions.push(condition);
    }

    public ArrayList<String> popConditionFromStack() {
        return conditionalExpressions.pop();
    }

    //for debugging
    public void printFields() {
        System.out.println(commandAttributes.toString());
        System.out.println(commandValues.toString());
        System.out.println(commandName);
        System.out.println(structureName);
        System.out.println(structureType);
        System.out.println(commandValues.toString());
        System.out.println(conditionalExpressions.toString());
    }
}

