import java.io.*;
import java.util.*;
import java.util.Scanner;


public class Game {

        Random random = new Random();

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


        public static void main(String[] args) {
            testPlayGame();
        }

}