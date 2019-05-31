package sgr.st.sound.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

import sgr.st.sound.old.RealtimeReceiveSoundPlayer;
import sgr.st.sound.old.SoundRules;

public class RealtimeVoiceReceiveTest {

	public static void main(String[] args) {
		RealtimeReceiveSoundPlayer receiver = null;

		try {
			System.out.println("受信IP: " + InetAddress.getLocalHost().getHostAddress());
			System.out.println("受信ポート: " + SoundRules.PORT_RTP_SOUND_RCEIVE);
			receiver = new RealtimeReceiveSoundPlayer(SoundRules.SIZE_MAX_DATA_ULAW);
			receiver.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while(true) {
				System.out.println("受信を開始しました\n終了するには\"stop\"と入力します");
				if(reader.readLine().equals("stop") == true)
					break;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		receiver.close();

		System.out.println("受信を終了しました");
		System.exit(0);
	}

}
