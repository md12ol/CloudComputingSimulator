/**
 * This class is meant to abstract the data and procedural components which relate to the remote cloud as outlined by
 * the paper.
 */

class RemoteCloud {
    
    private double RC_CPU_RATE;     // Cycles per second
    private double RC_PROC_CST;     // Constant related to processing task on RC
    
    /**
     * This constructor initializes a new Remote Cloud with the specified values.
     *
     * @param rcr Remote Cloud CPU Rate
     * @param b   Remote Cloud Processing Constant
     */
    RemoteCloud(double rcr, double b) {
        // Initializing...
        RC_CPU_RATE = rcr;
        RC_PROC_CST = b;
    } // Constructor
    
    /**
     * Resolve each task. Try to compute the task on the access point, otherwise, send it to the remote cloud.
     *
     * @param t Task to be resolved
     * @throws CustomException Indicates Program Error
     */
    void resolveTask(Task t) throws CustomException {
        // Compute on Remote Cloud
        t.processTask(0, RC_CPU_RATE, RC_PROC_CST);
    } // resolveTask
}
