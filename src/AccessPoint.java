/**
 * This class was created to abstract the data and procedural components which relate to the
 * (computational) access point as outlined within the paper.
 */

public class AccessPoint {

    RemoteCloud remoteCloud;        // Reference to the Remote Cloud
    public double CAP_CPU_RATE;     // Cycles per second
    private double CAP_TRANS_RATE;  // Bits per second
    private double CAP_PROC_CST;    // Constant related to processing task on CAP

    public AccessPoint(RemoteCloud rc, double ccr, double ctr, double a) {
        // Initializing...
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
