package net.dashflight.data.uuid;

import de.huxhorn.sulky.ulid.ULID.Value;
import java.util.UUID;

public class ULID extends GUID<UUID> {

    private UUID id;

    public ULID(UUID id) {
        this.id = id;
    }

    public static GUID<UUID> fromString(String guid) {
        return new ULID(UUID.fromString(guid));
    }

    /**
     * Returns timestamp of the time the guid was created. Timestamp is milliseconds
     * since epoch.
     */
    public long getTimestamp() {
        return new Value(id.getMostSignificantBits(), id.getLeastSignificantBits()).timestamp();
    }

    @Override
    protected String asString() {
        return id.toString();
    }
}
