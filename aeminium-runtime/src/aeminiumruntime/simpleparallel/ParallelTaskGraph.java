package aeminiumruntime.simpleparallel;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.TaskGraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ParallelTaskGraph implements TaskGraph {
    HashMap<Integer, RuntimeTask> taskDictionary = new HashMap<Integer, RuntimeTask>();
    HashMap<Integer, List<Integer>> taskDependencies = new HashMap<Integer, List<Integer>>();
    HashMap<Integer, List<Integer>> taskChildren = new HashMap<Integer, List<Integer>>();
    List<RuntimeTask> readyList = new ArrayList<RuntimeTask>();
    List<RuntimeTask> doneList = new ArrayList<RuntimeTask>();

    public synchronized boolean add(RuntimeTask task, Collection<RuntimeTask> deps) {
        taskDictionary.put(task.getId(), task);
        List<Integer> depIds = new ArrayList<Integer>();
        for (RuntimeTask t : deps) {
            depIds.add(t.getId());
            addOrAppendChild(t.getId(), task.getId());
        }
        taskDependencies.put(task.getId(), depIds);
        if (deps.isEmpty()) {
            appendToReady(task);
        }
        return true;
    }

    private void appendToReady(RuntimeTask task) {
        if (!doneList.contains(task)) {
            readyList.add(task);
        }
    }

    private void addOrAppendChild(int dep, int task) {
        if (!taskChildren.containsKey(dep)) {
            taskChildren.put(dep, new ArrayList());
        }
        taskChildren.get(dep).add(task);
    }

    /* Removes finished tasks from dependencies  */
    private synchronized void updateGraph() {
        List<Integer> tasksToBeRemoved = new ArrayList();
        for (int tid : taskDictionary.keySet()) {
            if (taskDictionary.get(tid).isDone()) {
                if ( taskChildren.containsKey(tid) ) {
                    for (int childId : taskChildren.get(tid)) {
                        List<Integer> deps = taskDependencies.get(childId);
                        deps.remove(new Integer(tid));
                        if (deps.isEmpty()) {
                            readyList.add(taskDictionary.get(childId));
                        }

                    }
                }
                tasksToBeRemoved.add(tid);
            }

        }
        for (Integer tid: tasksToBeRemoved) {
            taskChildren.remove(tid);
            taskDictionary.remove(tid);
        }

    }

    public synchronized boolean hasNext() {
        updateGraph();
        return !readyList.isEmpty();
    }

    public synchronized Object next() {
        updateGraph();
        RuntimeTask task = readyList.get(0);
        readyList.remove(0);
        doneList.add(task);
        return task;
    }

    public void remove() {}

    public boolean isDone() {
        return taskDictionary.isEmpty();
    }
    
    
}
