package net.dashflight.data.helpers.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.util.Pool;
import java.util.HashMap;
import java.util.Map;

/**
 * Based on https://github.com/EsotericSoftware/kryo#pooling
 */
public class KryoPool {

    // Stores classes Kryo needs to register on creation
    private static final Map<Class<?>, Serializer<?>> registeredClasses = new HashMap<>();

    private static final Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 4) {
        protected Kryo create () {
            Kryo kryo = new Kryo();

            synchronized (registeredClasses) {
                registeredClasses.forEach((clazz, serializer) -> {
                    if (serializer == null) {
                        kryo.register(clazz, Math.abs(clazz.hashCode()) + 12);
                    } else {
                        kryo.register(clazz, serializer, Math.abs(clazz.hashCode()) + 12);
                    }
                });
            }

            return kryo;
        }
    };


    public static Pool<Kryo> getPool() {
        return kryoPool;
    }


    /**
     * Registers a class with an id based on its hashcode. 12 is added to ensure
     * there is no version clash with Kryo's default registered classes.
     */
    public static <T> void registerClass(Class<T> clazz) {
        synchronized (registeredClasses) {
            registeredClasses.put(clazz, null);
        }
    }

    public static <T> void registerClass(Class<T> clazz, Serializer<T> serializer) {
        synchronized (registeredClasses) {
            registeredClasses.put(clazz, serializer);
        }
    }
}
