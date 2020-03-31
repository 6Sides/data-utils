package net.dashflight.data.random

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.net.URL
import java.security.SecureRandom
import java.security.SecureRandomSpi
import java.util.concurrent.ArrayBlockingQueue
import javax.xml.bind.DatatypeConverter

internal class LavaRandomSpi : SecureRandomSpi() {

    private val backupRandom = SecureRandom()

    companion object {
        private const val BUFFER_SIZE = 64 * 128
        private val BUFFER = ArrayBlockingQueue<Byte>(BUFFER_SIZE)
        private val producer: Runnable = LavaRandomGenerator()

        init {
            val t = Thread(producer)
            t.isDaemon = true
            t.start()
        }
    }

    override fun engineSetSeed(seed: ByteArray) {
        throw UnsupportedOperationException("Lava Random can't use a seed. It's too random :-)")
    }

    override fun engineNextBytes(bytes: ByteArray) {
        synchronized(BUFFER) {
            if (BUFFER.size >= bytes.size) {
                try {
                    for (i in bytes.indices) {
                        val next = BUFFER.take()
                        bytes[i] = next
                    }
                } catch (e: Exception) {
                    backupRandom.nextBytes(bytes)
                }
            } else {
                backupRandom.nextBytes(bytes)
            }
        }
    }

    override fun engineGenerateSeed(numBytes: Int): ByteArray {
        throw UnsupportedOperationException("Lava Random can't use a seed. It's too random :-)")
    }

    /**
     * Background thread that fetches random bytes from the server and puts them in the buffer.
     */
    private class LavaRandomGenerator : Runnable {
        override fun run() {
            while (true) {
                try {
                    val res: Map<String, String> = mapper.readValue(URL(ENDPOINT), object : TypeReference<Map<String, String>>() {})

                    // Prevents bug in random service where it returns odd length hex string
                    var bytes = res.getOrDefault("value", "")
                    if (bytes.length % 2 == 1) {
                        bytes = bytes.substring(1)
                    }
                    synchronized(BUFFER) {
                        if (BUFFER.size > BUFFER_SIZE * 0.75) {
                            // TODO: Check if this works
                            return@synchronized
                        }
                        for (b in DatatypeConverter.parseHexBinary(bytes)) {
                            BUFFER.offer(b)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    Thread.sleep(1000)
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                }
            }
        }

        companion object {
            private const val ENDPOINT = "http://173.68.124.69:5000/bytes?length=512"
            private val mapper = ObjectMapper()
        }
    }
}