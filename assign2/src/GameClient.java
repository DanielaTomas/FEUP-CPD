import java.net.*;
import java.util.Scanner;
import java.io.*;

public class GameClient {
    private static final String SAVE_FILE_PATH = "save.txt";//TODO: maybe concat username to filename

    private void parseAndStoreToken() {
        try {
            FileWriter fileWriter = new FileWriter(SAVE_FILE_PATH);
            fileWriter.write(token);
            fileWriter.close();
            System.out.println("Token stored to save file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        if (args.length < 2) return;
 
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        BufferedReader input;
        PrintWriter output;
        String serverMessage;
        Scanner scanner = new Scanner(System.in);
 
        try (Socket socket = new Socket(hostname, port)) {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            
            
            serverMessage = input.readLine();//read message from server
            System.out.println(serverMessage);//print to console
            
            output.println(scanner.nextLine());//send text written on console to server


            serverMessage = input.readLine();
            System.out.println(serverMessage);
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