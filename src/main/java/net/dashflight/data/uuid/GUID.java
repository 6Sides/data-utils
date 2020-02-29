package net.dashflight.data.uuid;

/**
 * @param <T> The java type the guid resolves to
 */
public abstract class GUID<T>  {

    protected abstract String asString();

    @Override
    public String toString() {
        return this.asString();
    }

}
