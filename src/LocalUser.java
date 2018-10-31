import java.util.ArrayList;

/**
 * This class abstracts the data and procedural components of the local user within the Mobile Cloud Computing
 * architecture defined within the paper.
 */
class LocalUser {
    
    private AccessPoint accessPoint;            // Reference to the Access Point
    
    private double LOCAL_CPU_RATE;                  // Cycles per second
    private double LOCAL_COMP_ENERGY_RATE;          // Jules per cycle
    private double LOCAL_TRANS_ENERGY_RATE;         // Jules per bit (up and down)
    private double LOCAL_TRANS_RATE;
    private ArrayList<Task> tasks;
    
    /**
     * This constructor initializes a new LocalUser with the specified values.
     *
     * @param ap   Access Point to forward tasks that are not computed locally
     * @param lcr  Local CPU Rate
     * @param lcer Local Computational Energy Rate
     * @param lter Local Transmission Energy Rate
     * @param ltr  Local Transmission Rate
     */
    LocalUser(AccessPoint ap, double lcr, double lcer, double lter, double ltr) {
        // Initializing...
        accessPoint = ap;
        tasks = null;
        LOCAL_CPU_RATE = lcr;
        LOCAL_COMP_ENERGY_RATE = lcer;
        LOCAL_TRANS_ENERGY_RATE = lter;
        LOCAL_TRANS_RATE = ltr;
    } // Constructor
    
    /**
     * Resolve each task. Try to compute the task locally, otherwise, send it to the access point.
     * Regardless of whether it is to be computed on the AP or RC, the AP must first receive it.
     *
     * @throws CustomException Indicates Program Error
     */
    void resolveTasks() throws CustomException {
        if (tasks == null) {
            throw new CustomException("ERROR: Local User attempted to resolve a  task list");
        }
        for (Task t : tasks) {              // For each task...
            if (t.getCompL()) {             // If task can be computed on Local User...
                t.processTask(LOCAL_COMP_ENERGY_RATE, LOCAL_CPU_RATE, 0); // Compute on Local User
            } else {
                t.sendToAP(LOCAL_TRANS_ENERGY_RATE, LOCAL_TRANS_RATE, LOCAL_TRANS_RATE);
                accessPoint.resolveTask(t); // Otherwise, pass task to Access Point
            }
        }
    } // resolveTasks
    
    // Setter
    void setTasks(ArrayList<Task> t) {
        tasks = t;
    } // setTasks
}