package aeminiumruntime.linear;

import aeminiumruntime.AtomicTask;
import aeminiumruntime.BlockingTask;
import aeminiumruntime.Body;
import aeminiumruntime.DataGroup;
import aeminiumruntime.NonBlockingTask;
import aeminiumruntime.Runtime;
import aeminiumruntime.Task;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LinearRuntime extends Runtime {

    private List<Task> queue;

    @Override
    public void init() {
        queue = new ArrayList<Task>();
    }

    @Override
    public boolean schedule(Task task, Collection<Task> deps) {
        if (task instanceof LinearTask) {
            ((LinearTask ) task).execute();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void shutdown() {
        //TODO clean runtime
    }

    @Override
    public DataGroup createDataGroup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BlockingTask createBlockingTask(Callable<Body> b) {
        try {
            return new LinearBlockingTask(b.call());
        } catch (Exception ex) {
            Logger.getLogger(LinearRuntime.class.getName()).log(Level.SEVERE, "Error creating Task.", ex);
            return null;
        }
    }

    @Override
    public NonBlockingTask createNonBlockingTask(Body b) {
        try {
            return new LinearNonBlockingTask(b);
        } catch (Exception ex) {
            Logger.getLogger(LinearRuntime.class.getName()).log(Level.SEVERE, "Error creating Task.", ex);
            return null;
        }
    }

    @Override
    public AtomicTask createAtomicTask(Body b, DataGroup g) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
