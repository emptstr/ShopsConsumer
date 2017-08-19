package TaskManagement;

import javax.ws.rs.client.Invocation;
import java.util.concurrent.Callable;

/**
 * Created by jordan on 6/16/17.
 */
public abstract class CallableInvocation<T> implements Callable<T> {

    protected final Invocation invocation;


    public CallableInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    public T call() throws Exception {
        throw new UnsupportedOperationException("Did not implement the call method");
    }
}
