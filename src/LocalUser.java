public class LocalUser {

    public LocalUser() {
    }

    public void runTask(Task t) {
        // Any modifications that need to be made to task to calculare local shit
        if (t.compL) { // Locally

        } else if (t.compAP) { // Access Point
            // Do stuff
            // Send to AP
        } else { // Remote Cloud
            // Do stuff
            // Send to AP
        }
    }

}