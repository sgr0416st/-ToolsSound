package sgr.st.sound.old;

import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import sgr.st.rtp.lib.RTPSendThread;
import sgr.st.thread.lib.ICallback;
import sgr.st.thread.lib.ThreadOperater;

public class RealtimeRecordSoundSender extends ThreadOperater<RTPSendThread>
implements ICallback<Void, AudioInputStream>{

	protected DatagramSocket socket;
	protected AudioFormat linearFormat;
	protected DataLine.Info info;
	protected TargetDataLine targetDataLine;
	protected VoicePacketSender sender;
	protected  byte[] linearBuffer;
	protected AudioInputStream linearStream;

	public RealtimeRecordSoundSender(String destIP, int max_size) {
		linearFormat = new AudioFormat(
				SoundRules.sampleRate,
				SoundRules.sampleSizeInBits_PCM,
				SoundRules.channels,
				true,
				SoundRules.isBigEndian
				);
		this.info = new DataLine.Info(SourceDataLine.class,linearFormat);
		this.linearBuffer = new byte[max_size*2];
		try {
			info = new DataLine.Info(TargetDataLine.class,linearFormat);
			targetDataLine = (TargetDataLine)AudioSystem.getLine(info);
			targetDataLine.open(linearFormat);
			targetDataLine.start();
			socket = new DatagramSocket(SoundRules.PORT_RTP_SOUND_SEND, InetAddress.getLocalHost());
			sender = new VoicePacketSender(socket, destIP, String.valueOf(SoundRules.PORT_RTP_SOUND_RCEIVE),
					this, max_size);
			this.setThread(sender.getThread());

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public RealtimeRecordSoundSender(String destIP) {
		this(destIP, SoundRules.SIZE_MAX_DATA_ULAW);
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void restart() {
		super.restart();
		targetDataLine.start();
	}

	@Override
	public void halt() {
		// TODO 自動生成されたメソッド・スタブ
		super.halt();
		targetDataLine.stop();
	}

	@Override
	public void close() {
		super.close();
		targetDataLine.close();
	}

	@Override
	public AudioInputStream callback(Void param) {
		if(!this.targetDataLine.isActive())
			linearStream = new AudioInputStream(targetDataLine);
		else
			linearStream = null;
		return linearStream;
	}

}
