import java.net.Socket;
import java.util.UUID;

public class User {
    private final Socket socket;
    private final UUID uuid;
    private final String name;
    private final int rankValue;//Unused for now

    public User(Socket socket,UUID uuid, String name, int rankValue){
        this.socket = socket;
        this.uuid = uuid;
        this.name = name;
        this.rankValue = rankValue;
    }

    public Socket getSocket(){
        return socket;
    }

    public UUID getUuid(){
        return uuid;
    }

    public String getName(){
        return name;
    }

    public int updateRank(int delta){
        int newRank = rankValue + delta;
        return newRank;
    }
}
