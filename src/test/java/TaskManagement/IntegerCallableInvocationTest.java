package TaskManagement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Invocation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by jordan on 6/17/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class IntegerCallableInvocationTest {


    private Invocation intInvocation;
    private IntegerCallableInvocation intCallableInvocation;
    private Integer expectedResult = 5;

    @Test
    public void testCall(){
        intCallableInvocation = new IntegerCallableInvocation(intInvocation);
        when(intInvocation.invoke(Integer.class)).thenReturn(expectedResult);
        Integer actualResult = intCallableInvocation.call();
        assertEquals(actualResult, expectedResult);
    }
}