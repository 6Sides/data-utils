package net.dashflight.data.random;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import javax.xml.bind.DatatypeConverter;

class LavaRandomSpi extends SecureRandomSpi {

    private static final int BUFFER_SIZE = 64 * 128;
    private static final ArrayBlockingQueue<Byte> BUFFER = new ArrayBlockingQueue<>(BUFFER_SIZE);

    private static final Runnable producer = new LavaRandomSpi.LavaRandomGenerator();

    private final SecureRandom backupRandom = new SecureRandom();

    static {
        Thread t = new Thread(producer);
        t.setDaemon(true);
        t.start();
    }

    @Override
    protected void engineSetSeed(byte[] seed) {
        throw new UnsupportedOperationException("Lava Random can't use a seed. It's too random :-)");
    }

    @Override
    protected void engineNextBytes(byte[] bytes) {
        synchronized (BUFFER) {
            if (BUFFER.size() >= bytes.length) {
                try {
                    for (int i = 0; i < bytes.length; i++) {
                        Byte next = BUFFER.take();
                        bytes[i] = next;
                    }
                } catch (Exception e) {
                    backupRandom.nextBytes(bytes);
                }
            } else {
                backupRandom.nextBytes(bytes);
            }
        }
    }

    @Override
    protected byte[] engineGenerateSeed(int numBytes) {
        throw new UnsupportedOperationException("Lava Random can't use a seed. It's too random :-)");
    }


    /**
     * Background thread that fetches random bytes from the server and puts them in the buffer.
     */
    private static class LavaRandomGenerator implements Runnable {

        private static final String ENDPOINT = "http://173.68.124.69:5000/bytes?length=512";

        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public void run() {
            while (true) {
                try {
                    Map<String, String> res = mapper.readValue(new URL(ENDPOINT), new TypeReference<Map<String, String>>(){});

                    // Prevents bug in random service where it returns odd length hex string
                    String bytes = res.get("value");
                    if (bytes.length() %2 == 1) {
                        bytes = bytes.substring(1);
                    }

                    synchronized (BUFFER) {
                        if (BUFFER.size() > BUFFER_SIZE * 0.75) {
                            continue;
                        }

                        for (byte b : DatatypeConverter.parseHexBinary(bytes)) {
                            BUFFER.offer(b);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
