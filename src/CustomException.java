/**
 * This class implements a custom exception used for outputting error information should an error occur.
 */
class CustomException extends Exception {
    
    private String message;
    
    /**
     * This constructor creates a new exception.
     *
     * @param errorMsg Error message
     */
    CustomException(String errorMsg) {
        message = errorMsg;
    } // Constructor
    
    /**
     * Prints the error message to the user.
     */
    void print() {
        System.out.println(message);
    } // print
}
