/**
 * This class is meant to abstract the data and procedural components which relate to individual
 * tasks which need to be executed as outlined through the paper.
 */

class Task {

    // Variables regardless of location of processing
    private double inputData;         // Bits - Input Data Size
    private double outputData;        // Bits - Output Data Size
    private double cyclesPerBit;     // Cycles per Bit - The number of CPU cycles necessary per byte of input data
    private boolean marked;         // If the task has been marked for location of processing
    private boolean arrived;        // If the task has arrived at the processing location
    private boolean calculated;       // If the energy use and processing time of the task has been calculated
    private double procEnergy;          // Energy from phone used to process task
    private double procTime;            // Time to process task
    private double transEnergy;         // Energy from phone used to transmit task
    private double transTime;           // Time to transmit task

    // Locally (L)
    private boolean compL;            // Task is processed locally

    // Access Point (AP)
    private boolean compAP;          // Task is processed on CAP

    // Remote Cloud (RC)
    private boolean compRC;         // Task is processed on RC

    // Costs
    private double costAP;          // Cost of letting AP process task (from paper)
    private double costRC;          // Cost of letting RC process task (from paper)

    // TODO: Michael add in cost of time of computation at L, AP, RC and change the eqaution to calculate max of the
    // three

    /**
     * This constructor creates a task and sets the variables and progress flags to their initial values.
     *
     * @param inputData    Input data size
     * @param outputData   Output data size
     * @param cyclesPerBit Cycles per Bit for Task
     */
    Task(double inputData, double outputData, double cyclesPerBit) {
        this.inputData = inputData;
        this.outputData = outputData;
        this.cyclesPerBit = cyclesPerBit;
        marked = false;
        arrived = false;
        calculated = false;
        procEnergy = 0.0;
        procTime = 0.0;
        transTime = 0.0;
        transEnergy = 0.0;
        costAP = inputData;
        costRC = inputData;
    } // Constructor

    /**
     * This method calculates the processing time and energy to process the task.
     *
     * @param energyRate Rate of Energy Use by CPU in Joules per Cycle
     * @param cpuRate    CPU Cycles per Second
     * @param constant   Calculating the cost of processing on AP and RC are a constant * cost (equivalent to size)
     * @throws CustomException Indicates program error
     */
    void processTask(double energyRate, double cpuRate, double constant) throws CustomException {
        if (!marked || !arrived) {
            throw new CustomException("ERROR: Task being processed but not marked and/or not arrived");
        }
        if (compL) {
            procEnergy = inputData * cyclesPerBit * energyRate;
        } else if (compAP) {
            procEnergy = constant * costAP;
        } else {
            procEnergy = constant * costRC;
        }
        procTime = inputData * cyclesPerBit / cpuRate;
        calculated = true;
    }

    /**
     * This method adds the transmission time and energy to and from the access point to the accumulated transmission
     * time and energy.
     *
     * @param upRate   Upload Rate
     * @param downRate Download Rate
     * @throws CustomException Indicates program error
     */
    void sendToAP(double energyRate, double upRate, double downRate) throws CustomException {
        if (compL || (!compAP && !compRC)) {
            throw new CustomException("ERROR: Task sent to AP but not marked for this");
        }
        if (!marked || calculated) {
            throw new CustomException("ERROR: Task being sent to RC but not marked and/or already calculated");
        }
        if (compAP) {
            arrived = true;
        }
        transEnergy += energyRate * inputData;
        transTime += inputData / upRate + outputData / downRate;
    }

    /**
     * This method adds the transmission time to and from the remote cloud to the accumulated transmission time.
     *
     * @param upRate   Upload Rate
     * @param downRate Download Rate
     * @throws CustomException Indicates program error
     */
    void sendToRC(double upRate, double downRate) throws CustomException {
        if (compL || compAP || !compRC) {
            throw new CustomException("ERROR: Task sent to RC but not marked for this");
        }
        if (!marked || calculated) {
            throw new CustomException("ERROR: Task being sent to RC but not marked and/or already calculated");
        }
        arrived = true;
        transTime += inputData / upRate + outputData / downRate;
    }

    /**
     * @return total energy used to transmit and process the task
     * @throws CustomException Indicates program error
     */
    double totalEnergy() throws CustomException {
        if (!marked || !arrived || !calculated) {
            throw new CustomException("ERROR: Attempt to calculate energy before calculated");
        }
        return transEnergy + procEnergy;
    }

    /**
     * @return total time used to transmit and process the task
     * @throws CustomException Indicates program error
     */
    double totalTime() throws CustomException {
        if (!marked || !arrived || !calculated) {
            throw new CustomException("ERROR: Attempt to calculate time before calculated");
        }
        return transTime + procTime;
    }

    /**
     * This method marks a task to be processed locally
     *
     * @throws CustomException Indicates program error
     */
    void markL() throws CustomException {
        if (arrived || calculated) {
            throw new CustomException("ERROR: Task being marked after arrival and/or calculation");
        }
        marked = true;
        arrived = true;
        compL = true;
        compAP = false;
        compRC = false;
    }

    /**
     * This method marks a task to be processed at the access point
     *
     * @throws CustomException Indicates program error
     */
    void markAP() throws CustomException {
        if (arrived || calculated) {
            throw new CustomException("ERROR: Task being marked after arrival and/or calculation");
        }
        marked = true;
        arrived = false;
        compL = false;
        compAP = true;
        compRC = false;
    }

    /**
     * This method marks a task to be processed on the remote cloud
     *
     * @throws CustomException Indicates program error
     */
    void markRC() throws CustomException {
        if (arrived || calculated) {
            throw new CustomException("ERROR: Task being marked after arrival and/or calculation");
        }
        marked = true;
        arrived = false;
        compL = false;
        compAP = false;
        compRC = true;
    }

    boolean getCompL() {
        return compL;
    }

    boolean getCompAP() {
        return compAP;
    }

    boolean getCompRC() {
        return compRC;
    }


}
