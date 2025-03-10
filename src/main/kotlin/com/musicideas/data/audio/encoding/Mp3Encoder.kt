package com.musicideas.data.audio.encoding

import com.musicideas.core.model.MusicIdea
import com.musicideas.data.audio.config.AudioFormatConfig.format
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.javacv.FFmpegFrameRecorder
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ShortBuffer
import javax.sound.sampled.AudioInputStream

class Mp3Encoder {
    suspend fun encode(musicIdea: MusicIdea, outputPath: String) {
        // Encoding logic

        val rawData = musicIdea.audioDataProvider()

        val inputStream = ByteArrayInputStream(rawData)
        val audioInputStream = AudioInputStream(
            inputStream,
            format,
            rawData.size.toLong()
        )

        val recorder = FFmpegFrameRecorder(File(outputPath), format.channels).apply {
            sampleRate = sampleRate
            audioCodec = avcodec.AV_CODEC_ID_MP3
            audioBitrate = 192000  // 192kbps
            format = "mp3"
        }
        recorder.audioChannels = format.channels

        try {
            recorder.start()
            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (audioInputStream.read(buffer).also { bytesRead = it } != -1) {
                val shortBuffer = ShortBuffer.allocate(bytesRead / 2)
                ByteBuffer.wrap(buffer, 0, bytesRead)
                    .asShortBuffer()
                    .get(shortBuffer.array(), 0, bytesRead / 2)

                recorder.recordSamples(format.sampleRate.toInt(), format.channels, shortBuffer)
            }
        } finally {
            recorder.stop()
            recorder.release()
            audioInputStream.close()
        }

    }
}