import java.io.*;
import java.util.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Game implements Runnable {
        private final HashMap<User, Integer> playingClients = new HashMap<>();//Integer represents score in current instance
        private GameServer server;
        //private BufferedReader input;
        //private PrintWriter output;
        List<String> words =new ArrayList<String>();  
        Random random = new Random();

        private Game(){}

        public Game (GameServer gameServer){
            this.server = gameServer;
        }

        public void addPlayer(User user){
            playingClients.put(user,0);
        }

        public void sendMessageToPlayer(User user,MessageType messageType, String messageContent) {
            Socket playerSocket = user.getSocket();
            if (playerSocket != null) {
                try {
                    PrintWriter output = new PrintWriter(playerSocket.getOutputStream(), true);
                    System.out.println("Sending single message to : " + user.getName() );
                    if (messageContent != null) output.println(messageType + ":" + messageContent);
                    else output.println(messageType);
                } catch (IOException e) {
                    System.out.println("Error sending message to player " +  user.getName() + ": " + e.getMessage());
                }
            } else {
                System.out.println("Player " + user.getName() + " is not connected.");
            }
        }

        public void broadcastMessage(MessageType messageType, String messageContent) {
            for (Map.Entry<User, Integer> entry : playingClients.entrySet()) {//key is a user value is score
                try {
                    User user = entry.getKey();
                    Integer userScore = entry.getValue();
                    System.out.println("Sending broadcast message to : " + user.getName() );
                    PrintWriter output = new PrintWriter(user.getSocket().getOutputStream(), true);
                    if (messageContent != null) output.println(messageType + ":" + messageContent);
                    else output.println(messageType);
                } catch (IOException e) {
                    // Handle any errors that may occur during communication
                    System.out.println("Error sending message to player : " + entry.getKey().getName() + "  while broadcasting, error:" + e.getMessage());
                }
            }
        }

        public void testPlayGameMultiplayer(){
            //Game gameObject = new Game();

            // TODO: Find a better place to store the words, maybe 
            words.add("FOOD");
            words.add("DEPRESSION");  
            words.add("COMPUTER");  
            words.add("CRINGE");  
            words.add("GUESS");
            words.add("WORD");
            

            //System.out.println(chosenWord);
            

            for(int i = 0; i < 2; i++) this.onlineGameLoop();

            this.broadcastMessage(MessageType.GAME_OVER, null);

            for (Map.Entry<User, Integer> entry : playingClients.entrySet()) {
                this.server.sendToMenu(entry.getKey());
            }

             //CRIAR CLIENT HANDLER AQUI

            //this.broadcastMessage(MessageType.MAIN_MENU_PICK_OPTION, null);

        }

        public void onlineGameLoop() {//TODO:jogo esta a enviar mensagem do tipo CORRECT OU INCORRECT depois de voltar para o main menu
            String chosenWord = this.chooseWord(words, this.random);
            String shuffledWord = this.shuffleWord(chosenWord, this.random);
            this.broadcastMessage(MessageType.GAME_START,null);
            //this.broadcastMessage(MessageType.WORD_TO_GUESS , shuffledWord);
            
            /*for (Map.Entry<User, Integer> entry : playingClients.entrySet()) {
                User user = entry.getKey();
                Integer userScore = entry.getValue();
            }*/

            this.broadcastMessage(MessageType.WORD_TO_GUESS, shuffledWord);
            ExecutorService executorService = Executors.newFixedThreadPool(playingClients.size());
            CountDownLatch answerLatch = new CountDownLatch(playingClients.size());
            try {
                for (Map.Entry<User, Integer> entry : playingClients.entrySet()) {
                    executorService.execute(() -> {
                        try {
                            User user = entry.getKey();
                            Socket socket = user.getSocket();
            
                            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            
                            String response = input.readLine();
                            String messageContent = null;
                            if (response != null) {
                                String[] parts = response.split(":", 2);
                                MessageType message = MessageType.valueOf(parts[0]);
                                if (parts.length == 2) {
                                    messageContent = parts[1];
                                }
                                
                                if (message == MessageType.GUESS_ATTEMPT){
                                    String guessAttempt = this.convertToCapitalLetters(messageContent);
                                    MessageType verifyGuess = this.compareWords(guessAttempt, chosenWord);
                                    this.sendMessageToPlayer(user, verifyGuess, chosenWord);
                                }

                                answerLatch.countDown();
            
                                
                            }
                        } catch (IOException e) {
                            System.out.println("Error on game loop: " + e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                System.out.println("Error start game outside for: " + e.getMessage());
            }

            executorService.shutdown();

            try {
                // Wait for all threads to finish 
                boolean allAnswered  = answerLatch.await(30, TimeUnit.SECONDS);
                if (!allAnswered ) {
                    // Timeout occurred, handle the situation accordingly
                    System.out.println("Timeout: Some players have not given their answers.");
                }else{
                    System.out.println("All players have given their answers.");
                }
            } catch (InterruptedException e) {
                System.out.println("Error waiting for game loop to finish: " + e.getMessage());
            }

        }
        

        @Override
        public void run() {
            System.out.println("Running game instance");
            for (Map.Entry<User, Integer> entry : playingClients.entrySet()) {
                User user = entry.getKey();
                Integer value = entry.getValue();
                System.out.println("Key: " + user.getName() + ", Value: " + value);
            }

            this.testPlayGameMultiplayer();
            

            //testPlayGame();
        }
        

        // Choose a random word from the list of words
        public String chooseWord(List<String> words, Random random) { 
            int wordsLength = words.size();
            int randomIndex =  random.nextInt(wordsLength);

            return words.get(randomIndex);
        }


        // Convert all lettters in the guessed to capital letters -> so that it is case insensitive 
        public String convertToCapitalLetters (String word) {
            return word.toUpperCase();
        }


        public String shuffleWord(String word, Random random) {
            // Convert the string into an array of chars
            char letters[] = word.toCharArray();

            // Using Fisher-Yates algorithm to shuffle the word
            for (int i=0; i < letters.length; i++){
                int j = random.nextInt(letters.length);
                char temp = letters[i];
                letters[i] = letters[j];
                letters[j] = temp;
            }

            String resultWord = new String(letters); 
            
            // For cases when the shuffled word is equals to the original word
            if (resultWord.equals(word)){
                resultWord = shuffleWord(word, new Random());
            }

            return resultWord;
        }

        public MessageType compareWords(String guess, String correctWord){
            MessageType response;
            if (guess.equals(correctWord)){
                response = MessageType.CORRECT_GUESS;
            } else {
                response = MessageType.INCORRECT_GUESS;
            }
            return response;
        }

        public void gameLoop(String chosenWord, String shuffledWord) {
            Game gameObject = new Game();

            Scanner myObj = new Scanner(System.in);

            String guess = gameObject.convertToCapitalLetters(myObj.nextLine());
            MessageType verifyGuess = gameObject.compareWords(guess, chosenWord);

            while (verifyGuess != MessageType.CORRECT_GUESS){
                System.out.println("Your guess: " + guess + " is wrong. Please try again in another round.\n"); // TODO: Maybe Change
                System.out.println("-------------------------------------------");
                System.out.println("Guess the word: " + shuffledWord);
                guess = gameObject.convertToCapitalLetters(myObj.nextLine());
                verifyGuess = gameObject.compareWords(guess, chosenWord);
            }

            System.out.println("Correct word! Game Finished!\n");
        }

        // TODO: Change function name when it is fully implemented 
        public static void testPlayGame() {
            Game gameObject = new Game();

            // TODO: Find a better place to store the words, maybe 
            List<String> words =new ArrayList<String>();  
            words.add("FOOD");
            words.add("DEPRESSION");  
            words.add("COMPUTER");  
            words.add("CRINGE");  
            words.add("GUESS");
            words.add("WORD");


            String chosenWord = gameObject.chooseWord(words, gameObject.random);
            String shuffledWord = gameObject.shuffleWord(chosenWord, gameObject.random);

            //System.out.println(chosenWord);
            
            System.out.println("-------------------------------------------");
            System.out.println("Shuffled word: " + shuffledWord);
            System.out.println("Enter your guesses: "); // TODO: Maybe change when every client has to guess

            gameObject.gameLoop(chosenWord, shuffledWord);

        }

        /*public static void main(String[] args) {
            testPlayGame();
        }*/

}