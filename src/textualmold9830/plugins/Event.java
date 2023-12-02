package textualmold9830.plugins;

public class Event {
    private String name;
    public String getEventName() {
        if (name == null) {
            name = getClass().getSimpleName();
        }
        return name;
    }

}
