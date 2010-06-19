package aeminiumruntime.schedulers;

import java.util.concurrent.Callable;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinTask;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.TaskGraph;

public class ForkJoinScheduler extends BaseScheduler {

    ForkJoinPool pool;
    
    public ForkJoinScheduler(TaskGraph graph) {
        super(graph);
        pool = new ForkJoinPool();
    }

    @Override
    public void scheduleWork() {

        synchronized (graph) {
            if (graph.hasNext()) {
                RuntimeTask task = (RuntimeTask) graph.next();
                ForkJoinTask<Object> thread = createThreadFromTask(task);
                pool.execute(thread);
            }
        }
    }

    protected ForkJoinTask<Object> createThreadFromTask(final RuntimeTask task) {
        Callable<Object> threadWrapper = new Callable<Object>() {
            public Object call() {
                Object result = task.execute();
                refresh();
                return result;
            }
        };
        ForkJoinTask<Object> thread = ForkJoinTask.adapt(threadWrapper);
        return thread;
    }

    
    
}