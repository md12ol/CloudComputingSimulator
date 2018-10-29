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
    double TRANS_RATE;              // The transmission rate of the access point with the remote cloud

    private double CAP_CPU_RATE;    // Cycles per second
    private double CAP_TRANS_RATE;  // Bits per second
    private double CAP_PROC_CST;    // Constant related to processing task on CAP

    public AccessPoint(LocalUser lu, RemoteCloud rc, double ccr, double ctr, double a) {
        // Initializing...
        localUser = lu;
        remoteCloud = rc;
        CAP_CPU_RATE = ccr;
        CAP_TRANS_RATE = ctr;
        CAP_PROC_CST = a;
    }

    public void resolveTask(Task t) throws CustomException {
        // If task can be computed on Access Point...
        if (t.getCompAP()) {
            // Compute on Access Point
            t.processTask(0, CAP_CPU_RATE, CAP_PROC_CST);
        } else {
            // Otherwise, pass task to Remote Cloud
            t.sendToRC(CAP_TRANS_RATE, CAP_TRANS_RATE);
            remoteCloud.resolveTask(t);
        }
    }
}
