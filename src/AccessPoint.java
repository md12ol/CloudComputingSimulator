/**
 * This class was created to abstract the data and procedural components which relate to the
 * (computational) access point as outlined within the paper.
 */

public class AccessPoint {

    LocalUser localUser;            // Reference to the Local User
    RemoteCloud remoteCloud;        // Reference to the Remote Cloud

    // TODO: Determine if this bool is necessary
    //  boolean computational;      // Whether or not the access point has computation resources
    double clockFreq;               // The speed of the CPU at the access point
    int numTasks;                   // The number of tasks being processed by the access point
    double transRate;               // The transmission rate of the access point with the remote cloud

    public AccessPoint(LocalUser lu, RemoteCloud rc) {
        localUser = lu;
        remoteCloud = rc;
    }

    public void resolveTask(Task t) {
        if (t.getCompAP()) {                 // If task can be computed on Access Point...
            computeTask(t);                 // Compute on Access Point
            returnTask(t);                  // Then return task to Local User
        } else {
            remoteCloud.resolveTask(t);     // Otherwise, pass task to Remote Cloud
        }
    }

    /**
     * Compute the task on the Access Point.
     * Math goes here.
     */
    public void computeTask(Task t) {
        // Compute the Task
    }

    /**
     * The task has been computed and has been passed back to the Access Point.
     * Pass the task back to the Local User.
     * Handle any cleanup here.
     */
    public void returnTask(Task t) {
        localUser.returnTask(t);
    }

}
