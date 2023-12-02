package textualmold9830.plugins;

public interface Plugin {

    String getName();

    default String getVersion(){
        return "";
    };

    void initialize();

    default void shutdown(){}
    default void handleEvent(Event event){}
}
