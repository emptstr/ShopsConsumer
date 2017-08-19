package TaskManagement;

import javax.ws.rs.client.Invocation;

/**
 * Created by jordan on 6/17/17.
 */
public class IntegerCallableInvocation extends CallableInvocation<Integer> {

    public IntegerCallableInvocation(Invocation invocation) {
        super(invocation);
    }

    @Override
    public Integer call(){
        return invocation.invoke(Integer.class);
    }
}
