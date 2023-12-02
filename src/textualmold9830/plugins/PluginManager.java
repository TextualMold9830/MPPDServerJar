package textualmold9830.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PluginManager {

    private List<Plugin> plugins = new ArrayList<>();

    public void loadPlugins() {
        try {
            plugins = PluginLoader.loadPlugins();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("plugin_size: "+plugins.size());
        for (Plugin plugin : plugins) {
            plugin.initialize();
        }
    }

    public void shutdownPlugins() {
        for (Plugin plugin : plugins) {
            plugin.shutdown();
        }
    }

    public void addPlugin(Plugin plugin) {
        plugins.add(plugin);
        plugin.initialize();
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }
    public void fireEvent(Event event){
        plugins.forEach((plugin)->plugin.handleEvent(event));
    }
    public void removePlugin(String pluginName){
        for (Plugin plugin: plugins) {
            if (plugin.getName().equals(pluginName)){
                plugins.remove(plugin);
                plugin.shutdown();

            }
        }
    }
}
