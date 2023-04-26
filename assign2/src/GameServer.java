import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GameServer {

    private final int port;
    private final int maxGames;
    private final ThreadPoolExecutor gameThreadPool;
    private final ConcurrentHashMap<String, String> connectedClients;//second string is user token
    private final ConcurrentHashMap<String, Long> waitingClients;//second string is waiting time
    private final ConcurrentHashMap<String, Game> playingGames;//second item is game instance

    public GameServer(int port, int maxGames) {
        this.port = port;
        this.maxGames = maxGames;
        this.gameThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxGames);
        this.connectedClients = new ConcurrentHashMap<>();
        this.waitingClients = new ConcurrentHashMap<>();
        this.playingGames = new ConcurrentHashMap<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();

                // create a new client handler thread
                ClientHandler handler = new ClientHandler(socket,this);
                gameThreadPool.execute(handler);
            }

        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public ConcurrentHashMap<String, String> getConnectedClients(){
        return connectedClients;
    }

    public ConcurrentHashMap<String, Long> getWaitingClients(){
        return waitingClients;
    }

    public ConcurrentHashMap<String, Game> getPlayingGames(){//TODO: do we really need this
        return playingGames;
    }

    public static void main(String[] args) {
        int port = 8080;
        int maxGames = 5;

        GameServer server = new GameServer(port, maxGames);
        server.start();
    }

    // add methods to manipulate connectedClients, waitingClients, and playingGames maps
}
