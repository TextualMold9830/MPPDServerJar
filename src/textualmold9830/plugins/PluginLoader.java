package textualmold9830.plugins;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    private static final String PLUGINS_DIRECTORY = "plugins";

    public static List<Plugin> loadPlugins() throws IOException, ClassNotFoundException {
        List<Plugin> plugins = new ArrayList<>();

        File pluginDirectory = new File(PLUGINS_DIRECTORY);
        System.out.println("Found plugins: " + Arrays.toString(Arrays.stream(pluginDirectory.listFiles()).filter((file -> file.getName().endsWith(".jar"))).toArray()));
        if (pluginDirectory.exists() && pluginDirectory.isDirectory()) {
            for (File jarFile : pluginDirectory.listFiles()) {
                if (jarFile.getName().endsWith(".jar")) {
                    System.out.println("loading: " + jarFile.getName());
                    URL jarURL = jarFile.toURI().toURL();
                    URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{jarURL});
                    JarFile jar = new JarFile(jarFile);
                    Enumeration<JarEntry> entries = jar.entries();

                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (entryName.endsWith(".class") && !entryName.contains("META-INF")) {
                            String className = entryName.substring(0, entryName.lastIndexOf(".class")).replace("/", ".");
                            Class<?> clazz = classLoader.loadClass(className);
                            if (Plugin.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                                // Create an instance of the plugin class
                                Plugin plugin = null;
                                try {
                                    plugin = (Plugin) clazz.getDeclaredConstructor().newInstance();
                                } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                                         IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                plugins.add(plugin);
                                System.out.println("found plugin: " + plugin.getName());
                            }
                        }
                    }
                    classLoader.close(); // Close the class loader to release resources
                    jar.close();
                }
            }
        }

        return plugins;
    }


}
