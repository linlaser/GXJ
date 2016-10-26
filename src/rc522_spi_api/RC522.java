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

	// 打开SPI，返回SPI的ID号
	public static native int open(String device, byte mode, byte bits, int speed);

	// 关闭SPI
	public static native int close(int fd);

	// 寻卡
	public static native int request(byte reg_code, byte[] card_type);

	// 放冲突，返回卡ID
	public static native int anticoll(byte[] card_id);

	// 选卡
	public static native int select(byte[] card_id);

	// 认证
	public static native int auth_state(byte auth_mode, byte addr, byte[] key, byte[] card_id);

	// 读卡
	public static native int read(byte addr, byte[] readData);

	// 写卡
	public static native int write(byte addr, byte[] writeData);

	static {
		System.loadLibrary("rc522_spi_jni");
	}

}