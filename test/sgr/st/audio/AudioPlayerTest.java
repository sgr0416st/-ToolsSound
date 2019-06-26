package sgr.st.audio;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import sgr.st.AudioPlayer;

public class AudioPlayerTest {


	public static void main(String[] args) {
		AudioPlayer player;
		AudioFormat format;

		// 読み込みファイルの準備　
		int numBytes = 0;
		byte[] audioBytes = null;
		AudioInputStream audioInputStream = null;
		File fileIn = new File("./sound/sample/egao1.wav");
		try {
			audioInputStream = AudioSystem.getAudioInputStream(fileIn);
			format = audioInputStream.getFormat();
			// Set an arbitrary buffer size of 1024 frames.
			numBytes = 1024 * format.getFrameSize();
			audioBytes = new byte[numBytes];
			//player = new AudioPlayer(numBytes, numBytes, format, format);
			player = new AudioPlayer(numBytes, format);

			System.out.println("sound test : ");
			while ((audioInputStream.read(audioBytes)) != -1) {
				player.write(new ByteArrayInputStream(audioBytes));
			}
			player.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
