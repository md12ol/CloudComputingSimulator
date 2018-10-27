import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.pow;

/**
 * COSC 5P06 Project. October 31st 2018. By Tyler Cowan (5635784) and Michael Dubé (5243845).
 * <p>
 * This class was created to handle the simulation of a Mobile Cloud Computing architecture which
 * includes a single mobile device, a single mobile access point, and a single remote cloud server.
 * The purpose to simulate this architecture is to test whether the inclusion of computation
 * resources at the access point, which can process tasks from the mobile device, has better
 * performance than a cloud computing system with only the remote cloud for execution.
 */

class Main {

    // Task Constants
    private static final double MAX_INPUT_SIZE = 30 * 8 * pow(10, 6);               // Bits
    private static final double MIN_INPUT_SIZE = 10 * 8 * pow(10, 6);               // Bits
    private static final double MAX_OUTPUT_SIZE = 3 * 8 * pow(10, 6);               // Bits
    private static final double MIN_OUTPUT_SIZE = 1 * 8 * pow(10, 6);               // Bits
    private static final double APP_CYCLES_PER_BIT = 1900.0 / 8.0;                  // Cycles per bit

    // Local (L) Constants
    private static final double LOCAL_CPU_RATE = 500 * pow(10, 6);                  // Cycles per second
    private static final double LOCAL_COMP_ENERGY_RATE = 1 / (730 * pow(10, 6));    // Jules per cycle
    private static final double LOCAL_TRANS_ENERGY_RATE = 1.42 * pow(10, -7);       // Jules per bit (up and down)
    private static final double LOCAL_TRANS_RATE = 72.2 * pow(10, 6);               // Bits per second

    // (Computational) Access Point ((C)AP) Constants
    private static final double CAP_CPU_RATE = 5 * pow(10, 9);                      // Cycles per second
    private static final double CAP_TRANS_RATE = 15 * pow(10, 6);                   // Bits per second

    // Remote Cloud (RC) Constants
    private static final double RC_CPU_RATE = 10 * pow(10, 9);                      // Cycles per second

    // LAC 100 Constants
    private static final double ALPHA = 2 * pow(10, -7);                            // Jules per second
    private static final double BETA = 5 * pow(10, -7);                             // Jules per second
    private static final double RHO = 1;                                            // Jules per second

    // Other Constants
    private static final int NUMBER_OF_TASKS = 10;                                  // Number of tasks to simulate
    private static final long SEED = 587469L;                                       // Random number seed

    // The Mobile Cloud Computing Architecture
    ArrayList<Task> tasks;                                                          // Holds the tasks to be executed
    // Other Variables
    Random rand = new Random(SEED);
    private LocalUser localUser;                                                    // Simulates the local user
    private AccessPoint accessPoint;                                                // Simulates the access point
    private RemoteCloud remoteCloud;                                                // Simulates the remote cloud

    private Main(String mode) throws CustomException {
        int m = Integer.valueOf(mode);

        // FIXME: Commented out because changes to other classes will immediatly cause errors
        localUser = new LocalUser(accessPoint, tasks);          // Initializing simulated Local User with references to Access Point and Tasks
        accessPoint = new AccessPoint(localUser, remoteCloud);  // Initializing simulated Access Point with references to Local User and Remote Cloud
        remoteCloud = new RemoteCloud(accessPoint);             // Initializing simulated Remote Cloud with references to Access Point

        localUser.resolveTasks();   // Start resolving task list

        makeTasks();


        switch (m) {
            case 0: // Custom test apparatus NOTE: Default Choice
                testSuite();
                break;
            case 1: // Local
                runLocal();
                break;
            case 2: // Remote Cloud
                runCloud();
                break;
            case 3: // Random
                runRandom();
                break;
            case 4: // LC 100
                runLC100();
                break;
            case 5: // LAC 100
                runLAC100();
                break;
        }

        localUser = new LocalUser(accessPoint, tasks);          // Initializing simulated Local User with references to Access Point and Tasks
        accessPoint = new AccessPoint(localUser, remoteCloud);  // Initializing simulated Access Point with references to Local User and Remote Cloud
        remoteCloud = new RemoteCloud(accessPoint);             // Initializing simulated Remote Cloud with references to Access Point

        localUser.resolveTasks();   // Start resolving task list
    }

    public static void main(@NotNull String[] args) {
        try {
            Main m = new Main(args[0]);
        } catch (CustomException e) {
            e.print();
            e.printStackTrace();
        }
    }

    /**
     * This method is for the creation of custom and more elaborate tests.  This can include averaging multiple runs
     * or testing various offloading strategies without running the program multiple times.
     */
    private void testSuite() {
        System.out.println("Works!");
    }

    /**
     * This method creates the tasks that are to be processed.  Tasks are comprised of an input and output data size.
     */
    private void makeTasks() {
        double inRange;     // Range of input size
        double outRange;    // Range of output size
        double in;          // Input size
        double out;         // Output size

        inRange = MAX_INPUT_SIZE - MIN_INPUT_SIZE + 1;
        outRange = MAX_OUTPUT_SIZE - MIN_OUTPUT_SIZE + 1;
        tasks = new ArrayList<>(NUMBER_OF_TASKS);

        for (int t = 0; t < NUMBER_OF_TASKS; t++) {
            in = rand.nextDouble() * inRange + MIN_INPUT_SIZE;
            out = rand.nextDouble() * outRange + MIN_OUTPUT_SIZE;
            tasks.add(new Task(in, out, APP_CYCLES_PER_BIT));
        }
    }

    /**
     * This method runs a simulation in which each of the tasks are processed locally.
     */
    private void runLocal() throws CustomException {
        for (Task t : tasks) {
            t.markL();
        }
    }

    /**
     * This method runs a simulation in which each of the tasks is processed on the remote cloud.
     */
    private void runCloud() throws CustomException {
        for (Task t : tasks) {
            t.markRC();
        }
    }

    /**
     * This method runs a simulation in which each of the tasks is processed randomly
     */
    private void runRandom() throws CustomException {
        int x, y; // Variables from paper to determine where task is processed
        for (Task t : tasks) {
            x = rand.nextBoolean() ? 1 : 0;
            y = rand.nextBoolean() ? 1 : 0;
            if (x == 0) {            // Local
                t.markL();
            } else if (y == 0) {    // Access Point
                t.markAP();
            } else {                // Remote cloud
                t.markRC();
            }
        }
    }

    /**
     * This method runs a simulation using the LC100 method described in the paper
     */
    @Contract(" -> fail")
    private void runLC100() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * This method runs a simulation using the LAC100 method described in the paper
     */
    @Contract(" -> fail")
    private void runLAC100() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}