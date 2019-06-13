package sgr.st;

public class AudioConstants {
	public static final float sampleRate = 8000;
	public static final int sampleSizeInBits_ulaw = 8;
	public static final int sampleSizeInBits_PCM = 16;
	public static final int channels = 1;
	public static final int frameSize = 1;
	public static final  float frameRate = 8000;
	public static final boolean isBigEndian = false;

	// 0.15秒遅れ
	public static final int SIZE_MAX_DATA_ULAW = 120;

	public static final int PORT_RTP_SOUND_SEND = 9000;
	public static final int PORT_RTP_SOUND_RCEIVE = 9001;

}
