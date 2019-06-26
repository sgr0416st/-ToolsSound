package sgr.st.jtalk;
import jtalk.JTalkJna;

//共有ライブラリ 、辞書、音響モデルが必要
public class JtdJnaJava {
	public static void main(String[] args) {

		try {
			//String name = System.getProperty("user.dir") + "/voice/sample.wav";
			JTalkJna tts = new JTalkJna("", "", "/usr/local/OpenJTalk/voice");
			System.out.println("available HTS Voices:");
			tts.getVoices().forEach(v->System.out.println(v.path));
			//ランダムな声
			//tts.setVoice(tts.getVoices().get((new Random()).nextInt(tts.getVoices().size())));
			tts.setVoice(tts.getVoices().get(2));

			System.out.println("current voice: " + tts.getVoice().name);
			System.out.println("dic: " + tts.getDic());
			System.out.println("s  = " + tts.getS());
			System.out.println("p  = " + tts.getP());
			System.out.println("a  = " + tts.getA());
			System.out.println("b  = " + tts.getB());
			System.out.println("r  = " + tts.getR());
			System.out.println("fm = " + tts.getFm());
			System.out.println("u  = " + tts.getU());
			System.out.println("jm = " + tts.getJm());
			System.out.println("jf = " + tts.getJf());
			System.out.println("g  = " + tts.getG());
			tts.speakAsync("聞こえてますか?");
			//tts.speakToFile("聞こえてますか？", name);
			tts.waitUntilDone();
		} catch (Exception e) {
			System.out.println("エラーが発生しました: " + e.getMessage());
		}
	}
}
