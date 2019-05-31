package sgr.st.sound.old;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import sgr.st.rtp.lib.RTPSendThread;
import sgr.st.rtp.lib.RTPpacketSender;
import sgr.st.thread.lib.ICallback;
import sgr.st.thread.lib.ThreadOperater;

public class VoicePacketSender extends ThreadOperater<RTPSendThread>
implements ICallback<Void, ByteArrayOutputStream>{

	protected RTPpacketSender sender;
	protected AudioFormat linearFormat;
	protected AudioFormat ulawFormat;
	protected DataLine.Info info;
	protected TargetDataLine targetDataLine;
	protected AudioInputStream linearStream;
	protected AudioInputStream ulawStream;
	protected ByteArrayOutputStream byteArrayOutputStream;
	protected ICallback<Void, AudioInputStream> callback_method;
	private byte[] voicePacket;
	protected int max_size;

	/**
	 * callbackメソッドにより生成された音声ストリームを、指定したフォーマットに基づきRTPパケットに加工し、
	 * 指定したソケットを使用して、指定した宛先IPアドレス、指定した宛先UDPポート番号へ送信するRTPパケット送信機を生成します
	 * ディフォルトのメディアタイプは、G.711 u-law(0)を使用します
	 *
	 * @param socket RTP通信に使用するパケット.
	 * @param destIP 通信相手のIPアドレス
	 * @param destPort 通信相手のポート番号
	 * @param callback_method 送信前に制御を渡すコールバック関数。この関数はデータ送信前にそのAudioStreamを生成する。
	 * @param ulawFormat ulaw通信規格に乗っ取ったフォーマット。受信時のデータと整合されている必要がある。
	 * @param liAudioFormat 入力として受け取る音声のPCMフォーマット
	 */
	public VoicePacketSender(DatagramSocket socket,String destIP,String destPort,
			ICallback<Void, AudioInputStream> callback_method,
			AudioFormat linearFormat, AudioFormat ulawFormat, int max_size) {
		this.callback_method = callback_method;
		this.linearFormat = linearFormat;
		this.ulawFormat = ulawFormat;
		this.max_size = max_size;
		sender = new RTPpacketSender(socket, destIP, destPort, this, max_size);
		this.setThread(sender.getThread());
		linearStream = this.callback_method.callback(null);
		ulawStream = AudioSystem.getAudioInputStream(ulawFormat,linearStream);
	}



	/**
	 * callbackメソッドにより生成された音声ストリームを、指定したフォーマットに基づきRTPパケットに加工し、
	 * 指定したソケットを使用して、指定した宛先IPアドレス、指定した宛先UDPポート番号へ送信するRTPパケット送信機を生成します
	 * ディフォルトのメディアタイプは、G.711 u-law(0)を使用します
	 * AudioフォーマットはSoundCommunicationRulesクラスに記載されているデフォルトのルールを用いて生成されます。
	 *
	 * @param socket RTP通信に使用するパケット.
	 * @param destIP 通信相手のIPアドレス
	 * @param destPort 通信相手のポート番号
	 * @param callback_method 送信前に制御を渡すコールバック関数。この関数はデータ送信前にそのAudioStreamを生成する。
	 * @param max_size 送信パケットのデータ部分の最大サイズ
	 */
	public VoicePacketSender(DatagramSocket socket,String destIP,String destPort,
			ICallback<Void, AudioInputStream> callback_method, int max_size)
	{
		this(socket, destIP, destPort, callback_method,
				new AudioFormat(
						SoundRules.sampleRate,
						SoundRules.sampleSizeInBits_PCM,
						SoundRules.channels,
						true,
						SoundRules.isBigEndian
						),
				new AudioFormat(
						AudioFormat.Encoding.ULAW,
						SoundRules.sampleRate,
						SoundRules.sampleSizeInBits_ulaw,
						SoundRules.channels,
						SoundRules.frameSize,
						SoundRules.frameRate,
						SoundRules.isBigEndian
						),
				max_size
				);
	}

	/**
	 * callbackメソッドにより生成された音声ストリームを、指定したフォーマットに基づきRTPパケットに加工し、
	 * 指定したソケットを使用して、指定した宛先IPアドレス、指定した宛先UDPポート番号へ送信するRTPパケット送信機を生成します
	 * ディフォルトのメディアタイプは、G.711 u-law(0)を使用します
	 * AudioフォーマットはSoundCommunicationRulesクラスに記載されているデフォルトのルールを用いて生成されます。
	 *
	 * @param socket RTP通信に使用するパケット.
	 * @param destIP 通信相手のIPアドレス
	 * @param destPort 通信相手のポート番号
	 * @param callback_method 送信前に制御を渡すコールバック関数。この関数はデータ送信前にそのAudioStreamを生成する。
	 */
	public VoicePacketSender(DatagramSocket socket,String destIP,String destPort,
			ICallback<Void, AudioInputStream> callback_method){
		this(socket, destIP, destPort, callback_method, SoundRules.SIZE_MAX_DATA_ULAW);
	}

	/* (非 Javadoc)
	 * @see sgr.st.callback.lib.ICallback#callback(java.lang.Object)
	 * 生成したAudioInputStreamをByteArrayStreamに変換して送信機に渡す。
	 */
	@Override
	public ByteArrayOutputStream callback(Void param) {
		voicePacket = new byte[max_size];
		byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ulawStream.read(voicePacket,0,voicePacket.length);
			byteArrayOutputStream.write(voicePacket, 0, voicePacket.length);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return byteArrayOutputStream;
	}

}

