package textualmold9830.plugins;

import io.github.classgraph.ClassGraph;

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

            try (final var scanResults = new ClassGraph()
                    .acceptPaths(PLUGINS_DIRECTORY)
                    .enableClassInfo()
                    .scan()
            ) {
                // Find which classes extend Plugin, then use loadClass() on it (ClassInfo)
                scanResults.getClassesImplementing(Plugin.class).forEach((ci)->
                        {
                            try {
                                plugins.add((Plugin) ci.loadClass().getDeclaredConstructor().newInstance());
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                     NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
            }

            return plugins;
        }
}
