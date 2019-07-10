package sgr.st;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class AudioRecorder {
	public static final int HEADER_SIZE_ALL = 44;	// waveヘッダ全体のサイズ
	public static final int HEADER_SIZE_RIFF = 8;	// RIFF ヘッダのサイズ
	public static final int CHUNCK_SIZE_PCM = 16;	// PCMチャンクのサイズ

	public static final String riff_ChunkID = "RIFF";
	public static final String fmt_ChunkID = "fmt ";
	public static final String data_ChunkID = "data";

	private AudioFormat audioFormat;
	private ByteArrayOutputStream baos;


	public AudioRecorder(AudioFormat audioFormat) {
		this.audioFormat = audioFormat;
		this.baos = new ByteArrayOutputStream();
	}
	public AudioRecorder() {
		this(new AudioFormat(
						AudioConstants.sampleRate,
						AudioConstants.sampleSizeInBits_PCM,
						AudioConstants.channels,
						true,
						AudioConstants.isBigEndian
						)
				);
	}

	public void write(byte[] data, int off, int length) {
		baos.write(data, off, length);
	}

	public void write(byte[] data) {
		write(data, 0, data.length);
	}

	public AudioInputStream getStream() throws IOException {
		baos.flush();
		byte[] data = baos.toByteArray();
		// note : FrameSize = (SampleSizeInBits+7)/8*Channel
		long framesNum = data.length / audioFormat.getFrameSize();
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		return new AudioInputStream(bais, audioFormat, framesNum);
	}

	/**
	 * .wav拡張子で音声データを保存します。
	 * @param fileName
	 */
	public void save(String filePath) {
		File file = new File(filePath);
		try {
			AudioSystem.write(this.getStream(), AudioFileFormat.Type.WAVE , file);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void close() {
	}
}
