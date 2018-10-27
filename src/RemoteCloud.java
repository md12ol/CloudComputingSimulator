/**
 * This class is meant to abstract the data and procedural components which relate to the remote
 * cloud as outlined by the paper.
 */

public class RemoteCloud {

    AccessPoint accessPoint;        // Reference to the Access Point

    double clockFreq;               // The speed of the CPU within the remote cloud

    public RemoteCloud(AccessPoint ap) {
        accessPoint = ap;
    }

    public void resolveTask(Task t) {   // Compute task
        if (t.getCompRC()) {            // If task can be computed on Remote Cloud (should always be yes)
            computeTask(t);             // Compute on Remote Cloud
            returnTask(t);              // Then return to Access Point
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
     * The task has been computed.
     * Pass the task back to the Access Point.
     * Handle any cleanup here.
     */
    public void returnTask(Task t) {
        // Send task back (for completion sake)
        // Do any transfer calculations
        accessPoint.returnTask(t);
    }
}
