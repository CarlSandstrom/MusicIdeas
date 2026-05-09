package com.musicideas.data.audio.encoding

import com.musicideas.core.model.MusicIdea
import com.musicideas.data.audio.config.AudioFormatConfig.format
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.javacv.FFmpegFrameRecorder
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer
import javax.sound.sampled.AudioInputStream

class Mp3Encoder {
    suspend fun encode(musicIdea: MusicIdea, outputPath: String) {
        encodeToFile(musicIdea.audioDataProvider(), outputPath)
    }

    fun encodeToBytes(rawPcm: ByteArray): ByteArray {
        val tempFile = File.createTempFile("musicideas_", ".mp3")
        try {
            encodeToFile(rawPcm, tempFile.absolutePath)
            return tempFile.readBytes()
        } finally {
            tempFile.delete()
        }
    }

    private fun encodeToFile(rawPcm: ByteArray, outputPath: String) {
        val audioFormat = format
        val audioInputStream = AudioInputStream(
            ByteArrayInputStream(rawPcm),
            audioFormat,
            rawPcm.size.toLong()
        )

        val recorder = FFmpegFrameRecorder(File(outputPath), audioFormat.channels).apply {
            sampleRate = audioFormat.sampleRate.toInt()
            audioCodec = avcodec.AV_CODEC_ID_MP3
            audioBitrate = 192000
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
                recorder.recordSamples(audioFormat.sampleRate.toInt(), audioFormat.channels, shortBuffer)
            }
        } finally {
            recorder.stop()
            recorder.release()
            audioInputStream.close()
        }
    }
}
