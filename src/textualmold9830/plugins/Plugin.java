package textualmold9830.plugins;

public interface Plugin {

    String getName();

    default String getVersion(){
        return "";
    };

    void initialize();

    void shutdown();
    default void playerJoinEvent(){}
}
