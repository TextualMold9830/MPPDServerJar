package textualmold9830.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PluginManager {

    private List<Plugin> plugins = new ArrayList<>();

    public void loadPlugins() {
        PluginLoader loader = new PluginLoader();
        try {
            plugins = loader.loadPlugins();
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
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }
    public void fireEvent(Event event){
        plugins.forEach((plugin)->plugin.handleEvent(event));
    }
}
