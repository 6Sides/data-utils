package net.dashflight.data.uuid;

/**
 * Generates a globally unique id value
 */
public interface GUIDGenerator<T extends GUID<?>> {

    T next();

}
