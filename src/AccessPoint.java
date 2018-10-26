/**
 * This class was created to abstract the data and procedural components which relate to the
 * (computational) access point as outlined within the paper.
 */

public class AccessPoint {

    // TODO: Determine if this bool is necessary
//  boolean computational;    // Whether or not the access point has computation resources
    double clockFreq;         // The speed of the CPU at the access point
    int numTasks;             // The number of tasks being processed by the access point
    double transRate;         // The transmission rate of the access point with the remote cloud

    public AccessPoint() {

    }

}
