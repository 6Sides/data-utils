package net.dashflight.data.uuid;

import de.huxhorn.sulky.ulid.ULID.Value;
import java.util.UUID;

public class ULIDGenerator implements GUIDGenerator<ULID> {

    private static final de.huxhorn.sulky.ulid.ULID gen = new de.huxhorn.sulky.ulid.ULID();

    @Override
    public ULID next() {
        Value val = gen.nextValue();
        return new ULID(new UUID(val.getMostSignificantBits(), val.getLeastSignificantBits()));
    }

}
