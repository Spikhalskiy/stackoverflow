import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dmitry Spikhalskiy <dspikhalskiy@pulsepoint.com>
 */
public class MyObjectActiveRepository {
    public final static MyObjectActiveRepository INSTANCE = new MyObjectActiveRepository();

    private Field groupField;

    private Map<String, MyObject> groupNameToActiveEntity = new ConcurrentHashMap<String, MyObject>();

    private MyObjectActiveRepository() {
        try {
            groupField = MyObject.class.getField("group");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        groupNameToActiveEntity = new ConcurrentHashMap<String, MyObject>();
    }

    public void registerActiveForItsGroup(MyObject active) {
        String groupName;

        try {
            groupName = (String)groupField.get(active);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (groupName == null) {
            throw new RuntimeException("Don't track entities with null group");
        }

        this.groupNameToActiveEntity.put(groupName, active);
    }

    // add additional constructor argument for passing here
    public void putToGroup(MyObject myObject, String groupName/*,...*/) {
        try {
            groupField.set(myObject, groupName);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public MyObject getActiveForGroup(String groupName) {
        if (groupName == null) {
            throw new RuntimeException("Don't track entities with null group");
        }
        return this.groupNameToActiveEntity.get(groupName);
    }
}
