import java.io.*;
import java.util.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


public class Game implements Runnable {
        private final HashMap<User, Integer> playingClients = new HashMap<>();//Integer represents score in current instance
        private GameServer server;
        private BufferedReader input;
        private PrintWriter output;
        List<String> words =new ArrayList<String>();  
        Random random = new Random();

        public void addPlayer(User user){
            playingClients.put(user,0);
        }

        public void sendMessageToPlayer(User user, String message) {
            Socket playerSocket = user.getSocket();
            if (playerSocket != null) {
                try {
                    PrintWriter out = new PrintWriter(playerSocket.getOutputStream(), true);
                    out.println(message);
                } catch (IOException e) {
                    System.out.println("Error sending message to player " +  user.getName() + ": " + e.getMessage());
                }
            } else {
                System.out.println("Player " + user.getName() + " is not connected.");
            }
        }

        public void broadcastMessage(String message) {
            for (Map.Entry<User, Integer> entry : playingClients.entrySet()) {//key is a user value is score
                try {
                    User user = entry.getKey();
                    Integer userScore = entry.getValue();
                    //System.out.println("Key: " + user.getName() + ", Value: " + value);
                    PrintWriter out = new PrintWriter(user.getSocket().getOutputStream(), true);
                    out.println(message);
                } catch (IOException e) {
                    // Handle any errors that may occur during communication
                    System.out.println("Error sending message to player : " + entry.getKey().getName() + " error:" + e.getMessage());
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
            

            this.onlineGameLoop();
        }

        public void onlineGameLoop() {
            String chosenWord = this.chooseWord(words, this.random);
            String shuffledWord = this.shuffleWord(chosenWord, this.random);
            this.broadcastMessage(MessageType.WORD_TO_GUESS+":"+shuffledWord);
            
            for (Map.Entry<User, Integer> entry : playingClients.entrySet()) {
                User user = entry.getKey();
                Integer userScore = entry.getValue();

            }

            Scanner scanner = new Scanner(System.in);

            String guess = this.convertToCapitalLetters(scanner.nextLine());
            MessageType verifyGuess = this.compareWords(guess, chosenWord);

            while (verifyGuess != MessageType.CORRECT_GUESS){//TODO CONTINUE HERE
                /*System.out.println("Your guess: " + guess + " is wrong. Please try again in another round.\n"); // TODO: Maybe Change
                System.out.println("-------------------------------------------");
                System.out.println("Guess the word: " + shuffledWord);*/
                guess = this.convertToCapitalLetters(scanner.nextLine());
                verifyGuess = this.compareWords(guess, chosenWord);
            }

            System.out.println("Correct word! Game Finished!\n");
        }
        

        @Override
        public void run() {
            System.out.println("Running game instance");
            for (Map.Entry<User, Integer> entry : playingClients.entrySet()) {
                User user = entry.getKey();
                Integer value = entry.getValue();
                System.out.println("Key: " + user.getName() + ", Value: " + value);
            }
            

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