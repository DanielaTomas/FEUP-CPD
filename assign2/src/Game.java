import java.io.*;
import java.util.*;

// TODO: APAGAR
import java.util.Scanner;


public class Game {

        Random random = new Random();
        


        // Choose a random word from the list of words
        public String chooseWord(List<String> words, Random random) { 
            int wordsLength = words.size();
            int randomIndex =  random.nextInt(wordsLength);

            return words.get(randomIndex);
        }


        // Convert all lettters in a client word guess to capital letters 
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
            
            // TODO: For cases where the resulting word is equal to ther one given, not working  
            if (resultWord == word){
                shuffleWord(word, new Random());
            }

            return resultWord;
        }

        public MessageType compareWords(String guess, String correctWord){
            MessageType response;
            if (guess == correctWord){
                response = MessageType.CORRECT_GUESS;
            } else {
                response = MessageType.INCORRECT_GUESS;
            }
            return response;
        }

        // TODO: corrigir game loop quando a palavra tiver correta
        public void gameLoop(String chosenWord, String shuffledWord) {
            Game gameObject = new Game();

            // TODO: APAGAR
            Scanner myObj = new Scanner(System.in);

            String guess = gameObject.convertToCapitalLetters(myObj.nextLine());
            MessageType verifyGuess = gameObject.compareWords(guess, chosenWord);

            while (verifyGuess != MessageType.CORRECT_GUESS){
                System.out.println("Your guess: " + guess + " is wrong. Please try again in another round"); // TODO: Maybe Change
                System.out.println("-------------------------------------------\n");
                System.out.println("Shuffled word: " + shuffledWord);
                guess = gameObject.convertToCapitalLetters(myObj.nextLine());
            }

            System.out.println("Game Finished");
        }

        public static void testPlayGame() {
            Game gameObject = new Game();

            // TODO: Find a better place to store the words 
            List<String> words =new ArrayList<String>();  
            words.add("FOOD");  
            words.add("OI");  
            words.add("DEPRESSION");  
            words.add("COMPUTER");  


            String chosenWord = gameObject.chooseWord(words, gameObject.random);
            String shuffledWord = gameObject.shuffleWord(chosenWord, gameObject.random);

            //System.out.println(chosenWord);
            
            System.out.println("-------------------------------------------\n");
            System.out.println("Shuffled word: " + shuffledWord);

            System.out.println("\n");
            System.out.println("Enter your guesses:\n");

            gameObject.gameLoop(chosenWord, shuffledWord);

        }




        public static void main(String[] args) {
            testPlayGame();
        }

}