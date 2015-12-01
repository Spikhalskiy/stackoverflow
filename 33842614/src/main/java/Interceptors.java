import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.Super;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.util.concurrent.Callable;

/**
 * @author Dmitry Spikhalskiy <dmitry@spikhalskiy.com>
 */
@SuppressWarnings("unused")
public class Interceptors {
    public static void modify(@This MyObject thisObject, @Super MyObject thisObjectWithOriginalMethods,
                              @FieldValue("group") String groupName, @Argument(0) String newState) {
        MyObject activeObject = null;
        if (groupName != null) {
            activeObject = MyObjectActiveRepository.INSTANCE.getActiveForGroup(groupName);
        }

        if (activeObject == null) {
            //no active entity for group found
            activeObject = thisObject;
        }

        if (thisObject == activeObject) {
            thisObjectWithOriginalMethods.modify(newState);
        } else {
            activeObject.modify(newState);
        }
    }


    public static MyObject copy(@SuperCall Callable<MyObject> zuper, @FieldValue("group") String groupName)
            throws Exception {
        MyObject copyResult = zuper.call();
        MyObjectActiveRepository.INSTANCE.putToGroup(copyResult, groupName);
        return copyResult;
    }
}
