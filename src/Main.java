import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.max;
import static java.lang.Math.pow;

/**
 * COSC 5P06 Project.
 * October 31st 2018.
 * Tyler Cowan (tc14vv, 5635784) and
 * Michael Dubé (md12ol, 5243845)
 * <p>
 * This class was created to handle the simulation of a Mobile Cloud Computing architecture which
 * includes a single local user (Local) using a mobile device, a single access point, and a single remote cloud server.
 * The purpose to simulate this architecture is to test whether the inclusion of computation resources at the access
 * point, which can process tasks from the mobile device, has better performance than a cloud computing system with
 * only the remote cloud for execution.  A proposed algorithm to determine where different tasks should be processed
 * will be implemented and tested against various task offloading heuristics.
 * To Run:
 * 1 - Navigate to MCCProject/src in a Bash terminal
 * 2 - Execute "javac *.java" in the terminal
 * 3 - Execute "java Main" in the terminal
 * 4 - Follow command line instructions
 */

class Main {

    // Program Execution Constants
    private static final boolean COLLECTING_DATA = true;   // True when collecting data for report

    // Task Constants
    private static final double MAX_INPUT_SIZE = 30 * 8 * pow(10, 6);   // Bits; 30MB
    private static final double MIN_INPUT_SIZE = 10 * 8 * pow(10, 6);   // Bits; 10MB
    private static final double MAX_OUTPUT_SIZE = 3 * 8 * pow(10, 6);   // Bits; 3MB
    private static final double MIN_OUTPUT_SIZE = 1 * 8 * pow(10, 6);   // Bits; 1MB
    private static final double CYCLES_PER_BIT = 1900.0 / 8.0;          // Cycles per Bit; for task processing

    // Local (L) Constants
    private static final double LOCAL_CPU_RATE = 500 * pow(10, 6);                  // Cycles per second
    private static final double LOCAL_TRANS_RATE = 72.2 * pow(10, 6);               // Bits per second
    private static final double LOCAL_COMP_ENERGY_RATE = 1 / (730 * pow(10, 6));    // Jules per cycle
    private static final double LOCAL_TRANS_ENERGY_RATE = 1.42 * pow(10, -7);       // Jules per bit (up and down)

    // (Computational) Access Point ((C)AP) Constants
    private static final double CAP_CPU_RATE = 5 * pow(10, 9);      // Cycles per second
    private static final double CAP_TRANS_RATE = 15 * pow(10, 6);   // Bits per second

    // Remote Cloud (RC) Constants
    private static final double RC_CPU_RATE = 10 * pow(10, 9);      // Cycles per second

    // LAC 100 Constants
    private static final double ALPHA = 2 * pow(10, -7);            // Jules per second
    private static final double BETA = 5 * pow(10, -7);             // Jules per second
    private static final double RHO = 1;                            // Jules per second

    // Other Constants
    private static final int NUMBER_OF_TASKS = 10;                  // Number of tasks to simulate
    private static final int NUMBER_OF_UNIQUE_METHODS = 7;          // Number of task offloading methods
    private static final int NUMBER_OF_RUNS = 100;                  // Times to repeat each test for data collection
    private static final long SEED = (long) (Math.random() * 5000); //587469L;                       // Random number seed

    // The Mobile Cloud Computing Architecture
    private ArrayList<Task> tasks;                                  // Holds the tasks to be executed

    // Other Variables
    private Random rand = new Random(SEED);                         // For random number generation

    private Main() throws CustomException {
        LocalUser localUser;        // Simulates the local user
        AccessPoint accessPoint;    // Simulates the access point
        RemoteCloud remoteCloud;    // Simulates the remote cloud

        remoteCloud = new RemoteCloud(RC_CPU_RATE, BETA);
        accessPoint = new AccessPoint(remoteCloud, CAP_CPU_RATE, CAP_TRANS_RATE, ALPHA);
        localUser = new LocalUser(accessPoint, LOCAL_CPU_RATE, LOCAL_COMP_ENERGY_RATE, LOCAL_TRANS_ENERGY_RATE
                , LOCAL_TRANS_RATE);

        loadTasks(localUser); // Tasks are loaded into localUser
        if (!COLLECTING_DATA) { // Typical execution for marking
            switch (promptUser()) {
                default: // Custom test apparatus (DEFAULT CHOICE)
                    System.out.println("* * * CUSTOM TEST SUITE * * *");
                    testSuite(localUser);
                    break;
                case '1': // Local User
                    System.out.println("* * * LOCAL USER * * *");
                    markForL();
                    simpleTest(localUser);
                    break;
                case '2': // Access Point
                    System.out.println("* * * ACCESS POINT * * *");
                    markForAP();
                    simpleTest(localUser);
                    break;
                case '3': // Remote Cloud
                    System.out.println("* * * REMOTE CLOUD * * *");
                    markForRC();
                    simpleTest(localUser);
                    break;
                case '4': // Random
                    System.out.println("* * * RANDOM * * *");
                    markForRandom();
                    simpleTest(localUser);
                    break;
                case '5': // LC 100
                    System.out.println("* * * NOT YET IMPLEMENTED * * *");
                    markForLC100(localUser);
                    simpleTest(localUser);
                    break;
                case '6': // LAC 100
                    markForLAC100(localUser);
                    simpleTest(localUser);
                    break;
                case '7': // RM 100
                    markForRM100();
                    simpleTest(localUser);
                    break;
            }
        } else { // Atypical execution for data gathering
            System.out.println("Starting data collection...");
            outputData(dataCollection(localUser, NUMBER_OF_RUNS));
            System.out.println("Done. Data located in Output directory.");
        }
    } // Constructor

    /**
     * Creates a new instance of Main which runs the simulation.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            new Main();
        } catch (CustomException e) { // Catch any custom exceptions to return error message causing exception
            e.print();
            e.printStackTrace();
        }
    } // main

    /**
     * This method creates the tasks that are to be processed.  Tasks are comprised of an input and output data size.
     * Once made, the tasks are loaded into the local user.
     *
     * @param local the local user who will begin with the list of tasks
     */
    private void loadTasks(LocalUser local) {
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
            tasks.add(new Task(in, out, CYCLES_PER_BIT));
        }
        local.setTasks(tasks);
    } // setTasks

    /**
     * This method is for running multiple different task offloading methods consecutively to be able to compare data
     * against various methods in a single program run.
     *
     * @param local the Local User who starts with the tasks
     */
    private void testSuite(LocalUser local) throws CustomException {
        System.out.println("* * * LOCAL USER * * *");
        markForL();         // Setup offloading method
        simpleTest(local);  // Test offloading method
        resetTasks();       // Reset tasks for next test
        System.out.println("* * * ACCESS POINT * * *");
        markForAP();
        simpleTest(local);
        resetTasks();
        System.out.println("* * * REMOTE CLOUD * * *");
        markForRC();
        simpleTest(local);
        resetTasks();
        System.out.println("* * * RANDOM * * *");
        markForRandom();
        simpleTest(local);
    } // testSuite

    /**
     * This method signifies a single test where the pre-loaded and marked tasks are executed as per their marking
     * and the results from this test are printed, task by task as well as the total energy, time and cost of
     * processing all the tasks.
     *
     * @param local The local user
     * @throws CustomException Indicates program error
     */
    private void simpleTest(LocalUser local) throws CustomException {
        local.resolveTasks();
        displayAllTaskInfo();
    } // simpleTest

    /**
     * This method calculates the cost, as defined within the paper, for processing all the tasks.
     *
     * @return Cost of processing all tasks
     * @throws CustomException Indicates program error
     */
    private double calcCost() throws CustomException {
        double tE = 0.0;        // Total energy
        double timeL = 0.0;     // Total time delay from Local
        double timeAP = 0.0;    // Total time delay from AP
        double timeRC = 0.0;    // Total time delay from RC
        for (Task t : tasks) {
            tE += t.totalEnergy();
            if (t.getCompL()) timeL += t.totalTime();           // Total time delay from Local
            else if (t.getCompAP()) timeAP += t.totalTime();    // Total time delay from AP
            else timeRC += t.totalTime();                       // Total time delay from RC
        }
        return tE + RHO * max(timeL, max(timeAP, timeRC));
    } // calcCost

    /**
     * This method collects data from all the task offloading methods defined within the paper.  Each method is
     * repeated runs number of times and all the results are placed within results.
     *
     * @param local The local user
     * @param runs  The number of runs per method
     * @return Results from the runs of each test
     * @throws CustomException Indicates program error
     */
    private ArrayList<LinkedList<Double>> dataCollection(LocalUser local, int runs) throws CustomException {
        ArrayList<LinkedList<Double>> results;  // To hold test results
        /*
        0 - Local Only
        1 - Access Point Only
        2 - Remote Cloud Only
        3 - Random Only
        4 - LC 100
        5 - LAC 100
        6 - Random Mapping 100
         */
        results = new ArrayList<>(NUMBER_OF_UNIQUE_METHODS);
        for (int i = 0; i < NUMBER_OF_UNIQUE_METHODS; i++) {
            results.add(new LinkedList<>());
        }
        for (int test = 0; test < NUMBER_OF_UNIQUE_METHODS; test++) {
            try {
                for (int run = 0; run < runs; run++) {
                    loadTasks(local);                   // Load new tasks
                    markAll(test, local);               // Mark for proper execution
                    local.resolveTasks();               // Resolve tasks
                    results.get(test).add(calcCost());  // Append to results
                }
            } catch (UnsupportedOperationException e) {
                System.out.println("NOTE: Test " + test + " not run as not yet implemented");
            }
        }
        return results;
    } // dataCollection

    /**
     * This method resets all the calculated information regarding the tasks which are to be executed.  Resetting is
     * necessary in order to use the same tasks using various offloading heuristics in order to compare performance.
     */
    private void resetTasks() {
        for (Task t : tasks) {
            t.reset();
        }
    } // resetTasks

    /**
     * This method prints the results from the simulation to the user.  This includes the time and energy of each
     * task, the total time and energy of all the tasks as well as the cost, as defined within the paper.
     *
     * @throws CustomException Indicates program error
     */
    private void displayAllTaskInfo() throws CustomException {
        DecimalFormat f = new DecimalFormat("##.0000");  // To properly format numerical output
        int taskNum = 0;                                    // To keep track of number of tasks processed
        double tE = 0.0;                                    // Total energy
        double tT = 0.0;                                    // Total time
        for (Task t : tasks) {
            System.out.println("Task " + taskNum++ + ":\tTotal Energy: " + f.format(t.totalEnergy()) + "\tTotal "
                    + "Time: " + f.format(t.totalTime()));
            tE += t.totalEnergy();
            tT += t.totalTime();
        }
        System.out.println("ALL:\tTotal Energy: " + f.format(tE) + "\tTotal Time: " + f.format(tT) + "\tTotal: "
                + f.format(tE + tT));
        System.out.println("COST: " + f.format(calcCost()) + " Jules");
    } // displayAllTaskInfo

    /**
     * This method takes data within an ArrayList and outputs the data to the Output folder within the project.
     *
     * @param data Data to output
     */
    private void outputData(ArrayList<LinkedList<Double>> data) {
        ArrayList<FileWriter> outs = new ArrayList<>(NUMBER_OF_UNIQUE_METHODS);
        int testNum = 0; // Keeps track of current test
        for (int i = 0; i < NUMBER_OF_UNIQUE_METHODS; i++) {
            try {
                new File("./Output/" + i + ".txt");
                outs.add(new FileWriter("./Output/" + i + ".txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (LinkedList<Double> list : data) {
            for (Double d : list) {
                try {
                    outs.get(testNum).write(d.toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            testNum++;
        }
        for (FileWriter file : outs) {
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } // outputData

    /**
     * This method asks the user for their preferred option for running the simulation.  Upon running the program
     * the options will appear within the terminal and the user's response will be returned to facilitate
     * desired execution.
     *
     * @return User's choice for execution
     */
    private char promptUser() {
        Scanner s = new Scanner(System.in);
        System.out.println("Please choose a testing mode:");
        System.out.println("\t[0] Custom:\t\t\tConsecutively runs [1], [2], [3], and [4]. (DEFAULT).");
        System.out.println("\t[1] Local User:\t\tAll tasks will be processed on the Local User.");
        System.out.println("\t[2] Access Point:\tAll tasks will be processed on the Access Point.");
        System.out.println("\t[3] Remote Cloud:\tAll tasks will be processed on the Remote Cloud.");
        System.out.println("\t[4] Random:\t\t\tEach task is randomly assigned to LU, AP, or RC.");
        System.out.println("\t[5] LC 100:\t\t\tProposed method from paper. Does not include AP.");
        System.out.println("\t[6] LAC 100:\t\tPrimary offloading method investigated within paper.");
        System.out.println("\t[7] RM 100:\t\t\tProposed method from paper. Uses Random Mapping algorithm.");
        return s.next().charAt(0);
    } // promptUser

    // markForX Methods

    /**
     * This method uses the appropriate, numerically indexed, markForX where X can be: 1) Local, 2) Access Point, 3)
     * Remote Cloud, 4) Random, 5) LC100, 6) LAC100, and 7) Random Mapping 100
     *
     * @param test the associated numerical index of the appropriate markForX method.
     */
    private void markAll(int test, LocalUser local) throws CustomException, UnsupportedOperationException {
        switch (test) {
            case 0:
                markForL();
                break;
            case 1:
                markForAP();
                break;
            case 2:
                markForRC();
                break;
            case 3:
                markForRandom();
                break;
            case 4:
                markForLC100(local);
                break;
            case 5:
                markForLAC100(local);
                break;
            case 6:
                markForRM100();
        }
    } // markAll

    /**
     * This method marks the tasks to be processed locally.
     */
    private void markForL() throws CustomException {
        for (Task t : tasks) {
            t.markL();
        }
    } // markForL

    /**
     * This method marks the tasks to be processed on the access point.
     */
    private void markForAP() throws CustomException {
        for (Task t : tasks) {
            t.markAP();
        }
    } // markForAP

    /**
     * This method marks the tasks to be processed on the remote cloud.
     */
    private void markForRC() throws CustomException {
        for (Task t : tasks) {
            t.markRC();
        }
    } // markForRC

    /**
     * This method marks the tasks to be processed randomly at one of the three locations.
     */
    private void markForRandom() throws CustomException {
        int n;
        for (Task t : tasks) {
            n = rand.nextInt(3);
            if (n == 0) {           // Local
                t.markL();
            } else if (n == 1) {    // Access Point
                t.markAP();
            } else {                // Remote cloud
                t.markRC();
            }
        }
    } // markForRandom

    /**
     * This method runs a simulation using the LC100 method described in the paper.
     */
    private void markForLC100(LocalUser local) throws CustomException {

        int best_locations[] = new int[10];
        double best_cost = Double.MAX_VALUE;

        int i[] = new int[10];

        for (i[0] = 0; i[0] < 2; i[0]++) {
            for (i[1] = 0; i[1] < 2; i[1]++) {
                for (i[2] = 0; i[2] < 2; i[2]++) {
                    for (i[3] = 0; i[3] < 2; i[3]++) {
                        for (i[4] = 0; i[4] < 2; i[4]++) {
                            for (i[5] = 0; i[5] < 2; i[5]++) {
                                for (i[6] = 0; i[6] < 2; i[6]++) {
                                    for (i[7] = 0; i[7] < 2; i[7]++) {
                                        for (i[8] = 0; i[8] < 2; i[8]++) {
                                            for (i[9] = 0; i[9] < 2; i[9]++) {
                                                // Mark the tasks according to locations from above loops
                                                for (int k = 0; k < tasks.size(); k++) {
                                                    tasks.get(k).mark(i[k] * 2);
                                                }
                                                local.resolveTasks();
                                                double new_cost = calcCost();
                                                if (new_cost < best_cost) {
                                                    best_cost = new_cost;
                                                    best_locations = new int[]{i[0] * 2, i[1] * 2, i[2] * 2, i[3] * 2, i[4] * 2, i[5] * 2, i[6] * 2, i[7] * 2, i[8] * 2, i[9] * 2};
                                                }
                                                resetTasks();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Mark the tasks according to the best found locations
        for (int k = 0; k < tasks.size(); k++) {
            tasks.get(k).mark(best_locations[k]);
        }
    } // markForLC100

    /**
     * This method runs a simulation using the LAC100 method described in the paper.
     */
    private void markForLAC100(LocalUser local) throws CustomException {

        int best_locations[] = new int[10];
        double best_cost = Double.MAX_VALUE;

        int i[] = new int[10];

        for (i[0] = 0; i[0] < 3; i[0]++) {
            for (i[1] = 0; i[1] < 3; i[1]++) {
                for (i[2] = 0; i[2] < 3; i[2]++) {
                    for (i[3] = 0; i[3] < 3; i[3]++) {
                        for (i[4] = 0; i[4] < 3; i[4]++) {
                            for (i[5] = 0; i[5] < 3; i[5]++) {
                                for (i[6] = 0; i[6] < 3; i[6]++) {
                                    for (i[7] = 0; i[7] < 3; i[7]++) {
                                        for (i[8] = 0; i[8] < 3; i[8]++) {
                                            for (i[9] = 0; i[9] < 3; i[9]++) {
                                                // Mark the tasks according to locations from above loops
                                                for (int k = 0; k < tasks.size(); k++) {
                                                    tasks.get(k).mark(i[k]);
                                                }
                                                local.resolveTasks();
                                                double new_cost = calcCost();
                                                if (new_cost < best_cost) {
                                                    best_cost = new_cost;
                                                    best_locations = new int[]{i[0], i[1], i[2], i[3], i[4], i[5], i[6], i[7], i[8], i[9]};
                                                }
                                                resetTasks();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Mark the tasks according to the best found locations
        for (int k = 0; k < tasks.size(); k++) {
            tasks.get(k).mark(best_locations[k]);
        }
    } // markForLAC100

    /**
     * This method runs a simulation using the Random Mapping 100 method described in the paper.
     */
    private void markForRM100() throws CustomException {
        for (Task t : tasks) {
            if (Math.random() < 0.5) {
                t.markL();
            } else if (Math.random() < 0.5) {
                t.markAP();
            } else {
                t.markRC();
            }
        }
    } // markForRM100


}