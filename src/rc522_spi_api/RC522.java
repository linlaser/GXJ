/**
 * 
 */
/**
 * @author LASER
 *
 */
package rc522_spi_api;

public class RC522 {

	private static final String TAG = "RC522_SPI";
	public static final byte MODE = 0;
	public static final byte BITS = 8;
	public static final int SPEED = 10000;
	public static final String DEVICE = "/dev/spidev0.0";

	public static final byte PICC_REQALL = 0x52;
	public static final byte PICC_AUTHENT1A = 0x60;

	public byte[] readData;

	// ��SPI������SPI��ID��
	public static native int open(String device, byte mode, byte bits, int speed);

	// �ر�SPI
	public static native int close(int fd);

	// Ѱ��
	public static native int request(byte reg_code, byte[] card_type);

	// �ų�ͻ�����ؿ�ID
	public static native int anticoll(byte[] card_id);

	// ѡ��
	public static native int select(byte[] card_id);

	// ��֤
	public static native int auth_state(byte auth_mode, byte addr, byte[] key, byte[] card_id);

	// ����
	public static native int read(byte addr, byte[] readData);

	// д��
	public static native int write(byte addr, byte[] writeData);

	static {
		System.loadLibrary("rc522_spi_jni");
	}

}