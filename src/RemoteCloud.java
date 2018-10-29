/**
 * This class is meant to abstract the data and procedural components which relate to the remote
 * cloud as outlined by the paper.
 */

public class RemoteCloud {

    AccessPoint accessPoint;        // Reference to the Access Point

    double clockFreq;               // The speed of the CPU within the remote cloud

    private double RC_CPU_RATE;     // Cycles per second

    public RemoteCloud(AccessPoint ap, double rcr) {
        // Initializing...
        accessPoint = ap;
        RC_CPU_RATE = rcr;
    }

    public void resolveTask(Task t) throws CustomException {   // Compute task
        // Compute on Remote Cloud
        t.processTask(0, RC_CPU_RATE);
    }
}
