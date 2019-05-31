package sgr.st.sound.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayer{

	protected AudioFormat inFormat, outFormat;
	protected DataLine.Info info;
	protected SourceDataLine sourceDataLine;
	protected byte[] buffer;
	protected int size;
	protected AudioInputStream inStream, outStream;

	/**
	 * 与えられたバッファサイズとoutFormatを元にスピーカにアクセスして、ストリームを取得します。
	 * 取得したストリームはinFormatの形式で受け取ることができる様になります。
	 *
	 * @param max_size ラインへ１度に送る最大のデータ量
	 * @param inFormat 読み取り形式。スピーカから取得したラインはこの形式で渡せる様に変換されます。
	 * nullを渡すと、元のフォーマットを維持したまま読み取りが可能になります。
	 * @param outFormat スピーカデバイスへ渡すデータの形式。基本的には Linearです。
	 * @throws LineUnavailableException
	 */
	public AudioPlayer(int max_size, AudioFormat inFormat, AudioFormat outFormat) throws LineUnavailableException {
		this.outFormat = outFormat;
		if(inFormat == null) {
			this.inFormat = outFormat;
		}else {
			this.inFormat = inFormat;
		}
		this.info = new DataLine.Info(SourceDataLine.class,this.outFormat);
		this.sourceDataLine = (SourceDataLine)AudioSystem.getLine(info);
		sourceDataLine.open(this.outFormat);
		this.sourceDataLine.start();
		//this.size = Math.min(max_size*2, sourceDataLine.getBufferSize()/3);
		this.size = max_size*2;
		this.buffer = new byte[size];
	}

	/**
	 * 与えられたバッファサイズを元にスピーカにアクセスして、ストリームを取得します。
	 * スピーカへ渡すデータの形式をLinearと仮定します。
	 * 取得したストリームはoutFormatの形式で受け取ることができる様になります。
	 *
	 * @param max_size ラインから１度に読み取る最大のデータ量
	 * @param inFormat 読み取り形式。スピーカから取得したラインはこの形式で受け取れる様に変換されます。
	 * @throws LineUnavailableException
	 */
	public AudioPlayer(int max_size, AudioFormat inFormat) throws LineUnavailableException {
		this(
				max_size,
				inFormat,
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
	 * 与えられたバッファサイズを元にスピーカにアクセスして、ストリームを取得します。
	 * スピーカへ渡すデータの形式をLinearと仮定します。
	 * 取得したストリームは元の形式を維持したまま渡せます。
	 *
	 * @param max_size ラインから１度に読み取る最大のデータ量
	 * @throws LineUnavailableException
	 */
	public AudioPlayer(int max_size) throws LineUnavailableException {
		this(
				max_size,
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



	public void restart() {
		this.sourceDataLine.start();
	}

	public void halt() {
		this.sourceDataLine.stop();
	}

	public void close() {
		this.sourceDataLine.close();
	}

	/**
	 * 取得したストリームへ、あらかじめ指定された量以下のデータを書き込みます。
	 * 与えられたバイトストリームを適切なデータ形式に変換して、それをスピーカに渡します。
	 *
	 * @return　読み取ったデータから新たに生成したバイトストリーム
	 * @throws LineUnavailableException
	 */
	public void write(ByteArrayInputStream stream) {
		inStream = new AudioInputStream(stream, inFormat, size);
		if(this.outFormat != this.inFormat) {
			this.outStream = AudioSystem.getAudioInputStream(this.outFormat, inStream);
		}else {
			outStream = inStream;
		}
		try {
			outStream.read(buffer,0,buffer.length);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		this.sourceDataLine.write(buffer,0,buffer.length);
	}

}
