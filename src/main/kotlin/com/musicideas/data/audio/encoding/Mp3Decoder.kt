package com.musicideas.data.audio.encoding

import org.bytedeco.javacv.FFmpegFrameGrabber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer

data class DecodedAudio(val pcmBytes: ByteArray, val sampleRate: Int)

class Mp3Decoder {
    fun decode(encodedBytes: ByteArray): DecodedAudio {
        val grabber = FFmpegFrameGrabber(ByteArrayInputStream(encodedBytes))
        val output = ByteArrayOutputStream()
        try {
            grabber.audioChannels = 1
            grabber.start()
            val sampleRate = grabber.sampleRate
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
            return DecodedAudio(output.toByteArray(), sampleRate)
        } finally {
            grabber.stop()
            grabber.release()
        }
    }
}
