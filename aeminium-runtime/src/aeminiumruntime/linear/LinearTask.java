package aeminiumruntime.linear;

import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.Task;
import java.util.Collection;

public class LinearTask implements Task {

    private Body body;

    public LinearTask(Body b) {
        this.body = b;
    }

    public Body getBody() {
        return this.body;
    }

    public void execute() {
        this.body.execute();
    }

    public Collection<Hint> getHints() {
        return null;
    }
}
