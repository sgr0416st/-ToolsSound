package sgr.st.sound.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

import sgr.st.sound.old.RealtimeRecordSoundSender;
import sgr.st.sound.old.SoundRules;

public class RealtimeVoiceSendTest {
	// public static final String IP = NetworkData.SOTA_IP;
	// public static final String IP = NetworkData.MY_MAC_IP;
	// 自分自身に送る

	public static void main(String[] args) {
		RealtimeRecordSoundSender sender = null;
		try {
			String IP = InetAddress.getLocalHost().getHostAddress();
			System.out.println("送信IP: " + IP);
			System.out.println("送信先ポート: " + SoundRules.PORT_RTP_SOUND_RCEIVE);

			sender = new RealtimeRecordSoundSender(IP, SoundRules.SIZE_MAX_DATA_ULAW);
			sender.start();
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
