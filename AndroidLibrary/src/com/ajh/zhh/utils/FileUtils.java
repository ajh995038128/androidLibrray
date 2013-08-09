package com.ajh.zhh.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ajh.zhh.endecode.Coder;

public class FileUtils {
	/**
	 * @author benjamin 2013年7月18日 这个类是根据图片的地址将其转换成base64码以及将其转化回来的类
	 * @throws IOException
	 */
	/**
	 * 用于文件编码，便于传输
	 * 
	 * @param path
	 *            文件所在的位置
	 * @return 文件内容编码后的字符串
	 */
	public static String encode(String path) throws IOException {
		final File file = new File(path);
		return encode(file);
	}

	/**
	 * 将输入流的内容进行编码
	 * 
	 * @param is要编码的流
	 * @return 根据流内容编码后的字符串
	 */
	public static String encode(InputStream is) throws IOException {
		final ByteArrayOutputStream bao = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[512];
		while ((len = is.read(buffer)) != -1) {
			bao.write(buffer, 0, len);
		}
		is.close();
		byte[] byteData = bao.toByteArray();
		bao.close();
		String encodeedStr = null;
		try {
			encodeedStr = Coder.encryptBASE64(byteData);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return encodeedStr;
	}

	/**
	 * 用于文件编码，便于传输
	 * 
	 * @param file
	 *            要编码的文件
	 */
	public static String encode(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		return encode(fis);
	}

	/**
	 * 文件编码的反向操作,将编码后的文件内容转化成字节数组
	 * 
	 * @param encodeStr编码后形成的字符串
	 * @return 原来文件的字节数据
	 **/
	public static byte[] decodeToByteArray(String encodeStr) {
		try {
			return Coder.decryptBASE64(encodeStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 文件编码的反向操作,将编码后的文件内容转化成字节数组
	 * 
	 * @param encodeStr编码后形成的字符串
	 * @return 原来文件的文件
	 **/
	public static File decodeToFile(String encodeStr, String filename)
			throws IOException {
		final byte[] buffer = decodeToByteArray(encodeStr);
		final File file = new File(filename);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(buffer, 0, buffer.length);
		fos.close();
		return file;
	}

	public static String decodeToString(String encodeStr) {
		return new String(decodeToByteArray(encodeStr));
	}

	public static String getTxtCharest(String path) throws IOException {
		return getTxtCharest(new File(path));
	}

	public static String getTxtCharest(File file) throws IOException {

		return getTxtCharest(new FileInputStream(file));
	}

	public static String getTxtCharest(InputStream inputStream) throws IOException {
		int p = (inputStream.read() << 8) + inputStream.read();
		String code = null;

		switch (p) {
		case 0xefbb:
			code = "UTF-8";
			break;
		case 0xfffe:
			code = "Unicode";
			break;
		case 0xfeff:
			code = "UTF-16BE";
			break;
		default:
			code = "GBK";
		}

		return code;
	}
}
