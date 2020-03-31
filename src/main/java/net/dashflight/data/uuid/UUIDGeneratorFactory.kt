package net.dashflight.data.uuid;

import java.util.HashMap;
import java.util.Map;

public class UUIDGeneratorFactory {

    private static final Map<Class<?>, UUIDGenerator> instances = new HashMap<>();


    public static UUIDGenerator getDefault() {
        return getInstance(TimeBasedUUIDGenerator.class);
    }

    public static <T extends UUIDGenerator> UUIDGenerator getInstance(Class<T> clazz) {
        if (!instances.containsKey(clazz)) {
            if (clazz == TimeBasedUUIDGenerator.class) {
                instances.put(clazz, new TimeBasedUUIDGenerator());
            }
        }

        if (!instances.containsKey(clazz)) {
            throw new IllegalArgumentException(String.format("There is no generator associated with the class `%s`", clazz));
        }

        return instances.get(clazz);
    }
}
