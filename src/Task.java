/**
 * This class is meant to abstract the data and procedural components which relate to individual
 * tasks which need to be executed as outlined through the paper.
 */

class Task {

    // Variables regardless of location of processing
    private double inputData;         // Input Data Size
    private double outputData;        // Output Data Size
    private double cyclesPerByte;     // The number of CPU cycles necessary per byte of input data
    private double totalTime;         // Total time used to process task
    private double totalEnergy;       // Total energy used by mobile device to process task
    private boolean calculated;       // If the energy use and processing time of the task has been calculated

    // Locally (L)
    private boolean compL;            // Task is processed locally
    private double energyUseL;        // Energy used for processing locally
    private double processTimeL;      // Time to process if processed locally

    // Access Point (AP)
    private boolean compAP;          // Task is processed on CAP
    private double energyTransAP;    // Energy consumed for transmission to CAP
    private double energyReceivAP;   // Energy consumed for receiving from CAP
    private double upTimeAP;         // Time to upload to CAP
    private double downTimeAP;       // Time to download from CAP
    private double processTimeAP;    // Time to process if processed on CAP

    // Remote Cloud (RC)
    private boolean compRC;         // Task is processed on RC
    private double transTimeRC;     // Time to transmit to RC
    private double processTimeRC;   // Time to process if processed on RC

    // Costs
    private double costAP;          // Cost of letting AP process task
    private double costRC;          // Cost of letting RC process task

    Task(double inputData, double outputData, double cyclesPerByte) {
        this.inputData = inputData;
        this.outputData = outputData;
        this.cyclesPerByte = cyclesPerByte;
        calculated = false;
    } // Constructor

    void markL() {
        compL = true;
        compAP = false;
        compRC = false;
    }

    void markAP() {
        compL = false;
        compAP = true;
        compRC = false;
    }

    void markRC() {
        compL = false;
        compAP = false;
        compRC = true;
    }

    void markComplete() {
        // Make sure that this is the end of the Task, throw stuff if not
    }

}
