package net.dashflight.data;

import java.lang.reflect.Field;
import java.util.Properties;

public class ValueInjector {

    public void inject(Object source, Properties props) {
        try {
            for (Field field : source.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigValue.class)) {

                    String key = field.getAnnotation(ConfigValue.class).value();

                    field.setAccessible(true);

                    Class<?> fieldType = field.getType();
                    Object value;

                    String configValue = props.getProperty(key);

                    if (configValue == null) {
                        continue;
                    }

                    try {
                        if (fieldType == Integer.class || fieldType == int.class) {
                            value = Integer.parseInt(configValue);
                        } else if (fieldType == Double.class || fieldType == double.class) {
                            value = Double.parseDouble(configValue);
                        } else if (fieldType == Float.class || fieldType == float.class) {
                            value = Float.parseFloat(configValue);
                        } else if (fieldType == Short.class || fieldType == short.class) {
                            value = Short.parseShort(configValue);
                        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                            value = Boolean.parseBoolean(configValue);
                        } else {
                            value = configValue;
                        }

                        field.set(source, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
