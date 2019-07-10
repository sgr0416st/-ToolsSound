package sgr.st.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import sgr.st.AudioAnalyzer;
import sgr.st.AudioCapture;
import sgr.st.AudioConstants;
import sgr.st.AudioPlayer;
import sgr.st.properties.PropertiesReader;

public class AudioAnalyzerDemo {

	public static final int THREASHOLD = 2000;

	public static void main(String[] args) {
		PropertiesReader reader;
		@SuppressWarnings("unused")
		int audioBufSize_ulaw, audioBufSize_linear;
		@SuppressWarnings("unused")
		AudioFormat ulawFormat, linearFormat;
		byte[] data;

		AudioPlayer player = null;
		AudioCapture capture = null;
		AudioAnalyzer analyzer = null;

		try {
			// プロパティの読み込み
			reader = new PropertiesReader(
					"/Users/satousuguru/workspace/programing/java/propaties/network.properties"
					);

			audioBufSize_linear = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_LINEAR"));
			linearFormat = new AudioFormat(
					AudioConstants.sampleRate,
					AudioConstants.sampleSizeInBits_PCM,
					AudioConstants.channels,
					true,
					AudioConstants.isBigEndian
					);

			// 初期化
			player = new AudioPlayer(audioBufSize_linear);
			capture = new AudioCapture(audioBufSize_linear);
			analyzer = new AudioAnalyzer(linearFormat, 10);
			int diff;
			long captured ,writed ,analyzed, write_time = 0, capture_time = 0, analyze_time = 0;

			// 音声を取得、解析、出力の時間計測
			writed = System.currentTimeMillis();
			for(int c = 0; c < 1000; c++) {
				data = capture.read();
				captured = System.currentTimeMillis();
				capture_time +=  captured - writed;

				if((diff = analyzer.detectDiff(data)) > THREASHOLD) {
					System.out.println("diff detect: " + diff);
				}
				analyzed = System.currentTimeMillis();
				analyze_time += analyzed - captured;

			 	player.write(new ByteArrayInputStream(data));
				writed = System.currentTimeMillis();
				write_time += writed - analyzed;
			}

			System.out.println("analyze_time : " + analyze_time);
			System.out.println("capture_time : " + capture_time);
			System.out.println("write_time : " + write_time);

		} catch (SocketException | UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (LineUnavailableException e2) {
			// TODO 自動生成された catch ブロック
			e2.printStackTrace();
		}finally {
			player.close();
			capture.close();
		}
	}
}
