class CustomException extends Exception {

    String message;

    CustomException(String errorMsg) {
        message = errorMsg;
    }

    void print() {
        System.out.println(message);
    }

}
