package TaskManagement;

import TaskManagement.InvocationParameter;

import javax.ws.rs.client.Invocation;

/**
 * Created by jordan on 6/15/17.
 */
public interface InvocationFactory<T extends Invocation> {

    public T getInvocation(final InvocationParameter parameter);
}
