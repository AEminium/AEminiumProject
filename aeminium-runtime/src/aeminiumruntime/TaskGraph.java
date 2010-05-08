package aeminiumruntime;

import java.util.Collection;
import java.util.Iterator;

public interface TaskGraph extends Iterator {
    public boolean add(Task task, Collection<Task> deps);
}