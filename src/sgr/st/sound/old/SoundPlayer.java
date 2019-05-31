package sgr.st.sound.old;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SoundPlayer{

	protected AudioFormat format;
	protected DataLine.Info info;
	protected SourceDataLine sourceDataLine;
	protected byte[] buffer;

	public SoundPlayer(int max_size, AudioFormat format) throws LineUnavailableException {
		this.format = format;
		this.buffer = new byte[max_size*2];
		this.info = new DataLine.Info(SourceDataLine.class,format);
		this.sourceDataLine = (SourceDataLine)AudioSystem.getLine(info);
		sourceDataLine.open(format);
		this.sourceDataLine.start();
	}

	public SoundPlayer(int max_size) throws LineUnavailableException {
		this(max_size, new AudioFormat(
				SoundRules.sampleRate,
				SoundRules.sampleSizeInBits_PCM,
				SoundRules.channels,
				true,
				SoundRules.isBigEndian) );
	}

	public SoundPlayer() throws LineUnavailableException {
		this(SoundRules.SIZE_MAX_DATA_ULAW);
	}

	public void restart() {
		this.sourceDataLine.start();
	}

	public void halt() {
		this.sourceDataLine.stop();
	}

	public void close() {
		this.sourceDataLine.close();
	}

	public Void write(AudioInputStream stream) {
		try {
			stream.read(buffer,0,buffer.length);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		this.sourceDataLine.write(buffer,0,buffer.length);
		return null;
	}

}
