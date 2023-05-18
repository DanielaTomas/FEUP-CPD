import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

//########################################## NAO SE PODE USAR COISAS DO "java.util.concurrent"!!!!!!
public class GameServer {

    private final int port;
    private final int maxThreads;
    private final ThreadPoolExecutor gameThreadPool;
    private final MyConcurrentHashMap<UUID, String> connectedClients;//second value is user token
    //private final ConcurrentHashMap<UUID, Socket> waitingClients;//second string is socket user is connected to
    private final MyConcurrentLinkedQueue<User> waitQueue;
    private final MyConcurrentHashMap<UUID, Game> playingGames;//second item is game instance

    public GameServer(int port, int maxThreads) {
        this.port = port;
        this.maxThreads = maxThreads;
        this.gameThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
        this.connectedClients = new MyConcurrentHashMap<>();
        //this.waitingClients = new ConcurrentHashMap<>();
        this.playingGames = new MyConcurrentHashMap<>();
        this.waitQueue = new MyConcurrentLinkedQueue<>();
    }

    public void sendToMenu(User user){
        ClientHandler handler = new ClientHandler(user, this);
        gameThreadPool.execute(handler);
        System.out.println("Sending :" + user.getName() + " back to the main menu");
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
                    System.out.println("ping");
                }
                

                if (waitQueue.size() >= 2) {
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
        Game gameInstance = new Game(this);

        for(int i = 0; i < waitQueue.size() || i < 2; i++){
            User currUser = waitQueue.poll();
            System.out.println("adding " + currUser.getName() + " to a game instance");
            gameInstance.addPlayer(currUser);
        }

        gameThreadPool.execute(gameInstance);

    }

    public boolean handleJoinQueue(User user){
        if (!waitQueue.contains(user)) {
            waitQueue.add(user);
            System.out.println("Added " + user.getName() + " to queue! ");
            return true;
        }
        return false;
    }

    public MyConcurrentHashMap<UUID, String> getConnectedClients(){
        return connectedClients;
    }

    public MyConcurrentLinkedQueue<User> getWaitingClients(){
        return waitQueue;
    }

    public MyConcurrentHashMap<UUID, Game> getPlayingGames(){//TODO: do we really need this
        return playingGames;
    }

    public static void main(String[] args) {
        int port = 8080;
        int maxThreads = 10;

        GameServer server = new GameServer(port, maxThreads);
        server.start();
    }

    // add methods to manipulate connectedClients, waitingClients, and playingGames maps
}
