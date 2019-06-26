package sgr.st.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import sgr.st.AudioCapture;
import sgr.st.AudioConstants;
import sgr.st.AudioPlayer;
import sgr.st.properties.PropertiesReader;

public class CaptureAndPlayerDemo {


	public static void main(String[] args) {
		PropertiesReader reader;
		@SuppressWarnings("unused")
		int audioBufSize_ulaw, audioBufSize_linear;
		@SuppressWarnings("unused")
		AudioFormat ulawFormat, linearFormat;
		byte[] data;

		AudioPlayer player = null;
		AudioCapture capture = null;

		try {
			// プロパティの読み込み
			reader = new PropertiesReader(
					"/Users/satousuguru/workspace/programing/java/propaties/network.properties"
					);

			audioBufSize_ulaw = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_ULAW"));
			audioBufSize_linear = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_LINEAR"));
			ulawFormat = new AudioFormat(
					AudioFormat.Encoding.ULAW,
					AudioConstants.sampleRate,
					AudioConstants.sampleSizeInBits_ulaw,
					AudioConstants.channels,
					AudioConstants.frameSize,
					AudioConstants.frameRate,
					AudioConstants.isBigEndian
					);
			linearFormat = new AudioFormat(
					AudioConstants.sampleRate,
					AudioConstants.sampleSizeInBits_PCM,
					AudioConstants.channels,
					true,
					AudioConstants.isBigEndian
					);


			player = new AudioPlayer(audioBufSize_linear);
			//player = new AudioPlayer(audioBufSize_ulaw, audioBufSize_linear, ulawFormat, linearFormat);
			/*
			Info dataInfo = new DataLine.Info(SourceDataLine.class,linearFormat);
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataInfo);
			sourceDataLine.open(linearFormat);
			sourceDataLine.start();
			*/


			capture = new AudioCapture(audioBufSize_linear);
			/*
			Info info = new DataLine.Info(TargetDataLine.class, linearFormat);
			TargetDataLine targetDataLine = (TargetDataLine)AudioSystem.getLine(info);
			targetDataLine.open(linearFormat);
			AudioInputStream inStream = new AudioInputStream(targetDataLine);
			targetDataLine.start();
			*/

			long captured ,writed , write_time = 0, capture_time = 0;
			for(int c = 0; c < 8000; c++) {
				writed = System.currentTimeMillis();
				data = capture.read();
				captured = System.currentTimeMillis();
				capture_time +=  captured - writed;
				System.out.println("capture_time : " + capture_time);
			 	player.write(new ByteArrayInputStream(data));
				writed = System.currentTimeMillis();
				write_time += writed - captured;
			}

			System.out.println("capture_time : " + capture_time);
			System.out.println("write_time : " + write_time);

		} catch (SocketException | UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}finally {
			player.close();
			capture.close();
		}


	}


}
