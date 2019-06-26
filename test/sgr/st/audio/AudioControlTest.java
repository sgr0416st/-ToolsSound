package sgr.st.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioControlTest {

	public static final String soundPath = "./sound/sample/egao1.wav";
	public static final String[] label = {"low", "org", "high"};
	public static final float[] vol = { (float) -40.0 , (float) -10.0,  (float) 5.0 };

	public static void main(String[] args) {
		DataLine.Info data_info;
		SourceDataLine line;
		FloatControl volCtrl = null;
		AudioFormat format = null;

		// 読み込みファイルの準備　
		int bytesPerFrame = 0;
		int numBytes;
		byte[] audioBytes = null;
		AudioInputStream audioInputStream = null;
		File fileIn = new File(soundPath);
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

		data_info = new DataLine.Info(SourceDataLine.class, format); // format is an AudioFormat object


		try {
			// デフォルトミキサーでのコントロールの取得
			Mixer mixer = AudioSystem.getMixer(null);
			line = (SourceDataLine)mixer.getLine(data_info);
			line.open();
			volCtrl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
			System.out.println(volCtrl.toString());
			System.out.println("precison: " + volCtrl.getPrecision());
			System.out.println("units: " + volCtrl.getUnits());

			line.start();
			// 実際にボリュームを変えてみる
			for(int i = 0; i < 3; i++) {
				try {
					audioInputStream = AudioSystem.getAudioInputStream(fileIn);
					int numBytesRead = 0;
					System.out.println("sound test : " + label[i]);
					volCtrl.setValue(vol[i]);
					while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
						line.write(audioBytes, 0, numBytesRead);
					}
					line.drain();
					Thread.sleep(3000);

				} catch (InterruptedException e) {
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
			line.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
