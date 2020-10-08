package Main;

import DataStructure.InterpreterException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class DBServer {
    final static char EOT = 4;
    private String currentCommand = ""; //change this

    public static void main(String args[])
    {
        new DBServer(8888);
    }

    public DBServer(int portNumber)
    {
        try {
            ServerSocket ss = new ServerSocket(portNumber); //initalise a connection to listen to in serversocket class
            System.out.println("Main.DBServer Listening...");

            while(true) {
                acceptNextConnection(ss);
            }

        } catch(IOException ioe) {
            System.out.println("cannot connect, wrong port?");
            System.err.println(ioe);
        }
    }

    private void acceptNextConnection(ServerSocket ss) {
            Session userSession = new Session();
            String nextCommand;

            try {
                Socket socket = ss.accept(); //accept connection incoming
                System.out.println("accepted new connection");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                System.out.println("new buffered reader");

                while(!currentCommand.equals("exit")) {
                    nextCommand = processNextCommand(in);
                    handleCommand(nextCommand, userSession, out);
                }

                System.out.println("exiting...");
                in.close();
                out.close();
                socket.close();

            } catch (IOException ioe) {
                System.err.println(ioe);
                ioe.printStackTrace();
            }
    }

    private String processNextCommand(BufferedReader in) throws IOException //server stuff
    {
        String line = in.readLine();
        currentCommand = line;

        return line;
    }

    private void handleCommand(String commandString, Session session, BufferedWriter out) throws IOException {
        Formatter formatResponse = new Formatter(out);
        Tokeniser commandTokenizer = new Tokeniser();
        Parser commandParser = new Parser(session);
        session.setFormatResponse(formatResponse);

        try {
            commandTokenizer.tokeniseCommand(commandString);

            try {
                commandParser.setTokenList(commandTokenizer.getTokens());
                commandParser.parseCommand(); //check for parsing errors, build tree for command objects
                commandParser.interpretQuery();


            } catch (ParserException parsingError) {
                formatResponse.printError(parsingError);

            } catch (InterpreterException runtimeError) {
                formatResponse.printError(runtimeError);
            }


        } catch (ParserException lexerError) {
            formatResponse.printError(lexerError);
        }

    }

}
