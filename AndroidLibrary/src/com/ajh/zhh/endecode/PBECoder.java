package com.ajh.zhh.endecode;
import java.security.Key;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * PBE安全编码组件
 * 
 * @author benjamin 2013/7/10
 * @version 1.0
 * @since 1.0
 */
public abstract class PBECoder extends Coder {
	/**
	 * 支持以下任意一种算法
	 * 
	 * <pre>
	 * PBEWithMD5AndDES  
	 * PBEWithMD5AndTripleDES  
	 * PBEWithSHA1AndDESede 
	 * PBEWithSHA1AndRC2_40
	 * </pre>
	 */
	public final static String PBEWithMD5AndDES = "PBEWithMD5AndDES";
	public final static String PBEWithMD5AndTripleDES = "PBEWithMD5AndTripleDES";
	public final static String PBEWithSHA1AndDESede = "PBEWithSHA1AndDESede";
	public final static String PBEWithSHA1AndRC2_40 = "PBEWithSHA1AndRC2_40";

	/**
	 * 盐初始化
	 * 
	 * @return
	 * @throws Exception
	 */
	public static byte[] initSalt() throws Exception {
		byte[] salt = new byte[8];
		Random random = new Random();
		random.nextBytes(salt);
		return salt;
	}

	/**
	 * 转换密钥<br>
	 * 
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static Key toKey(String password,String ALGORITHM) throws Exception {
		PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey secretKey = keyFactory.generateSecret(keySpec);

		return secretKey;
	}

	/**
	 * 加密
	 * 
	 * @param data
	 *            数据
	 * @param password
	 *            密码
	 * @param salt
	 *            盐
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, String password, byte[] salt,String ALGORITHM)
			throws Exception {

		Key key = toKey(password,ALGORITHM);

		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

		return cipher.doFinal(data);

	}

	/**
	 * 解密
	 * 
	 * @param data
	 *            数据
	 * @param password
	 *            密码
	 * @param salt
	 *            盐
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, String password, byte[] salt,String ALGORITHM)
			throws Exception {

		Key key = toKey(password,ALGORITHM);

		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

		return cipher.doFinal(data);

	}
}