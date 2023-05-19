import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GameServer {

    private final int port;
    private static final int MAX_GAMES = 5;
    private static final int MIN_PLAYERS_PER_GAME = 5;
    private final ThreadPoolExecutor gameThreadPool;
    private final MyConcurrentHashMap<UUID, String> connectedClients;//second value is user token
    private final MyConcurrentHashMap<UUID, Integer> QueuePositions;//second string is socket user is connected to
    private final MyConcurrentLinkedQueue<User> waitQueue;
    private final MyConcurrentHashMap<UUID, Game> playingGames;//second item is game instance

    public GameServer(int port) {
        this.port = port;
        this.gameThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_GAMES);
        this.connectedClients = new MyConcurrentHashMap<>();
        this.QueuePositions = new MyConcurrentHashMap<>();
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
                

                if (waitQueue.size() >= MIN_PLAYERS_PER_GAME) {
                    System.out.println("Starting a new match... ");
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

    public boolean checkIfQueued(UUID token){//TODO: IMPLEMENT
        if (QueuePositions.containsKey(token)) {
            return true;
        } 
        return false;
    }
    
    public boolean rejoinQueue(User user, Socket newSocket){
        Integer queuePosition = QueuePositions.get(user.getUuid());

        if (queuePosition != null) {
            synchronized(waitQueue) {
                waitQueue.set(queuePosition, user);
                return true;
            }
        }
        return false;
    }

    public boolean handleJoinQueue(User user){
        synchronized(this){//block other threads when joining queue
            if (!waitQueue.contains(user)) {
                waitQueue.add(user);
                QueuePositions.put(user.getUuid(), waitQueue.size());
                System.out.println("Added " + user.getName() + " to queue! ");
                return true;
            } 
        }
        return false;
    }

    public void shutdown() {
        gameThreadPool.shutdown();

        try {
            // Wait for the thread pools to terminate
            gameThreadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Handle interruption exception
            e.printStackTrace();
        }
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

        GameServer server = new GameServer(port);
        try{
            server.start();
        }catch (Exception e){
            System.out.println("Server error: " + e.getMessage());
        }finally{
            server.shutdown();
        }
    }
}
