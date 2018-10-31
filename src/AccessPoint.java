/**
 * This class was created to abstract the data and procedural components which relate to the computational access
 * point as outlined within the paper.
 */
class AccessPoint {
    
    private RemoteCloud remoteCloud;    // Reference to the Remote Cloud
    
    private double CAP_CPU_RATE;        // Cycles per second
    private double CAP_TRANS_RATE;      // Bits per second
    private double CAP_PROC_CST;        // Constant related to processing task on CAP
    
    /**
     * This constructor initializes a new Access Point with the specified values.
     *
     * @param rc  Remote Cloud to forward tasks that are not to be processed on the Access Point
     * @param ccr Access Point CPU Rate
     * @param ctr Access Point Transmission Rate
     * @param a   Access Point Processing Constant, from paper
     */
    AccessPoint(RemoteCloud rc, double ccr, double ctr, double a) {
        // Initializing...
        remoteCloud = rc;
        CAP_CPU_RATE = ccr;
        CAP_TRANS_RATE = ctr;
        CAP_PROC_CST = a;
    } // Constructor
    
    /**
     * Resolve each task. Try to compute the task on the access point, otherwise, send it to the remote cloud.
     *
     * @param t Task to be resolved
     * @throws CustomException Indicates Program Error
     */
    void resolveTask(Task t) throws CustomException {
        // If task can be computed on Access Point...
        if (t.getCompAP()) {
            // Compute on Access Point
            t.processTask(0, CAP_CPU_RATE, CAP_PROC_CST);
        } else {
            // Otherwise, pass task to Remote Cloud
            t.sendToRC(CAP_TRANS_RATE, CAP_TRANS_RATE);
            remoteCloud.resolveTask(t);
        }
    } // resolveTask
}
