package sgr.st.sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioCapture {

	protected AudioFormat inFormat, outFormat;
	protected DataLine.Info info;
	protected TargetDataLine targetDataLine;
	protected AudioInputStream inStream, outStream;
	protected ByteArrayOutputStream byteArrayOutputStream;
	protected boolean isOpen;
	protected int bffsize;

	/**
	 * 与えられたバッファサイズとフォーマットを元にマイクにアクセスして、ストリームを取得します。
	 * 取得したストリームはoutFormatの形式で読み取ることができる様になります。
	 *
	 * @param bffsize ラインから１度に読み取る最大のデータ量
	 * @param outFormat 読み取り形式。マイクから取得したラインはこの形式で読み取れる様に変換されます。
	 * nullを渡すと、元のフォーマットを維持したまま読み取りが可能になります。
	 * @param inFormat マイクデバイスから得られるデータの形式。基本的には Linearです。
	 * @throws LineUnavailableException
	 */
	public AudioCapture(int bffsize, AudioFormat outFormat, AudioFormat inFormat) throws LineUnavailableException {
		this.bffsize = bffsize;
		this.inFormat = inFormat;
		if(outFormat == null) {
			this.outFormat = inFormat;
		}else {
			this.outFormat = outFormat;
		}
		this.info = new DataLine.Info(TargetDataLine.class, this.inFormat);
		this.targetDataLine = (TargetDataLine)AudioSystem.getLine(info);
		this.targetDataLine.open(this.inFormat);
		this.isOpen = targetDataLine.isOpen();

		this.targetDataLine.start();
		this.inStream = new AudioInputStream(targetDataLine);
		if(this.outFormat != null) {
			this.outStream = AudioSystem.getAudioInputStream(this.outFormat, inStream);
		}else {
			outStream = inStream;
		}

	}

	/**
	 * 与えられたバッファサイズを元にマイクにアクセスして、ストリームを取得します。
	 * マイクからの形式をLinearと仮定します。
	 * 取得したストリームはoutFormatの形式で読み取ることができる様になります。
	 *
	 * @param bffsize ラインから１度に読み取る最大のデータ量
	 * @param outFormat 読み取り形式。マイクから取得したラインはこの形式で読み取れる様に変換されます。
	 * @throws LineUnavailableException
	 */
	public AudioCapture(int bffsize, AudioFormat outFormat) throws LineUnavailableException {
		this(
				bffsize,
				outFormat,
				new AudioFormat(
						AudioRules.sampleRate,
						AudioRules.sampleSizeInBits_PCM,
						AudioRules.channels,
						true,
						AudioRules.isBigEndian
						)
				);
	}

	/**
	 * 与えられたバッファサイズを元にマイクにアクセスして、ストリームを取得します。
	 * マイクからの形式をLinearと仮定します。
	 * 取得したストリームは元の形式を維持したまま読み取れます。
	 *
	 * @param max_size ラインから１度に読み取る最大のデータ量
	 * @throws LineUnavailableException
	 */
	public AudioCapture(int bffsize) throws LineUnavailableException {
		this(
				bffsize,
				null,
				new AudioFormat(
						AudioRules.sampleRate,
						AudioRules.sampleSizeInBits_PCM,
						AudioRules.channels,
						true,
						AudioRules.isBigEndian
						)
				);
	}

	/**
	 * ターゲットラインをstartさせます。
	 */
	public void restart() {
		targetDataLine.start();
	}

	/**
	 * ターゲットラインをstopさせます。
	 */
	public void halt() {
		targetDataLine.stop();
	}

	/**
	 * ストリームを閉じます。
	 * @throws LineUnavailableException すでにストリームが閉じている場合。
	 */
	public void close() throws LineUnavailableException {
		checkOpen();
		try {
			outStream.close();
			isOpen = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取得したストリームから、あらかじめ指定された量以下のデータを読み取ります。
	 * 読み取ったデータから新たにバイトストリームを生成し、それを返します。
	 *
	 * @return　読み取ったデータから新たに生成したバイトストリーム
	 * @throws LineUnavailableException
	 */
	public byte[] read() throws LineUnavailableException {
		checkOpen();
		byte[] buffer = new byte[bffsize];
		try {
			outStream.read(buffer, 0, buffer.length);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return buffer;
	}

	private void checkOpen() throws LineUnavailableException {
		if(!isOpen) {
			throw new LineUnavailableException();
		}
	}



}
