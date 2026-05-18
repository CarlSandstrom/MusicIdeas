package com.musicideas.data.audio.encoding

import com.musicideas.core.model.AudioQuality
import com.musicideas.core.model.MusicIdea
import com.musicideas.data.audio.config.AudioFormatConfig
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.javacv.FFmpegFrameRecorder
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream

class Mp3Encoder {
    suspend fun encode(musicIdea: MusicIdea, outputPath: String) {
        encodeToFile(musicIdea.audioDataProvider(), musicIdea.metadata.sampleRate, outputPath)
    }

    fun encodeToBytes(rawPcm: ByteArray, sampleRate: Int = AudioFormatConfig.format.sampleRate.toInt(), quality: AudioQuality = AudioQuality.HIGH): ByteArray {
        val tempFile = File.createTempFile("musicideas_", ".mp3")
        try {
            encodeToFile(rawPcm, sampleRate, tempFile.absolutePath, quality)
            return tempFile.readBytes()
        } finally {
            tempFile.delete()
        }
    }

    private fun encodeToFile(rawPcm: ByteArray, sampleRate: Int, outputPath: String, quality: AudioQuality = AudioQuality.HIGH) {
        val audioFormat = AudioFormat(sampleRate.toFloat(), 16, 1, true, false)
        val audioInputStream = AudioInputStream(
            ByteArrayInputStream(rawPcm),
            audioFormat,
            rawPcm.size.toLong()
        )
        val bitrate = when (quality) {
            AudioQuality.LOW -> 128000
            AudioQuality.MEDIUM -> 192000
            AudioQuality.HIGH -> 320000
        }

        val recorder = FFmpegFrameRecorder(File(outputPath), audioFormat.channels).apply {
            this.sampleRate = sampleRate
            audioCodec = avcodec.AV_CODEC_ID_MP3
            audioBitrate = bitrate
            format = "mp3"
        }
        recorder.audioChannels = audioFormat.channels

        try {
            recorder.start()
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (audioInputStream.read(buffer).also { bytesRead = it } != -1) {
                val shortBuffer = ShortBuffer.allocate(bytesRead / 2)
                ByteBuffer.wrap(buffer, 0, bytesRead)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .asShortBuffer()
                    .get(shortBuffer.array(), 0, bytesRead / 2)
                recorder.recordSamples(sampleRate, audioFormat.channels, shortBuffer)
            }
        } finally {
            recorder.stop()
            recorder.release()
            audioInputStream.close()
        }
    }
}
