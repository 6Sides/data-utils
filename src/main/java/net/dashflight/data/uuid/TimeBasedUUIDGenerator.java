package net.dashflight.data.uuid;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import java.util.UUID;

public class TimeBasedUUIDGenerator implements UUIDGenerator {

    private static final TimeBasedGenerator gen = Generators.timeBasedGenerator();

    TimeBasedUUIDGenerator() {}

    @Override
    public UUID next() {
        return gen.generate();
    }
}
