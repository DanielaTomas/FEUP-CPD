import java.net.*;
import java.util.Scanner;
import java.util.UUID;
import java.io.*;

public class GameClient {
    BufferedReader input;
    PrintWriter output;
    String userName;
    UUID userToken;
    String hostname;
    int port;

    Game game;

    public GameClient(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }

    public void handleAuthentication(String userName){
        try{
            String fileName = "tokens/"+ userName + "token.txt";
            File tokenFile = new File(fileName);

            if( tokenFile.exists() ){
                System.out.println("Attempting to login as existing user... ");
                BufferedReader reader = new BufferedReader(new FileReader(tokenFile)) ;

                output.println(MessageType.LOGIN + ":" + reader.readLine());

            }else{
                System.out.println("No token found...");

                output.println(MessageType.REGISTRATION + ":" +userName);//send username to server
            }
            
        }catch(IOException e){
            System.out.println("I/O error: " + e.getMessage());
        }
    }

    public void handleRegistration(String token){
        try{
            String fileName = "tokens/"+ userName + "token.txt";
            
            userToken = UUID.fromString(token);
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(token);
            fileWriter.close();
            System.out.println("Saving token...");
            output.println(MessageType.SUCCESS);
        }catch(IOException e){
            System.out.println("I/O error: " + e.getMessage());
        }
    }
    
    public void run() {
        Scanner scanner = new Scanner(System.in);
 
        try (Socket socket = new Socket(hostname, port)) {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            while (true){
                String response = input.readLine();
                String messageContent = null;
                if(response != null){
                    String[] parts = response.split(":",2);
                    MessageType message = MessageType.valueOf(parts[0]);
                    if (parts.length ==2){
                        messageContent = parts[1];
                    }

                                        
                    //if (message == MessageType.QUEUE_JOIN_SUCESS) break;

                    switch (message){
                        case WELCOME:
                            System.out.println("Enter username:");
                            userName = scanner.nextLine();
                            this.handleAuthentication(userName);
                            break;
                        case AUTHENTICATION_SUCESS:
                            System.out.println("Login attempt was successful, welcome back " + userName + "!");
                            break;
                        case AUTHENTICATION_FAILURE:
                            output.println(MessageType.REGISTRATION + ":" + userName);
                            System.out.println("Login attempt was unsuccessful...");
                            System.out.println("Attempting to register...");
                            break;
                        case AUTHENTICATION_RESPONSE:
                            if( messageContent != null){
                                this.handleRegistration(messageContent);
                                System.out.println("Registration was sucessfull, hello " + userName + "!");
                            }else{
                                System.out.println("Registration was unsucessfull...");
                            }
                            break;
                        case MAIN_MENU_PICK_OPTION:
                            System.out.println("Select an option:\n" +
                                                "1. Find an opponent\n" +
                                                "2. Quit\n" +
                                                "3. Play Alone" );
                            int option = scanner.nextInt();
                            if (option == 1){
                                output.println(MessageType.JOIN_QUEUE);
                            }else if (option == 2){
                                output.println(MessageType.QUIT);
                                output.close();
                                input.close();
                                scanner.close();
                                socket.close();
                            } else if (option == 3){//SINGLE PLAYER
                                game.testPlayGame();
                                //output.println(MessageType.JOIN_QUEUE);
                            }
                            break;
                        case QUEUE_JOIN_SUCESS:
                            System.out.println("Joined Queue, there are currently: " + messageContent + " players in the queue");
                            break;
                        case QUEUE_POSITION:
                            System.out.println("You are currently in position : " + messageContent + " of the queue");
                            break;
                        case GAME_START:
                            this.interfaceWithGame(socket);
                            break;
                        default:
                            System.out.println("New message type detected: " + message);
                    }

                }
            }
            
 
        } catch (UnknownHostException ex) {
 
            System.out.println("Server not found: " + ex.getMessage());
 
        } catch (IOException ex) {
 
            System.out.println("I/O error: " + ex.getMessage());
        }finally{
            scanner.close();
            //input.close();
            output.close();
            //socket.close();
        }
    }

    public void interfaceWithGame(Socket socket){
        Scanner scanner = new Scanner(System.in);
 
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            int score;
            boolean gameOver = false;

            System.out.println("-------------------------------------------");
            System.out.println("Game Starting!");
            System.out.println("-------------------------------------------");


            while (!gameOver){//TODO: CONTINUE HERE
                String response = input.readLine();
                String messageContent = null;
                if(response != null){
                    String[] parts = response.split(":",2);
                    MessageType message = MessageType.valueOf(parts[0]);
                    if (parts.length ==2){
                        messageContent = parts[1];
                    }

                     switch (message){
                        case WORD_TO_GUESS:
                            System.out.println("Shuffled word: " + messageContent);
                            System.out.println("Enter your guess: ");
                            String guessString  = scanner.nextLine();
                            output.println(MessageType.GUESS_ATTEMPT + ":" + guessString);
                            break;
                        case CORRECT_GUESS:
                            System.out.println("You guessed correctly!");
                            break;
                        case INCORRECT_GUESS:
                            System.out.println("Wrong! You guessed incorrectly...");
                            System.out.println("The correct word was:" + messageContent);
                            break;
                        case GAME_OVER:
                            gameOver = true;
                            System.out.println("Game Over!");
                            break;
                        default:
                            System.out.println("Received a non-useful message for this scenario:" + message);
                        }
                    }

                }
        }
        catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }finally{
            //scanner.close();
            //input.close();
            //output.close();
            //socket.close();
            System.out.println("Going back to the menu...");
            System.out.println("-------------------------------------------");
        }
                    
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) return;
        GameClient client = new GameClient(args[0], Integer.parseInt(args[1]));
        client.run();
    }
}