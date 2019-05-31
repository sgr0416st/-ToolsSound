package sgr.st.sound.old;

import java.io.ByteArrayInputStream;
import java.net.DatagramSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import sgr.st.rtp.lib.RTPPacketReceiver;
import sgr.st.rtp.lib.RTPReceiveThread;
import sgr.st.thread.lib.ICallback;
import sgr.st.thread.lib.ThreadOperater;

public class VoicePacketReceiver extends ThreadOperater<RTPReceiveThread>
implements ICallback<ByteArrayInputStream, Void>{

	protected ICallback<AudioInputStream, Void> callback_method;
	protected RTPPacketReceiver receiver;
	protected AudioFormat ulawFormat;
	protected AudioFormat linearFormat;
	protected AudioInputStream ulawStream;
	protected AudioInputStream linearStream;
	protected ByteArrayInputStream byteArrayInputStream;
	protected int max_size;

	/**
	 * 指定したSocketを使用して、RTPパケットを待ち受け、
	 * RTPパケットから指定されたフォーマットに基づきオーディオストリームに翻訳するノンブロッキングな受信機を生成します。
	 * 生成したオーディオストリームは、コールバックメソッド内で利用できます。
	 *　
	 * @param socket RTP通信に使用するソケット
	 * @param callback_method 受信時に制御を渡すコールバック関数。この関数はデータ受信時にそのAudioStreamを受け取る
	 * @param ulawFormat ulaw通信規格に乗っ取ったフォーマット。送信時のデータと整合されている必要がある。
	 * @param liAudioFormat 再生するPCMのフォーマット
	 */
	public VoicePacketReceiver(DatagramSocket socket, ICallback<AudioInputStream, Void> callback_method,
			AudioFormat ulawFormat, AudioFormat liAudioFormat, int max_size) {
		this.callback_method = callback_method;
		this.ulawFormat = ulawFormat;
		this.linearFormat = liAudioFormat;
		this.max_size = max_size;
		ulawStream = null;
		linearStream = null;
		byteArrayInputStream = null;
		receiver = new RTPPacketReceiver(socket, this, this.max_size);
		this.setThread(receiver.getThread());
	}

	/**
	 * 指定したSocketを使用して、RTPパケットを待ち受け、
	 * RTPパケットからオーディオストリームに翻訳するノンブロッキングな受信機を生成します。
	 * 生成したオーディオストリームは、コールバックメソッド内で利用できます。
	 * AudioフォーマットはSoundCommunicationRulesクラスに記載されているデフォルトのルールを用いて生成されます。
	 *
	 * @param socket RTP通信に使用するソケット
	 * @param callback_method 受信時に制御を渡すコールバック関数。この関数はデータ受信時にそのAudioStreamを受け取る
	 */
	public VoicePacketReceiver(DatagramSocket socket, ICallback<AudioInputStream, Void> callback_method,
			int max_size) {
		this(
				socket,
				callback_method,
				new AudioFormat(
						AudioFormat.Encoding.ULAW,
						SoundRules.sampleRate,
						SoundRules.sampleSizeInBits_ulaw,
						SoundRules.channels,
						SoundRules.frameSize,
						SoundRules.frameRate,
						SoundRules.isBigEndian
						),
				new AudioFormat(
						SoundRules.sampleRate,
						SoundRules.sampleSizeInBits_PCM,
						SoundRules.channels,
						true,
						SoundRules.isBigEndian
						),
				max_size
				);
	}

	public VoicePacketReceiver(DatagramSocket socket, ICallback<AudioInputStream, Void> callback_method) {
		this(socket, callback_method,SoundRules.SIZE_MAX_DATA_ULAW);
	}

	/* (非 Javadoc)
	 * @see sgr.st.callback.lib.ICallback#callback(java.lang.Object)
	 * 受信したパケットをAudioデータのストリームに変換して返すコールバック関数.
	 */
	public Void callback(ByteArrayInputStream byteArrayInputStream) {
		ulawStream = new AudioInputStream(byteArrayInputStream,ulawFormat, this.max_size);
		// G.711 u-law からリニアPCM 16bit 8000Hzへ変換
		linearStream = AudioSystem.getAudioInputStream(linearFormat,ulawStream);
		return this.callback_method.callback(linearStream);
	}

}