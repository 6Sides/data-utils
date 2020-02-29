package net.dashflight.data.config;

import java.lang.reflect.Field;

public class ValueInjector {

    public static void inject(Object source, ConfigurationData<?> props) {
        Class<?> clazz = source.getClass().equals(Class.class) ? ((Class<?>) source) : source.getClass();

        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigValue.class)) {

                    String key = field.getAnnotation(ConfigValue.class).value();

                    field.setAccessible(true);

                    Class<?> fieldType = field.getType();
                    Object value;

                    String configValue = (String) props.get(key);

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
