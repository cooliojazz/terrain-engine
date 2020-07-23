package com.up.terrainengine.operator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ricky
 */
public class Operators {

    private static Class<? extends Operator>[] operators = null;
    
    public static Class<? extends Operator>[] getOperators() {
        if (operators == null) {
            try {
                String packageName = Operators.class.getPackage().getName() + ".operators";
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                assert classLoader != null;
                String path = packageName.replace('.', '/');
                Enumeration<URL> resources = classLoader.getResources(path);
                ArrayList<File> dirs = new ArrayList<>();
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    dirs.add(new File(resource.getFile()));
                }
                operators = dirs.stream().flatMap(d -> findOperators(d, packageName).stream()).toArray(Class[]::new);
            } catch (IOException ex) {
                Logger.getLogger(Operators.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return operators;
    }

    private static ArrayList<Class> findOperators(File directory, String packageName) {
        ArrayList<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findOperators(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    Class c = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                    if (Operator.class.isAssignableFrom(c)) classes.add(c);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operators.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return classes;

    }

}
