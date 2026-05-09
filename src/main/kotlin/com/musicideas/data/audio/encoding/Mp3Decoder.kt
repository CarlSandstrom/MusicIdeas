package com.musicideas.data.audio.encoding

import org.bytedeco.javacv.FFmpegFrameGrabber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer

class Mp3Decoder {
    fun decode(mp3Bytes: ByteArray): ByteArray {
        val grabber = FFmpegFrameGrabber(ByteArrayInputStream(mp3Bytes))
        val output = ByteArrayOutputStream()
        try {
            grabber.start()
            var frame = grabber.grabSamples()
            while (frame != null) {
                frame.samples?.forEach { buffer ->
                    val shortBuffer = buffer as ShortBuffer
                    shortBuffer.rewind()
                    val byteBuffer = ByteBuffer.allocate(shortBuffer.remaining() * 2)
                    byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
                    while (shortBuffer.hasRemaining()) {
                        byteBuffer.putShort(shortBuffer.get())
                    }
                    output.write(byteBuffer.array())
                }
                frame = grabber.grabSamples()
            }
        } finally {
            grabber.stop()
            grabber.release()
        }
        return output.toByteArray()
    }
}
