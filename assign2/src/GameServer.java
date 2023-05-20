import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GameServer {

    private final int port;
    private static final int MAX_GAMES = 5;
    private static final int MIN_PLAYERS_PER_GAME = 2;
    private static final int MAX_PLAYERS_PER_GAME = 5;
    private final ThreadPoolExecutor gameThreadPool;
    private final MyConcurrentHashMap<UUID, String> connectedClients;//second value is user token
    private final Map<UUID, Integer> QueuePositions;//second string is socket user is connected to
    private final MyConcurrentLinkedQueue<User> waitQueue;

    public GameServer(int port) {
        this.port = port;
        this.gameThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_GAMES);
        this.connectedClients = new MyConcurrentHashMap<>();
        this.QueuePositions = new HashMap<>();
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
        Game gameInstance = null;
        int playersToAdd = 0;
        List<UUID> players = new LinkedList<>();
        synchronized (this) {
            playersToAdd = Math.min(waitQueue.size(), MAX_PLAYERS_PER_GAME);
            gameInstance = new Game(this,playersToAdd);
            for (int i = 0; i < playersToAdd; i++) {
                User currUser = waitQueue.poll();
                System.out.println("Adding " + currUser.getName() + " to a game instance");
                gameInstance.addPlayer(currUser);
                players.add(i, currUser.getUuid());
            }
        if(gameInstance != null){
            gameThreadPool.execute(gameInstance);
            System.out.println("Updating queue");


                Iterator<Map.Entry<UUID, Integer>> iterator = QueuePositions.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<UUID, Integer> entry = iterator.next();
                    UUID playerId = entry.getKey();
                    int currentPosition = entry.getValue();
            
                    if (players.contains(playerId)) {
                        iterator.remove();  // Safely remove the entry using the iterator's remove() method
                    } else {
                        int newPosition = currentPosition - playersToAdd;
                        QueuePositions.put(playerId, newPosition);
                    }
                }
            }
        }
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
                waitQueue.set(queuePosition - 1, user);
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

    public int getQueueSize(){
        return waitQueue.size();
    }

    public Integer getQueuePosition(UUID token){
        if ( QueuePositions.containsKey(token) ) return QueuePositions.get(token);
        return null;
    }

    public MyConcurrentLinkedQueue<User> getWaitingClients(){
        return waitQueue;
    }

    public static void main(String[] args) {
        int port = 8080;

        GameServer server = new GameServer(port);
        try{
            server.start();
        }catch (Exception e){
            System.out.println("Server error: " + e.getMessage());
        }finally{
            //server.shutdown();
        }
    }
}
