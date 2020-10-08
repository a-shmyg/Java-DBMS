package Main;

import Commands.*;
import DataStructure.InterpreterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Parser {
    private ArrayList<String> tokens; //token stream
    private int index; //current position in token stream
    private Session session; //we need this here to pass to interpret objects

    //the parser goes through the tokens in a recursive fashion, taking everything it needs from the tokens to pass to
    //interpreter object is created, which then executes the actual command

    protected Stack<ArrayList<String>> conditionalExpressions = new Stack<>(); //holds expressions for conditionals
    protected ArrayList<String> commandAttributes = new ArrayList<String>(); //column names
    protected ArrayList<String> commandValues = new ArrayList<String>(); //values in table cells
    protected String commandName;
    protected String structureName;
    protected String structureType;
    protected String alterType;
    protected String joinStructureName;

    Map<String, Interpreter> allAvailableCommands;

    public Parser(Session currentSession) {
        System.out.println("inside parser with tokens array");
        index = 0;
        tokens = null;

        session = currentSession;
        /*I understand this line is very long, and that is bad. When I tried to use .put method I got an NullPointerException, and IncorrectOperationException
        because Map object seemed to be immutable (?). Possibly I don't understand Map object well enough, however this way it at least worked*/
        allAvailableCommands = Map.of("create", new Create(session), "select", new Select(session), "use", new Use(session), "drop", new Drop(session), "delete", new Delete(session), "insert", new Insert(session), "join", new Join(session), "update", new Update(session), "alter", new Alter(session));

    }

    /*after we finished building our attribute lists and command info from tokens,
    we pass this along to the correct interpreter object which then does the command*/
    public void interpretQuery() throws InterpreterException, IOException {
        Interpreter newQuery = allAvailableCommands.get(commandName);
        initialiseNewQuery(newQuery);
        newQuery.executeQuery();
        System.out.println(session.getDatabaseName()+" FROM PARSER");
    }

    //set references to the lists we build, so child object Interpreter can have access and perform operations on tables
    private void initialiseNewQuery(Interpreter newQuery) {
        newQuery.setCommandAttributes(commandAttributes);
        newQuery.setCommandName(commandName);
        newQuery.setCommandValues(commandValues);
        newQuery.setConditionalExpressions(conditionalExpressions);
        newQuery.setStructureName(structureName);
        newQuery.setStructureType(structureType);
        newQuery.setJoinStructureName(structureName);
        newQuery.setAlterType(alterType);
    }

    public void setTokenList(ArrayList<String> tokensArray) {
        System.out.println("setting tokens arraylist");
        tokens = tokensArray;
    }

    public void parseCommand() throws ParserException {
        if (tokens != null) { //null guard in case something is not tokenized right
            command(tokens); //recursively descent into the token list and parse token by token

        } else {
            throw new ParserException("ERROR: Missing command");
        }
    }

    private boolean checkSyntax(String checkToken, int expectedIndex, ArrayList<String> tokensList) throws ParserException {
        if (!tokensList.get(index).toLowerCase().equals(checkToken)) { //if the current token when accounting for case does not match specific syntax
            throw new ParserException("ERROR: syntax not correct, missing " + checkToken);
        }
        System.out.println("checking syntax");
        return true;
    }

    private void checkLength(int expectedLength, int currentLength) throws ParserException {
        if (expectedLength != currentLength) throw new ParserException("too many/few tokens");
    }

    private void checkOperator(String operator) throws ParserException { //check operator is valid and if so pass on to command
        ArrayList<String> validOperators = new ArrayList<>();

        validOperators.add("==");
        validOperators.add("!=");
        validOperators.add("like");
        validOperators.add(">=");
        validOperators.add(">");
        validOperators.add("<=");
        validOperators.add("<");

        if (!validOperators.contains(operator.toLowerCase())) {
            throw new ParserException("comparison operator not valid");
        }
    }

    private void command(ArrayList<String> tokensList) throws ParserException {
        System.out.println("currently on tape: " + index);
        if (index >= tokensList.size()) {
            return;
        }

        if (tokensList.get(index).equals(";")) {
            return;
        }

        commandType(tokensList);
        command(tokensList);
    }

    private void commandType(ArrayList<String> tokensList) throws ParserException {
        String currentCommandType = tokensList.get(index).toLowerCase();

        switch (currentCommandType) {
            case "use":
                System.out.println("use command: ");
                index++;
                use(tokensList);
                break;
            case "create":
                System.out.println("create command: ");
                index++;
                create(tokensList);
                break;
            case "drop":
                System.out.println("drop command: ");
                index++;
                drop(tokensList);
                break;
            case "alter":
                System.out.println("alter command: ");
                index++;
                alter(tokensList);
                break;
            case "insert":
                System.out.println("insert command: ");
                index++;
                insert(tokensList);
                break;
            case "select":
                System.out.println("select command: ");
                index++;
                select(tokensList);
                break;
            case "update":
                System.out.println("update command: ");
                index++;
                update(tokensList);
                break;
            case "delete":
                System.out.println("delete command: ");
                index++;
                delete(tokensList);
                break;
            case "join":
                System.out.println("join command: ");
                index++;
                join(tokensList);
                break;
            default:
                throw new ParserException("ERROR: command doesnt exist");
        }

        commandName = currentCommandType;

        return;
    }


    private void use(ArrayList<String> tokensList) throws ParserException {
        System.out.println("    inside use statement:");
        checkLength(3, tokensList.size());

        structureName = tokensList.get(index);
        structureType = "database";

        index++;

        return;
    }

    private void drop(ArrayList<String> tokensList) throws ParserException {
        System.out.println("    inside drop command");
        checkLength(4, tokensList.size());

        if (tokensList.get(index).toLowerCase().equals("database")) { //deal with either case for BNF tokens
            System.out.println("dropping db");
            structureType = "database";

        } else if (tokensList.get(index).toLowerCase().equals("table")) {
            System.out.println("dropping table");
            structureType = "table";

        } else {
            throw new ParserException("invalid structure");
        }
        index++;

        structureName = tokensList.get(index);

        index++;

        return;
    }

    private void create(ArrayList<String> tokensList) throws ParserException {
        System.out.println("    inside create command:" + tokensList.get(index));

        if (tokensList.get(index).toLowerCase().equals("database")) {
            index++;
            structureType = "database";
            createDatabase(tokensList);

        } else if (tokensList.get(index).toLowerCase().equals("table")) {
            index++;
            structureType = "table";
            createTable(tokensList);

        } else {
            throw new ParserException("can only create table or database");
        }
    }

    private void createDatabase(ArrayList<String> tokensList) throws ParserException { //LEAF
        System.out.println("        inside createDatabase handler");
        checkLength(4, tokensList.size());

        structureName = tokensList.get(index);

        index++;
        return;
    }

    private void createTable(ArrayList<String> tokensList) throws ParserException { //PROBABLY A LEAF EDIT:NOPE
        System.out.println("        inside CreateTable handler "+tokensList.get(index));

        structureName = tokensList.get(index);
        //handle empty table
        if (tokensList.size() == 4) {
            System.out.println("create empty table");


            index++;
            return;
        }

        index++; //move token up to attribute list
        System.out.println(tokensList.get(index));

        checkSyntax("(", index, tokensList); //beginning of attributes list
        attributeList(tokensList);

        return;
    }

    private void attributeList(ArrayList<String> tokensList) throws ParserException { //does the same thing as value, we can condense into one
        System.out.println("            Inside attribute list condition: " + tokensList.get(index));
        System.out.println(tokensList.get(index));

        index++; //should land on either comma or a )

        commandAttributes.add(tokensList.get(index));
        System.out.println(tokensList.get(index));

        index++;

        if (tokensList.get(index).equals(")")) { //we can have EITHER an attribute name on it's own, in which case this is our base case
            index++;
            return;
        } else if (tokensList.get(index).equals(",")) {  //OR we have additional attributes after the first one
            attributeList(tokensList);
        } else {
            throw new ParserException("missing comma or bracket after attribute");
        }
    }

    private void alter(ArrayList<String> tokensList) throws ParserException {
        System.out.println("    inside alter:");
        checkSyntax("table", index, tokensList);
        index++;

        structureType = "table";
        structureName = tokensList.get(index);

        index++;

        if (tokensList.get(index).toLowerCase().equals("drop")) {
            //dropping a column
            alterType = "drop";
            index++;
            dropColumn(tokensList);

        } else if (tokensList.get(index).toLowerCase().equals("add")) {
            //adding a column
            alterType = "add";
            index++;
            addColumn(tokensList);

        } else {
            throw new ParserException("ERROR: alteration type invalid");
        }

        return;
    }

    private void dropColumn(ArrayList<String> tokensList) {
        System.out.println("        inside drop column");
        commandAttributes.add(tokensList.get(index));
        index++;
        return;
    }

    private void addColumn(ArrayList<String> tokensList) {
        System.out.println("        inside add column");
        commandAttributes.add(tokensList.get(index));
        index++;
        return;
    }

    private void insert(ArrayList<String> tokensList) throws ParserException {
        System.out.println("    inside insert: "+tokensList.get(index));
        checkSyntax("into", index, tokensList);
        index++;

        if (tokensList.size() < 4) {
            throw new ParserException("missing end of statement");
        }

        structureName = tokensList.get(index);
        structureType = "table";

        index++;

        checkSyntax("values", index, tokensList);
        index++;

        checkSyntax("(", index, tokensList);
        index++;
        valueList(tokensList);
    }

    private void valueList(ArrayList<String> tokensList) throws ParserException { //inserts a complete row
        System.out.println("        inside value list:" + tokensList.get(index));

        commandValues.add(tokensList.get(index));
        index++;

        if (tokensList.get(index).equals(")")) {
            index++;
            return;
        } else if (tokensList.get(index).equals(",")) {
            index++;
            valueList(tokensList);
        } else {
            throw new ParserException("missing , or )");
        }
    }

    private void select(ArrayList<String> tokensList) throws ParserException {
        System.out.println("    inside select list:" + tokensList.get(index));

        structureType = "table";

        //this can be ALL columns:
        if (tokensList.get(index).equals("*")) {
            //set attribute list to * to signify user wants ALL columns
            commandAttributes.add("*");
            index++;
            index++;

        } else {
            //OR specific columns we choose, to pass along to formatter to display after query
            selectAttribute(tokensList);
            index++;
        }

        structureName = tokensList.get(index);
        index++;

        if (tokensList.get(index).equals(";")) {
            System.out.println("end of command tree, returning");
            return;
        }

        checkSyntax("where", index, tokensList);
        index++;

        if (!tokensList.get(index).equals("(")) { //not a chained operator, therefor not a base case
            addSelectStatement(tokensList); //just add single statement
            index++;

        } else {
            selectCondition(tokensList);
        }
        index++;
        return;
    }

    private void selectAttribute(ArrayList<String> tokensList) {
        System.out.println("        inside selectAttribute: " + tokensList.get(index));

        commandAttributes.add(tokensList.get(index));

        index++;

        if (tokensList.get(index).equals(",")) {
            index++;
            selectAttribute(tokensList);
        }

        return;
    }

    private void selectCondition(ArrayList<String> tokensList) throws ParserException { //recursive, build the expression stack as we go along
        System.out.println("        currently in condition:" + tokensList.get(index));

        index++;

        if (tokensList.get(index).equals("(")) {
            System.out.println("        end of condition" + tokensList.get(index));
        }

        if (tokensList.get(index + 1).toLowerCase().equals("and") || tokensList.get(index + 1).toLowerCase().equals("or")) {
            System.out.println("        chained by operator: " + tokensList.get(index + 1));
            index++;
            index++;
        }

        if (tokensList.get(index).equals(")")) { //MAIN BASE CASE
            System.out.println("        end of condition");
            index++;
            return;
        }

        addSelectStatement(tokensList);
        selectCondition(tokensList);

        index++;
        return;
    }

    private void addSelectStatement(ArrayList<String> tokensList) throws ParserException { //this is a single conditional statement
        System.out.println("            currently in the STATEMENT:" + tokensList.get(index));
        ArrayList<String> expression = new ArrayList<>();

        if (tokensList.get(index).equals("(")) {
            return;
        }

        System.out.println("!attribute: " + tokensList.get(index));
        expression.add(tokensList.get(index));
        index++;

        System.out.println("!operator: " + tokensList.get(index));
        checkOperator(tokensList.get(index)); //check operator valid, add to our expression
        expression.add(tokensList.get(index).toLowerCase());
        index++;

        System.out.println("!value:" + tokensList.get(index));
        expression.add(tokensList.get(index));
        System.out.println("!now we are here: " + tokensList.get(index));

        conditionalExpressions.push(expression); //add our entire expression to the conditional stack to be used for operations
        return;
    }


    private void update(ArrayList<String> tokensList) throws ParserException {
        System.out.println("    currently in update");

        structureType = "table";
        structureName = tokensList.get(index);
        index++;

        checkSyntax("set", index, tokensList);
        index++;

        nameValueList(tokensList);
        System.out.println("currently at:" + tokensList.get(index));

        checkSyntax("where", index, tokensList);

        index++;
        if (!tokensList.get(index).equals("(")) {
            addSelectStatement(tokensList);
            index++;
        } else {
            selectCondition(tokensList);
        }

        index++;
        return;
    }

    private void nameValueList(ArrayList<String> tokensList) throws ParserException {
        System.out.println("        inside name value pairs: " + tokensList.get(index));

        String column = tokensList.get(index);
        commandAttributes.add(column);
        index++;

        checkSyntax("=", index, tokensList); //check assignment operator present
        index++;

        String value = tokensList.get(index);
        commandValues.add(value);
        index++;

        if (tokensList.get(index).equals(",")) {
            index++;
            nameValueList(tokensList);
        }

        return;
    }

    private void delete(ArrayList<String> tokensList) throws ParserException {
        System.out.println("    currently in delete");

        structureType = "table";

        checkSyntax("from", index, tokensList);
        index++;
        structureName = tokensList.get(index);
        index++;
        checkSyntax("where", index, tokensList);
        index++;

        if (!tokensList.get(index).equals("(")) {
            addSelectStatement(tokensList);
            index++;
        } else {
            selectCondition(tokensList);
        }
        index++;
        return;
    }

    private void join(ArrayList<String> tokensList) throws ParserException {
        System.out.println("    currently in join");


        structureName = tokensList.get(index);
        index++;

        checkSyntax("and", index, tokensList);
        index++;

        joinStructureName = tokensList.get(index);
        index++;

        checkSyntax("on", index, tokensList);
        index++;

        commandAttributes.add(tokensList.get(index)); //first table attribute
        index++;

        checkSyntax("and", index, tokensList);
        index++;

        commandAttributes.add(tokensList.get(index)); //second table to be joined attribute
        index++;

        return;
    }
}