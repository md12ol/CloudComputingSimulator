/**
 * This class is meant to abstract the data and procedural components which relate to the remote
 * cloud as outlined by the paper.
 */

public class RemoteCloud {

    private double RC_CPU_RATE;     // Cycles per second
    private double RC_PROC_CST;     // Constant related to processing task on RC

    public RemoteCloud(double rcr, double b) {
        // Initializing...
        RC_CPU_RATE = rcr;
        RC_PROC_CST = b;
    }

    public void resolveTask(Task t) throws CustomException {
        // Compute on Remote Cloud
        t.processTask(0, RC_CPU_RATE, RC_PROC_CST);
    }
}
