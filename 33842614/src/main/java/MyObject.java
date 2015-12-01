/**
* @author Dmitry Spikhalskiy <dmitry@spikhalskiy.com>
*/
public final class MyObject {
    private String state;

    public void modify(String newState) {
        this.state = newState;
    }

    public String getState() {
        return state;
    }

    public MyObject copy() {
        MyObject cloned = new MyObject();
        cloned.modify(state);
        return cloned;
    }
}