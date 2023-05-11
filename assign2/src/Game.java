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

        public static void main(String[] args) {
            //testPlayGame();
        }

}