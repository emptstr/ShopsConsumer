package TaskManagement;


/**
 * Created by jordan on 6/15/17.
 */
public interface CallableInvocationFactory<T> {

    public CallableInvocation<T> getInstance();
}
