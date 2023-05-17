public enum MessageType {
    // Messages sent by the server
    WELCOME, // Sent when a client first connects to the server, client responds either with a name or a token
    AUTHENTICATION_RESPONSE, //Sent when the user requests a new token
    AUTHENTICATION_SUCESS, //Sent when the user is recognized
    AUTHENTICATION_FAILURE,
    MAIN_MENU_PICK_OPTION, //Sent when the user has to pick an option of the main menu
    MAIN_MENU_INVALID_OPTION, //Sent when the option is picked is invalid
    WORD_TO_GUESS, // Sent to tell the client what word they need to guess
    CORRECT_GUESS, // Sent to inform the client that their guess was correct
    INCORRECT_GUESS, // Sent to inform the client that their guess was incorrect
    GAME_OVER, // Sent when the game is over
    
    // Messages sent by the client
    REGISTRATION, // sent when the user requests a new token (new user)
    LOGIN, // Sent when the user tries sending a token
    SUCCESS,
    JOIN_QUEUE,
    GUESS, // Sent to make a guess for the current word
    QUIT // Sent to quit the game
}
