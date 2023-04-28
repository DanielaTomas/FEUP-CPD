import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private GameServer server;
    private BufferedReader input;
    private PrintWriter output;
    private String username;
    private UUID token;

    public ClientHandler(Socket socket,GameServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // Client Authentication

            output.println("Enter username:");
            String clientMessage = input.readLine();

            if (isValidToken(clientMessage)) {
                token = UUID.fromString(clientMessage);
                username = server.getConnectedClients().get(token);
                output.println("Authentication successful. Welcome back, " + username);
            } else {
                username = clientMessage;
                token = generateToken();
                server.getConnectedClients().put(token, username);
                output.println("Registration successful. Welcome, " + username + " : " + token.toString());
            }


            // Client Menu
            while(true){
                output.println(4);//number of lines to follow
                output.println("Select an option:\n" +
                                "1. Find an opponent\n" +
                                "2. View waiting list\n" +
                                "3. Quit");

                output.println(0);
                String choice = input.readLine();
                

                if (choice.equals("1")) {
                    output.println(1);
                    output.println("You choose : 1. Find an opponent" );
                    //findOpponent();
                } else if (choice.equals("2")) {
                    output.println(1);
                    output.println("You choose : 2. View waiting list" );
                    //viewWaitingList();
                } else if (choice.equals("3")) {
                    output.println(2);
                    output.println("You choose : 3. Quit" );
                    output.println("Closing connection. Goodbye " + username + "!");
                    output.println(-1);
                    break;
                    //quit();
                } else {
                    output.println(1);
                    output.println("Invalid choice. Please try again.");
                }
                
            
            }
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidToken(String message) {
        try {
            UUID tempToken = UUID.fromString(message);
            //if ( tempToken != null && server.getConnectedClients().containsKey(tempToken) ) return true;
            
            return true;
        } catch (IllegalArgumentException e) {
            // Handle the case where tempToken is not a valid UUID
            return false;
        }
        
    }

    private UUID generateToken() {
        return UUID.randomUUID();
    }
    /* 
    private void findOpponent() {
        waitingClients.put(username, token);
        output.println("Finding opponent...");
        while (true) {
            // Check if there are any waiting clients
            if (waitingClients.size() > 1) {
                // Remove the current client from the waiting list
                waitingClients.remove(username);

                // Get the first waiting client
                String opponentUsername = waitingClients.keys().nextElement();
                String opponentToken = waitingClients.remove(opponentUsername);

                // Add the two clients to the playing list
                playingClients.put(username, opponentToken);
                playingClients.put(opponentUsername, token);

                // Start a new game
                //TicTacToeGame game = new TicTacToeGame(username, opponentUsername, token, opponentToken);
                //executor.submit(game);
                break;
            }
        }
    }

            private void viewWaitingList() {
        if (waitingClients.isEmpty()) {
            output.println("There are no clients waiting.");
        } else {
            output.println("Clients waiting:");
            for (String waitingClient : waitingClients.keySet()) {
                output.println(waitingClient);
            }
        }
    }

    private void quit() {
        // Remove the client from any lists
        
        connectedClients.remove(username);
        waitingClients.remove(username);
        playingClients.remove(username);

        output.println("Goodbye!");
    }*/
}


