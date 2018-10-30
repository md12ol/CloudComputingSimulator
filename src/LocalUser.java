import java.util.ArrayList;

public class LocalUser {

    AccessPoint accessPoint;            // Reference to the Access Point
    private double LOCAL_CPU_RATE;                  // Cycles per second
    private double LOCAL_COMP_ENERGY_RATE;          // Jules per cycle
    private double LOCAL_TRANS_ENERGY_RATE;         // Jules per bit (up and down)
    private double LOCAL_TRANS_RATE;
    ArrayList<Task> tasks;

    public LocalUser(AccessPoint ap, ArrayList<Task> t, double lcr, double lcer, double lter, double ltr) {
        // Initializing...
        accessPoint = ap;
        tasks = t;
        LOCAL_CPU_RATE = lcr;
        LOCAL_COMP_ENERGY_RATE = lcer;
        LOCAL_TRANS_ENERGY_RATE = lter;
        LOCAL_TRANS_RATE = ltr;
    }

    /**
     * Loading a list of tasks into the Local User
     */
    public void loadTasks(ArrayList<Task> t) {
        tasks = t;
    }

    /**
     * Resolve each task. Try to compute the task locally, otherwise, send it to the access point.
     * Regardless of whether it is to be computed on the AP or RC, the AP must first receive it.
     */
    public void resolveTasks() throws CustomException {
        for (Task t : tasks) {              // For each task...
            if (t.getCompL()) {             // If task can be computed on Local User...
                t.processTask(LOCAL_COMP_ENERGY_RATE, LOCAL_CPU_RATE, 0);             // Compute on Local User
            } else {
                t.sendToAP(LOCAL_TRANS_ENERGY_RATE, LOCAL_TRANS_RATE, LOCAL_TRANS_RATE);
                accessPoint.resolveTask(t); // Otherwise, pass task to Access Point
            }
        }
    }
}