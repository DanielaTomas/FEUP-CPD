import java.net.*;
import java.util.Scanner;
import java.util.UUID;
import java.io.*;

public class GameClient {

    
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
            fileName = "tokens/"+ userName + "token.txt";
            tokenFile = new File(fileName);
            System.out.println(tokenFile.getAbsolutePath());

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
                FileWriter fileWriter = new FileWriter(fileName);
                fileWriter.write(token);
                fileWriter.close();
                System.out.println("Token stored to save file.");
            }

            while (true){
                
                
                int numberOfLines = Integer.parseInt(input.readLine());
                if ( numberOfLines > 0 ){
                    for (int i = 0; i < numberOfLines; i++) {
                        String line = input.readLine();
                        System.out.println(line);
                    }
                }else if ( numberOfLines == 0){
                    System.out.println("send your answer");
                    output.println(scanner.nextLine());
                    System.out.println("answer sent");
                }else{
                    break;
                }
                
                
            }

            input.close();
            output.close();
            socket.close();

            
            /*
            */

            
 
        } catch (UnknownHostException ex) {
 
            System.out.println("Server not found: " + ex.getMessage());
 
        } catch (IOException ex) {
 
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}