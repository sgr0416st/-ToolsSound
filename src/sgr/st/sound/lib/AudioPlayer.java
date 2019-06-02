package sgr.st.sound.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

//size
public class AudioPlayer{

	protected AudioFormat inFormat, outFormat;
	protected DataLine.Info dataInfo;
	protected SourceDataLine sourceDataLine;
	protected byte[] buffer;
	protected int in_size, out_size;
	protected AudioInputStream inStream, outStream;

	/**
	 * 与えられたバッファサイズとoutFormatを元にスピーカにアクセスして、ストリームを取得します。
	 * 取得したストリームはinFormatの形式で受け取ることができる様になります。
	 *
	 * @param in_size １度の書き込み時に受け取るデータ量
	 * @param out_size ラインへ１度に送るデータ量
	 * @param inFormat 読み取り形式。スピーカから取得したラインはこの形式で渡せる様に変換されます。
	 * nullを渡すと、元のフォーマットを維持したまま読み取りが可能になります。
	 * @param outFormat スピーカデバイスへ渡すデータの形式。基本的には Linearです。
	 * @throws LineUnavailableException
	 */
	public AudioPlayer(int in_size, int out_size, AudioFormat inFormat, AudioFormat outFormat) throws LineUnavailableException {
		init(in_size, out_size, inFormat, outFormat);
		prepareLine();
	}

	/**
	 * 与えられたバッファサイズを元にスピーカにアクセスして、ストリームを取得します。
	 * スピーカへ渡すデータの形式はそのまま渡せる形式とします。
	 * 取得したストリームはformatの形式で受け取ることができる様になります。
	 *
	 * @param size １度の書き込み時に受け取るデータ量
	 * @param format 読み取り形式。スピーカから取得したラインはこの形式で受け取れる様に変換されます。
	 * @throws LineUnavailableException
	 */
	public AudioPlayer(int size, AudioFormat format) throws LineUnavailableException {
		this(
				size,
				size,
				format,
				format
			);
	}

	/**
	 * 与えられたバッファサイズを元にスピーカにアクセスして、ストリームを取得します。
	 * スピーカへ渡すデータの形式をLinearと仮定します。
	 * 取得したストリームは元の形式を維持したまま渡せます。
	 *
	 * @param size １度に読み込み、ラインへ送るデータ量
	 * @throws LineUnavailableException
	 */
	public AudioPlayer(int size) throws LineUnavailableException {
		this(
				size,
				size,
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
	 * シンプルに初期化するメソッド.
	 *
	 * @param in_size
	 * @param out_size
	 * @param inFormat
	 * @param outFormat
	 */
	protected void init(int in_size, int out_size, AudioFormat inFormat, AudioFormat outFormat) {
		this.outFormat = outFormat;
		if(inFormat == null) {
			this.inFormat = outFormat;
		}else {
			this.inFormat = inFormat;
		}
		this.dataInfo = new DataLine.Info(SourceDataLine.class,this.outFormat);
		//this.size = Math.min(max_size*2, sourceDataLine.getBufferSize()/3);
		this.in_size = in_size;
		this.out_size = out_size;
		this.buffer = new byte[out_size];

	}

	/**
	 * 指定した規約を満たすオーディオのソースラインを１つ取得するメソッド。
	 *
	 * @throws LineUnavailableException
	 */
	protected void prepareLine() throws LineUnavailableException {
		this.sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataInfo);
		sourceDataLine.open(this.outFormat);
		this.sourceDataLine.start();
	}



	public void restart() {
		this.sourceDataLine.start();
	}

	public void halt() {
		this.sourceDataLine.drain();
		this.sourceDataLine.stop();
	}

	public void close() {
		this.sourceDataLine.drain();
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
		inStream = new AudioInputStream(stream, inFormat, in_size);
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
