import org.jetbrains.annotations.Contract;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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
    DecimalFormat f = new DecimalFormat("##.0000");
    private LocalUser localUser;                                                    // Simulates the local user
    private AccessPoint accessPoint;                                                // Simulates the access point
    private RemoteCloud remoteCloud;                                                // Simulates the remote cloud

    private Main() throws CustomException {
        // Initializing simulated Remote Cloud
        remoteCloud = new RemoteCloud(RC_CPU_RATE, BETA);
        // Initializing simulated Access Point
        accessPoint = new AccessPoint(remoteCloud, CAP_CPU_RATE, CAP_TRANS_RATE, ALPHA);
        // Initializing simulated Local User
        localUser = new LocalUser(accessPoint, tasks, LOCAL_CPU_RATE, LOCAL_COMP_ENERGY_RATE, LOCAL_TRANS_ENERGY_RATE, LOCAL_TRANS_RATE);
        // Generates NUMBER_OF_TASKS many tasks
        makeTasks();
        switch (promptUser()) {
            default: // Custom test apparatus NOTE: Default Choice
                System.out.println("* * * CUSTOM TEST SUITE * * *");
                testSuite();
                break;
            case '1': // Local User
                System.out.println("* * * LOCAL USER * * *");
                markForLU();
                simpleTest();
                break;
            case '2': // Access Point
                System.out.println("* * * ACCESS POINT * * *");
                markForAP();
                simpleTest();
                break;
            case '3': // Remote Cloud
                System.out.println("* * * REMOTE CLOUD * * *");
                markForRC();
                simpleTest();
                break;
            case '4': // Random
                System.out.println("* * * RANDOM * * *");
                markForRandom();
                simpleTest();
                break;
            case '5': // LC 100
                System.out.println("* * * NOT YET IMPLEMENTED * * *");
                markForLC100();
                simpleTest();
                break;
            case '6': // LAC 100
                System.out.println("* * * NOT YET IMPLEMENTED * * *");
                markForLAC100();
                simpleTest();
                break;
        }

    }

    public static void main(String[] args) {
        try {
            Main m = new Main();
        } catch (CustomException e) {
            // TODO: Michael add exceptions for other classes if needed
            e.print();
            e.printStackTrace();
        }
    }

    /**
     * This method is for the creation of custom and more elaborate tests.  This can include averaging multiple runs
     * or testing various offloading strategies without running the program multiple times.
     */
    private void testSuite() throws CustomException {
        double averageCombinedTotal = 0;
        double averageTotalEnergy = 0;
        double averageTotalTime = 0;
        System.out.println("* * * LOCAL USER * * *");
        markForLU();
        for (int i = 0; i < 30; i++) {
            simpleTest();
            double tE = 0.0;
            double tT = 0.0;
            for (int j = 0; j < tasks.size(); j++) {
                tE += tasks.get(j).totalEnergy();
                tT += tasks.get(j).totalTime();
            }
            averageCombinedTotal += (tE + tT);
            averageTotalEnergy += tE;
            averageTotalTime += tT;
        }
        System.out.println();


        resetTasks();
        System.out.println("* * * ACCESS POINT * * *");
        markForAP();
        simpleTest();
        resetTasks();
        System.out.println("* * * REMOTE CLOUD * * *");
        markForRC();
        simpleTest();
        resetTasks();
        System.out.println("* * * RANDOM * * *");
        markForRandom();
        simpleTest();
    }

    private void simpleTest() throws CustomException {
        localUser.resolveTasks();
        displayTaskInfo();
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
        // Load list of tasks into the Local User
        localUser.loadTasks(tasks);
    }

    /**
     * This method runs a simulation in which each of the tasks are processed locally.
     */
    private void markForLU() throws CustomException {
        for (Task t : tasks) {
            t.markL();
        }
    }

    /**
     * This method runs a simulation in which each of the tasks is processed on the remote cloud.
     */
    private void markForAP() throws CustomException {
        for (Task t : tasks) {
            t.markAP();
        }
    }

    /**
     * This method runs a simulation in which each of the tasks is processed on the remote cloud.
     */
    private void markForRC() throws CustomException {
        for (Task t : tasks) {
            t.markRC();
        }
    }

    /**
     * This method runs a simulation in which each of the tasks is processed randomly
     */
    private void markForRandom() throws CustomException {
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

    private void displayTaskInfo() throws CustomException {
        double tE = 0.0;
        double tT = 0.0;
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("Task " + i + ":\tTotal Energy: " + f.format(tasks.get(i).totalEnergy()) + "\tTotal Time: " + f.format(tasks.get(i).totalTime()));
            tE += tasks.get(i).totalEnergy();
            tT += tasks.get(i).totalTime();

        }
        System.out.println("ALL:\tTotal Energy: " + f.format(tE) + "\tTotal Time: " + f.format(tT));
        System.out.println(f.format((tE + tT)));
    }

    private char promptUser() {
        Scanner s = new Scanner(System.in);
        System.out.println("Please choose a testing mode:");
        System.out.println("\t[0] Custom:\t\t\tConsecutively runs [1], [2], [3], and [4]. (DEFAULT).");
        System.out.println("\t[1] Local User:\t\tAll tasks will be processed on the Local User.");
        System.out.println("\t[2] Access Point:\tAll tasks will be processed on the Access Point.");
        System.out.println("\t[3] Remote Cloud:\tAll tasks will be processed on the Remote Cloud.");
        System.out.println("\t[4] Random:\t\t\tEach task is randomly assigned to LU, AP, or RC.");
        System.out.println("\t[5] LC 100:\t\t\tProposed method from paper. Not yet implemented.");
        System.out.println("\t[6] LAC 100:\t\tProposed method from paper. Not yet implemented.");
        return s.next().charAt(0);
    }

    private void resetTasks() {
        for (Task t : tasks) {
            t.reset();
        }
    }

    /**
     * This method runs a simulation using the LC100 method described in the paper
     */
    @Contract(" -> fail")
    private void markForLC100() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * This method runs a simulation using the LAC100 method described in the paper
     */
    @Contract(" -> fail")
    private void markForLAC100() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}