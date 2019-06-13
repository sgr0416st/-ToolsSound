package sgr.st;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;


//オーディオストリームからwav形式のデータを生成するクラス
public class WaveDataGenerater {
	public static final int HEADER_SIZE_ALL = 44;	// waveヘッダ全体のサイズ
	public static final int HEADER_SIZE_RIFF = 8;	// RIFF ヘッダのサイズ
	public static final int CHUNCK_SIZE_PCM = 16;	// PCMチャンクのサイズ
	public static final int FORMAT_ID_PCM = 1;		// PCMのフォーマットのID

	AudioInputStream audioInputStream;
	AudioFormat audioFormat;
	String riff_ChunkID = "RIFF";
	int riff_ChunkSize;
	String riff_FormType = "WAVE";
	String fmt_ChunkID = "fmt ";
	int fmt_ChunkSize;
	short fmt_WaveFormatType;
	short fmt_Channel;
	int fmt_SamplesPerSec;
	int fmt_BytesPerSec;
	short fmt_BlockSize;
	short fmt_BitsPerSamples;
	String data_ChunkID = "data";
	int data_ChunkSize;
	int numberOfBytesInSamples;
	byte[] buffer;
	byte[] result;

	private boolean setStream = false;

	//オーディオフォーマットは毎回固定のため、最初に一度だけ初期化
	public WaveDataGenerater(AudioFormat audioFormat){
		setStream = false;
		this.audioFormat = audioFormat;
		this.numberOfBytesInSamples = 0;
		this.buffer = null;
		this.riff_ChunkSize = 0;
		this.fmt_ChunkSize = CHUNCK_SIZE_PCM;
		this.fmt_WaveFormatType = FORMAT_ID_PCM;
		this.fmt_Channel = (short)this.audioFormat.getChannels();
		this.fmt_SamplesPerSec = (int)this.audioFormat.getSampleRate();
		this.fmt_BytesPerSec = this.fmt_SamplesPerSec * this.fmt_Channel * this.audioFormat.getSampleSizeInBits() / 8;
		this.fmt_BlockSize = (short)(this.audioFormat.getChannels() * this.audioFormat.getSampleSizeInBits() / 8);
		this.fmt_BitsPerSamples = (short)this.audioFormat.getSampleSizeInBits();
		this.data_ChunkSize = 0;
	}

	//非ストリーミングデータを扱う場合、通常オーディオストリームは1度だけ作成するため、最初に初期化
	public WaveDataGenerater(AudioInputStream audioInputStream)  throws IOException {
		this(audioInputStream.getFormat());
		setAudioStream(audioInputStream);
	}

	//ストリーミングデータを扱う場合、オーディオストリームは都度作成するため、その度にセットし直す
	public void setAudioStream(AudioInputStream audioInputStream) throws IOException {
		this.audioInputStream = audioInputStream;
		this.numberOfBytesInSamples = (int)(this.audioInputStream.getFrameLength() * (long)this.audioFormat.getFrameSize());
		this.buffer = new byte[this.audioInputStream.available()];
		this.riff_ChunkSize = this.numberOfBytesInSamples + HEADER_SIZE_ALL - HEADER_SIZE_RIFF;
		this.data_ChunkSize = this.numberOfBytesInSamples;
		setStream = true;
	}

	// オーディオストリームからwave形式のデータを生成して返す。
	// stream を毎回生成する必要はないと思われる!!!!!!!!!!!!!!
	public byte[] generateWaveData() throws IOException {
		result = null;
		if(setStream == false) {
			throw new IOException("generateWaveData: not initialize AudioStream.");
		}else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			WaveDataOutputStream wdos = new WaveDataOutputStream(dos);

			this.writeWavChunks_Format(wdos);
			// debug
			result = baos.toByteArray();
			this.writeWavChunks_Data(wdos);
			result = baos.toByteArray();
		}
		return result;
	}

	public int getMax() {
		int max = 0, tmp = 0;
		for(int i = 0; i < result.length; i++) {
			tmp = Math.abs(result[i]);
			if(max < tmp) {
				max = tmp;
			}
		}
		return max;
	}

	// 与えられたストリームにfmtチャンクを書き込む
	private void writeWavChunks_Format(WaveDataOutputStream wdos) throws IOException {
		wdos.writeBytes(this.riff_ChunkID);
		wdos.writeInt(this.riff_ChunkSize);
		wdos.writeBytes(this.riff_FormType);
		wdos.writeBytes(this.fmt_ChunkID);
		wdos.writeInt(this.fmt_ChunkSize);
		wdos.writeShort(this.fmt_WaveFormatType);
		wdos.writeShort(this.fmt_Channel);
		wdos.writeInt(this.fmt_SamplesPerSec);
		wdos.writeInt(this.fmt_BytesPerSec);
		wdos.writeShort(this.fmt_BlockSize);
		wdos.writeShort(this.fmt_BitsPerSamples);

		wdos.flush();
	}

	// 与えられたストリームにdataチャンクを書き込む。データは記憶してあるオーディオストリームから読み込む。
	private void writeWavChunks_Data(WaveDataOutputStream wdos) throws IOException {
		wdos.writeBytes(this.data_ChunkID);
		wdos.writeInt(this.data_ChunkSize);
		int readBytes;
		while((readBytes = this.audioInputStream.read(this.buffer)) != -1) {
			wdos.write(this.buffer, 0, readBytes);
		}
		wdos.flush();
	}
}

class WaveDataOutputStream{
	public static final int FORMAT_SIZE = 16;
	public static final short FORMAT_CORD_PCM = 1;
	private DataOutputStream systemStream;

	public WaveDataOutputStream(DataOutputStream systemStream) {
		this.systemStream = systemStream;
	}

	public void close() throws IOException {
		this.systemStream.close();
	}

	public void writeBytes(String stringToWrite) throws IOException {
		this.systemStream.writeBytes(stringToWrite);
	}


	public void write(byte[] bytesToWrite) throws IOException {
		this.systemStream.write(bytesToWrite, 0, bytesToWrite.length);
	}

	public void write(byte[] bytesToWrite, int readDataSize) throws IOException {
		this.systemStream.write(bytesToWrite, 0, readDataSize);
	}

	public void write(byte[] bytesToWrite, int off, int readDataSize) throws IOException {
		this.systemStream.write(bytesToWrite, off, readDataSize);
	}

	public void writeInt(int intToWrite) throws IOException {
		byte[] intToWriteAsBytesLittleEndian
		= new byte[]{(byte)(intToWrite & 255), (byte)(intToWrite >> 8 & 255),
				(byte)(intToWrite >> 16 & 255), (byte)(intToWrite >> 24 & 255)};
		this.systemStream.write(intToWriteAsBytesLittleEndian, 0, 4);
	}

	public void writeShort(short shortToWrite) throws IOException {
		byte[] shortToWriteAsBytesLittleEndian
		= new byte[]{(byte)shortToWrite, (byte)(shortToWrite >>> 8 & 255)};
		this.systemStream.write(shortToWriteAsBytesLittleEndian, 0, 2);
	}

	public void flush() throws IOException {
		this.systemStream.flush();
	}
}


