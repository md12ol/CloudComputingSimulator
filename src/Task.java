/**
 * This class is meant to abstract the data and procedural components which relate to individual
 * tasks which need to be executed as outlined through the paper.
 */

public class Task {

  // Variables regardless of location of processing
  double inputData;         // Input Data Size
  double outputData;        // Output Data Size
  String app;               // App Type TODO: May need to change how app type is stored
  double totalTime;         // Total time used to process task
  double totalEnergy;       // Total energy used by mobile device to process task
  boolean calculated;       // If the energy use and processing time of the task has been calculated

  // Locally (L)
  boolean compL;      // Task is processed locally
  double energyUseL;        // Energy used for processing locally
  double processTimeL;      // Time to process if processed locally

  // Access Point (AP)
  boolean compAP;          // Task is processed on CAP
  double energyTransAP;    // Energy consumed for transmission to CAP
  double energyReceivAP;   // Energy consumed for receiving from CAP
  double upTimeAP;         // Time to upload to CAP
  double downTimeAP;       // Time to download from CAP
  double processTimeAP;    // Time to process if processed on CAP

  // Remote Cloud (RC)
  boolean compRC;         // Task is processed on RC
  double transTimeRC;     // Time to transmit to RC
  double processTimeRC;   // Time to process if processed on RC

  // Costs
  double costAP;          // Cost of letting AP process task
  double costRC;          // Cost of letting RC process task

  public Task() {
    calculated = false;
  } // Constructor

}
