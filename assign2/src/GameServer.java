import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

//########################################## NAO SE PODE USAR COISAS DO "java.util.concurrent"!!!!!!
public class GameServer {

    private final int port;
    private final int maxGames;
    private final ThreadPoolExecutor gameThreadPool;
    private final ConcurrentHashMap<UUID, String> connectedClients;//second value is user token
    //private final ConcurrentHashMap<UUID, Socket> waitingClients;//second string is socket user is connected to
    private final ConcurrentLinkedQueue<User> waitQueue;
    private final ConcurrentHashMap<UUID, Game> playingGames;//second item is game instance

    public GameServer(int port, int maxGames) {
        this.port = port;
        this.maxGames = maxGames;
        this.gameThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxGames);
        this.connectedClients = new ConcurrentHashMap<>();
        //this.waitingClients = new ConcurrentHashMap<>();
        this.playingGames = new ConcurrentHashMap<>();
        this.waitQueue = new ConcurrentLinkedQueue<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(5000);
            System.out.println("Server is listening on port " + port);

            while (true) {
                try {
                    Socket socket = serverSocket.accept();

                    // create a new client handler thread
                    ClientHandler handler = new ClientHandler(socket,this);
                    gameThreadPool.execute(handler);
                }catch  (SocketTimeoutException ignored) {
                    System.out.println("hihi");
                }
                

                if (waitQueue.size() >= 3) {
                    System.out.println("Attempting ");
                    this.startGame();
                }
            }

        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void startGame(){
        Game gameInstance = new Game();

        for(int i = 0; i < waitQueue.size() || i < 3; i++){
            User currUser = waitQueue.poll();
            System.out.println("adding " + currUser.getName() + " to a game instance");
            gameInstance.addPlayer(currUser);
        }

        gameThreadPool.execute(gameInstance);

    }

    public boolean handleJoinQueue(User user){
        if (!waitQueue.contains(user)) {
            waitQueue.add(user);
            return true;
        }
        return false;
    }

    public ConcurrentHashMap<UUID, String> getConnectedClients(){
        return connectedClients;
    }

    /*public ConcurrentHashMap<UUID, Socket> getWaitingClients(){
        return waitingClients;
    }*/

    public ConcurrentHashMap<UUID, Game> getPlayingGames(){//TODO: do we really need this
        return playingGames;
    }

    public static void main(String[] args) {
        int port = 8080;
        int maxGames = 10;

        GameServer server = new GameServer(port, maxGames);
        server.start();
    }

    // add methods to manipulate connectedClients, waitingClients, and playingGames maps
}
