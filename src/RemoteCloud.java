/**
 * This class is meant to abstract the data and procedural components which relate to the remote
 * cloud as outlined by the paper.
 */

public class RemoteCloud {

    AccessPoint accessPoint;        // Reference to the Access Point

    double clockFreq;               // The speed of the CPU within the remote cloud

    private double RC_CPU_RATE;     // Cycles per second
    private double RC_PROC_CST;     // Constant related to processing task on RC

    public RemoteCloud(AccessPoint ap, double rcr, double b) {
        // Initializing...
        accessPoint = ap;
        RC_CPU_RATE = rcr;
        RC_PROC_CST = b;
    }

    public void resolveTask(Task t) throws CustomException {   // Compute task
        // Compute on Remote Cloud
        t.processTask(0, RC_CPU_RATE, RC_PROC_CST);
    }
}
