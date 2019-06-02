package sgr.st.sound._old;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import sgr.st._old.rtp.lib.RTPReceiveThread;
import sgr.st.thread.lib.ICallback;
import sgr.st.thread.lib.ThreadOperater;


public class RealtimeReceiveSoundPlayer extends ThreadOperater<RTPReceiveThread> implements ICallback<AudioInputStream, Void>{

	protected DatagramSocket socket;
	protected AudioFormat linearFormat;
	protected DataLine.Info info;
	protected SourceDataLine sourceDataLine;
	protected VoicePacketReceiver receiver;
	protected byte[] linearBuffer;

	public RealtimeReceiveSoundPlayer(int max_size, AudioFormat linerformat) {
		this.linearFormat = linerformat;
		this.linearBuffer = new byte[max_size*2];
		try {
			socket = new DatagramSocket(SoundRules.PORT_RTP_SOUND_RCEIVE, InetAddress.getLocalHost());
			this.info = new DataLine.Info(SourceDataLine.class,linearFormat);
			this.sourceDataLine = (SourceDataLine)AudioSystem.getLine(info);
			sourceDataLine.open(linearFormat);
			this.sourceDataLine.start();
			receiver = new VoicePacketReceiver(socket, this, max_size);
		} catch(Exception e) {
			e.printStackTrace();
		}
		this.setThread(receiver.getThread());
	}

	public RealtimeReceiveSoundPlayer(int max_size) {
		this(max_size, new AudioFormat(
				SoundRules.sampleRate,
				SoundRules.sampleSizeInBits_PCM,
				SoundRules.channels,
				true,
				SoundRules.isBigEndian) );
	}

	public RealtimeReceiveSoundPlayer() {
		this(SoundRules.SIZE_MAX_DATA_ULAW);
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void restart() {
		super.restart();
		this.sourceDataLine.start();
	}

	@Override
	public void halt() {
		// TODO 自動生成されたメソッド・スタブ
		super.halt();
		this.sourceDataLine.stop();
	}

	@Override
	public void close() {
		super.close();
		this.sourceDataLine.close();
	}

	@Override
	public Void callback(AudioInputStream linearStream) {

		try {
			linearStream.read(linearBuffer,0,linearBuffer.length);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		this.sourceDataLine.write(linearBuffer,0,linearBuffer.length);
		return null;
	}

}
