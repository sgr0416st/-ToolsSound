package sgr.st.sound.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import sgr.st.sound.lib.RealtimeRecordSoundSender;
import sgr.st.sound.lib.SoundRules;

public class RealtimeVoiceSendTest {
    // public static final String IP = NetworkData.SOTA_IP;
    // public static final String IP = NetworkData.MY_MAC_IP;
    public static final String IP = "133.34.174.234";

	public static void main(String[] args) {

		System.out.println("送信IP: " + IP);
		System.out.println("送信先ポート: " + SoundRules.PORT_RTP_SOUND_RCEIVE);

		RealtimeRecordSoundSender sender = new RealtimeRecordSoundSender(IP, 3200);
		sender.start();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while(true) {
				System.out.println("送信を開始しました\n終了するには\"stop\"と入力します");
				if(reader.readLine().equals("stop") == true)
					break;
			}
			// 受信再生停止
		} catch(Exception e) {
			e.printStackTrace();
		}
		sender.close();

		System.out.println("送信を終了しました");
		System.exit(0);
	}
}
