package sgr.st.sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


/**
 * デバイスのAudioミキサーが使用可能か調べるクラス。
 * まず最初に使用可能なオーディオデバイスのミキサーを取得し、
 * シンプルな音声データを実際に各ミキサーを用いて出力するテストを実行します。
 *
 * @author satousuguru
 *
 */
public class AudioTest {
	public static void main(String[] args) {
		AudioFormat format =  new AudioFormat(
				8000,
				16,
				1,
				true,
				false
				);

		Mixer.Info[] mixers_info;
		DataLine.Info data_info;
		List<Mixer> enableMixers = new ArrayList<Mixer>();

		// 読み込みファイルの準備　
		int bytesPerFrame = 0;
		int numBytes;
		byte[] audioBytes = null;
		AudioInputStream audioInputStream = null;
		File fileIn = new File("./sound/egao1.wav");
		try {
			System.out.println(fileIn.getCanonicalPath());
			audioInputStream = AudioSystem.getAudioInputStream(fileIn);
			format = audioInputStream.getFormat();
			bytesPerFrame = format.getFrameSize();
			// Set an arbitrary buffer size of 1024 frames.
			numBytes = 1024 * bytesPerFrame;
			audioBytes = new byte[numBytes];

		} catch (Exception e) {
			e.printStackTrace();
		}

		// ミキサーの確認
		mixers_info = AudioSystem.getMixerInfo();
		System.out.println("mixers_info:");
		for(Mixer.Info mi : mixers_info) {
			System.out.println(mi.toString());
		}
		System.out.println();


		// データラインの確認
		data_info = new DataLine.Info(SourceDataLine.class, format); // format is an AudioFormat object
		if (!AudioSystem.isLineSupported(data_info)) {
			System.err.println("error: Line not supported");
		}else {
			System.out.println("dataine_info:");
			System.out.println(data_info.toString());
			System.out.println();
		}

		//システムがとってくるラインの取得
		try {
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(data_info);
			System.out.println("systemline_info:");
			System.out.println(line.toString());
			System.out.println();
			//line.open(format);
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		}

		// 各ミキサーからソースラインを取得できるか確認
		int count = 0;
		for(Mixer.Info mi : mixers_info) {
			Mixer mixer = AudioSystem.getMixer(mi);
			Line.Info[] line_infos = mixer.getSourceLineInfo();
			System.out.println("mixer_info " + count + " : ");
			System.out.println(mixer.getMixerInfo().toString());
			if(mixer.isLineSupported(data_info)) {
				// 各ミキサーが指定したソースに対応しているか確認
				System.out.println("supported Specified format data line !! ");
				enableMixers.add(mixer);
				System.out.println("name : " + mi.getName());
				System.out.println("vender : " + mi.getVendor());
				System.out.println("description : " + mi.getDescription());
				System.out.println("version : " + mi.getVersion());

			}else {
				// 指定したソースに対応していなかった場合、対応しているラインを列挙
				System.out.println("not supported");
				for(Line.Info li:line_infos) {
					System.out.println(li.toString());
				}
			}
			System.out.println();
			count++;
		}


		// 取得した使用可能ミキサー全てで試して見る
		for(Mixer em : enableMixers) {
			try {
				Thread.sleep(5000);
				SourceDataLine s_line = (SourceDataLine)em.getLine(data_info);
				s_line.open(format);
				s_line.start();
				fileIn = new File("./sound/egao1.wav");
				audioInputStream = AudioSystem.getAudioInputStream(fileIn);
				try {
					int numBytesRead = 0;
					System.out.println(em.getMixerInfo().toString());
					System.out.println("sound test : ");
					while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
						s_line.write(audioBytes, 0, numBytesRead);
					}
					s_line.drain();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				s_line.close();
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}
}
