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
    private User user;// used only when all user info is set -> after login/registration

    public ClientHandler(Socket socket,GameServer server) {
        this.socket = socket;
        this.server = server;
        this.user = null;
    }

    public ClientHandler(User user,GameServer server) {
        this.user = user;
        this.server = server;
        this.socket = user.getSocket();
        this.username = user.getName();
        this.token = user.getUuid();
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            while (user == null) this.authenticateUser();

            this.menu();

            //input.close();
            //output.close();
            //socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authenticateUser() throws IOException{

        output.println(MessageType.WELCOME);
        String response = input.readLine();
        String[] parts = response.split(":",2);//
        
        while(MessageType.valueOf(parts[0]) != MessageType.SUCCESS){
            System.out.println(MessageType.valueOf(parts[0]));
            if (MessageType.valueOf(parts[0]) == MessageType.LOGIN){
                if(isValidToken(parts[1])) {
                    token = UUID.fromString(parts[1]);
                    username = server.getConnectedClients().get(token);
    
                    output.println(MessageType.AUTHENTICATION_SUCESS);
                    break;
                }else{
                    output.println(MessageType.AUTHENTICATION_FAILURE);
                }
            }else {
                username = parts[1];
                token = generateToken();
                server.getConnectedClients().put(token, username);
                UserTokenFileHandler.addUserTokenPair(token, username);

                output.println(MessageType.AUTHENTICATION_RESPONSE + ":" + token.toString());

            }
            response = input.readLine();
            parts = response.split(":",2);
        }

        user = new User(socket, token, response, 0);

        System.out.println("User " + username + " has connected to the server ");
    }

    private void menu() throws IOException {
        while(true){
            output.println(MessageType.MAIN_MENU_PICK_OPTION);

            String choice = input.readLine();
            MessageType message = MessageType.valueOf(choice);
            

            if (message == MessageType.JOIN_QUEUE) {
                if (server.handleJoinQueue(new User(socket, token, username, 0))){//break out of loop if client join wait queue
                    output.println(MessageType.QUEUE_JOIN_SUCESS+":"+server.getWaitingClients().size());
                    break;
                } 

            }else if (message == MessageType.QUIT) {
                input.close();
                output.close();
                socket.close();
                break;
            }else {
                output.println(MessageType.MAIN_MENU_INVALID_OPTION);
            }
        }
    }

    private boolean isValidToken(String message) {
        UUID tempToken;

        try {
            tempToken = UUID.fromString(message);
        } catch (IllegalArgumentException e) {
            // Handle the case where tempToken is not a valid UUID
            System.out.println(e);
            return false;
        }
        
        if( tempToken != null ){
            username = UserTokenFileHandler.getUsernameFromUUID(tempToken);
            if (username != null){
                server.getConnectedClients().put(tempToken,username);
                return true;
            } 
        }

        //if ( tempToken != null && server.getConnectedClients().containsKey(tempToken) ) return false;
        
        return false;
        
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


