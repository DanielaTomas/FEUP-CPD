import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserTokenFileHandler {
    private static final String FILENAME = "tokens/server_user_tokens.txt";
    
    public static void addUserTokenPair(UUID token, String username) {
        try {
            System.out.println("WRITING");
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME, true));
            writer.write(username + ":" + token + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public static String getUsernameFromUUID(UUID uuid) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILENAME));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String username = parts[0];
                UUID token = UUID.fromString(parts[1]);
                if (token.equals(uuid)) {
                    reader.close();
                    return username;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return null; // UUID not found in file
    }
    
    
    public static Map<UUID, String>  getUserTokenPairs() {
        Map<UUID, String>  userTokens = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILENAME));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String username = parts[0];
                UUID token = UUID.fromString(parts[1]);
                userTokens.put(token, username);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return userTokens;
    }
}
