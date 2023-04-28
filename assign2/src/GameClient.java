import java.net.*;
import java.util.Scanner;
import java.util.UUID;
import java.io.*;

public class GameClient {

    

    /*private void parseAndStoreToken(String serverMessage) {
        try {
            String token = serverMessage.substring(serverMessage.indexOf(":") + 2);
            userToken = UUID.fromString(token);
            FileWriter fileWriter = new FileWriter(SAVE_FILE_PATH);
            fileWriter.write(token);
            fileWriter.close();
            System.out.println("Token stored to save file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    
    public static void main(String[] args) {
        if (args.length < 2) return;
 
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        BufferedReader input;
        PrintWriter output;
        String userName;
        UUID userToken;
        Scanner scanner = new Scanner(System.in);
 
        try (Socket socket = new Socket(hostname, port)) {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            String serverMessage;
            String fileName;
            File tokenFile;
            
            
            serverMessage = input.readLine();//read welcome message from server
            System.out.println(serverMessage);//print to console
            
            userName = scanner.nextLine();
            fileName = userName + "token.txt";
            tokenFile = new File(fileName);

            if( tokenFile.exists() ){
                System.out.println("FILE EXISTS");
                BufferedReader reader = new BufferedReader(new FileReader(tokenFile)) ;

                output.println(reader.readLine());

            }else{
                System.out.println("FILE DOESNT EXIST");

                output.println(userName);//send username to server

                serverMessage = input.readLine();//server sends sucess message and token
                System.out.println(serverMessage);

                String token = serverMessage.substring(serverMessage.indexOf(":") + 2);// parse token from text
                userToken = UUID.fromString(token);
                FileWriter fileWriter = new FileWriter( (userName + "token.txt") );
                fileWriter.write(token);
                fileWriter.close();
                System.out.println("Token stored to save file.");
            }

            
            /*
            output.println(scanner.nextLine());

            serverMessage = input.readLine();
            System.out.println(serverMessage);*/

            
 
        } catch (UnknownHostException ex) {
 
            System.out.println("Server not found: " + ex.getMessage());
 
        } catch (IOException ex) {
 
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}