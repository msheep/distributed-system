package activitystreamer.util;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Coder {
	//generate RSA initial key pair
	public static Map<String, Object> genKeyPair() throws Exception{
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(Constants.PUBLIC_KEY, publicKey);
        keyMap.put(Constants.PRIVATE_KEY, privateKey);
		return keyMap;
	}
	
	public static SecretKey genSharedKey() throws Exception{
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		SecureRandom random = new SecureRandom();
		keygen.init(random);
		SecretKey sharedKey = keygen.generateKey();
		return sharedKey;
	}
	
	// parse string to public key
	public static PublicKey stringToPublicKey(String key) throws Exception {
		byte[] keyBytes = decryptBASE64(key);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicK = keyFactory.generatePublic(x509KeySpec);
		return publicK;
	}
	
	// parse string to private key
	public static PrivateKey stringToPrivateKey(String key) throws Exception {
		byte[] keyBytes = decryptBASE64(key);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateK = keyFactory.generatePrivate(x509KeySpec);
		return privateK;
	}
	
	// parse string to shared AES key
	public static SecretKey stringToSharedKey(String key) throws Exception {
		byte[] keyBytes = decryptBASE64(key);
		SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
		return secretKey;
	}
	
	// parse public key to string
	public static String publicKeyToString(PublicKey key) throws Exception {
		return encryptBASE64(key.getEncoded());
	}
	
	// parse private key to string
	public static String privateKeyToString(PrivateKey key) throws Exception {
		return encryptBASE64(key.getEncoded());
	}
	
	// parse shared key to string
	public static String sharedKeyToString(SecretKey key) throws Exception {
		return encryptBASE64(key.getEncoded());
	}
	
	public static byte[] decryptBASE64(String key) throws Exception {
		return (new BASE64Decoder()).decodeBuffer(key);
	}

	public static String encryptBASE64(byte[] key) throws Exception {
		return (new BASE64Encoder()).encodeBuffer(key);
	}
	
	// encrypt string by public key
	public static String encryptByPublicKey(String content, PublicKey key) throws Exception {
		byte[] data = content.getBytes("UTF-8");
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, key);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		
		// the max bytes that can be encrypted once is 117
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > 117) {
				cache = cipher.doFinal(data, offSet, 117);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * 117;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptBASE64(encryptedData).replace("\n", "").trim();
	}
	
	// decrypt string by private key
	public static String decryptByPrivateKey(String content, PrivateKey key)
			throws Exception {
		byte[] encryptedData = decryptBASE64(content);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, key);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		
		// after encrypting, the length of max bytes is 128
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > 128) {
				cache = cipher.doFinal(encryptedData, offSet, 128);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * 128;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return new String(decryptedData, "UTF-8");
	}
	
	// encrypt string by shared key
	public static String encryptBySharedKey(String content, SecretKey key) throws Exception {
		byte[] data = content.getBytes("UTF-8");
		byte[] enCodeFormat = key.getEncoded();
		SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, seckey);
		byte[] result = cipher.doFinal(data);
		return parseByte2HexStr(result).replace("\n", "").trim();
	}
	
	
	// decrypt string by shared key
	public static String decryptBySharedKey(String content, SecretKey key) throws Exception {
		byte[] data = parseHexStr2Byte(content);
		byte[] enCodeFormat = key.getEncoded();
		SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, seckey);
		byte[] result = cipher.doFinal(data);
		return new String(result, "UTF-8");
	}
	
	// To convert binary to hexadecimal
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
	
	// To convert hexadecimal to binary
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

}
