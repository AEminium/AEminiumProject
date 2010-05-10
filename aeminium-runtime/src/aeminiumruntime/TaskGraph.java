package aeminiumruntime;

import java.util.Collection;
import java.util.Iterator;

public interface TaskGraph extends Iterator {
    public boolean add(RuntimeTask task, Collection<RuntimeTask> deps);
    public boolean isDone();
}