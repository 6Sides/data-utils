package net.dashflight.data.serialize

interface Serializer {

    fun readObject(data: ByteArray): Any?

    fun writeObject(data: Any?): ByteArray?

}