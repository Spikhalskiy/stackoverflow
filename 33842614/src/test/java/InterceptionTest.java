import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitry Spikhalskiy <dmitry@spikhalskiy.com>
 */
public class InterceptionTest {
    @Before
    public void setup() {
        InterceptorsInitializer.registerModificationInterception();
    }

    @Test
    public void test() {
        //Let's create an object
        MyObject mObj = new MyObject();
        MyObjectActiveRepository.INSTANCE.putToGroup(mObj, "group1");
        MyObjectActiveRepository.INSTANCE.registerActiveForItsGroup(mObj);
        //Let's create a list of past states
        List<MyObject> pastStates = new ArrayList<MyObject>();

        //doing some operations on mObj ....
        mObj.modify("state1");

        //done modifying mObj, now let's save it's state and then create a copy to begin again
        pastStates.add(mObj.copy());

        //more of this...
        mObj.modify("state2");
        pastStates.add(mObj.copy());

        mObj.modify("state3");

        assertEquals("state1", pastStates.get(0).getState());
        assertEquals("state2", pastStates.get(1).getState());
        assertEquals("state3", mObj.getState());

        pastStates.get(0).modify("stateNew");
        assertEquals("state1", pastStates.get(0).getState());
        assertEquals("state2", pastStates.get(1).getState());
        assertEquals("stateNew", mObj.getState());
    }
}
