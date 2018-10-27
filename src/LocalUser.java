import java.util.ArrayList;

public class LocalUser {

    AccessPoint accessPoint;        // Reference to the Access Point

    double uploadRate;              // The upload capacity of the mobile device (in what unit?)
    double downloadRate;            // The download capacity of the mobile device (in what unit?)

    ArrayList<Task> tasks;

    public LocalUser(AccessPoint ap, ArrayList<Task> t) {
        accessPoint = ap;           // Initializing reference to Access Point
        tasks = t;                  // Initializing reference to array of tasks
    }

    /**
     * Resolve each task. Try to compute the task locally, otherwise, send it to the access point.
     * Regardless of whether it is to be computed on the AP or RC, the AP must first receive it.
     */
    public void resolveTasks() {
        for (Task t : tasks) {              // For each task...
            if (t.getCompL()) {              // If task can be computed on Local User...
                computeTask(t);             // Compute on Local User
                returnTask(t);              // Then return task to Local User
            } else {
                accessPoint.resolveTask(t); // Otherwise, pass task to Access Point
            }
        }
    }

    /**
     * Compute the task on the Local User.
     * Math goes here.
     */
    public void computeTask(Task t) {
        // Compute the Task
    }

    /**
     * The task has been computed and has been passed back to the Local User.
     * Handle any cleanup here.
     */
    public void returnTask(Task t) {
        // Task is completed. User gets result.
    }
}