import java.util.ArrayList;

/**
 * COSC 5P06 Project. October 31st 2018. By Tyler Cowan (5635784) and Michael Dubé (5243845).
 *
 * This class was created to handle the simulation of a Mobile Cloud Computing architecture which
 * includes a single mobile device, a single mobile access point, and a single remote cloud server.
 * The purpose to simulate this architecture is to test whether the inclusion of computation
 * resources at the access point, which can process tasks from the mobile device, has better
 * performance than a cloud computing system with only the remote cloud for execution.
 */

public class Main {

  double uploadRate;        // The upload capacity of the mobile device
  double downloadRate;      // The download capacity of the mobile device

  LocalUser theUser;
  AccessPoint theAP;
  RemoteCloud theRC;

  ArrayList<Task> tasks;

  public Main() {
    makeTasks();
  }

  public static void main(String[] args) {
    Main m = new Main();
    LocalUser l = new LocalUser();

  }

  public void makeTasks() {
    Task t = new Task();
    RemoteCloud rc = new RemoteCloud();
    rc.runTask(t);
    System.out.println(t.app);
  }

  public void runLocal(LocalUser l) {
    // Make the tasks be local tasks
    Task t = new Task();
    t.markForLocalComp();
    l.runTask(t);
  }

  public void runCloud() {

  }

  public void runRandom() {

  }

  public void calculateCost() {

  }

}