package aeminiumruntime;

import aeminiumruntime.linear.LinearRuntime;
import java.util.ArrayList;
import java.util.Collection;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Runtime rt = new LinearRuntime();
        rt.init();

        Body b1 = new Body() {
            public void execute() {
                int sum = 0;
                for (int i = 0; i < 100; i++) {
                    sum +=i;
                }
                System.out.println("Sum: " + sum);
            }
        };

        Body b2 = new Body() {
            public void execute() {
                int max = 0;
                for (int i = 0; i < 100; i++) {
                    if (i > max) max = i;
                }
                System.out.println("Maximum: " + max);
            }
        };

        Body b3 = new Body() {
            public void execute() {
                for (int i = 0; i < 20; i++) {
                    System.out.println("Processing...");
                }
            }
        };

        Task t1 = rt.createNonBlockingTask(b1);
        Task t2 = rt.createNonBlockingTask(b2);
        Task t3 = rt.createNonBlockingTask(b3);

        rt.schedule(t1, null);
        rt.schedule(t2, null);
        Collection<Task> deps = new ArrayList<Task>();
        deps.add(t2);
        rt.schedule(t3, deps);

        rt.shutdown();
    }

}
