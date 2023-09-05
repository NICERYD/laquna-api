package kr.co.seedit.global.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import kr.co.seedit.global.error.exception.CustomException;

public class Aes256 {

    public static String alg = "AES/CBC/PKCS5Padding";
//    private final String key = "01234567890123456789012345678901";
//    private final String iv = key.substring(0, 16); // 16byte

    public String encrypt(String uid, byte[] iv, String text) {
    	try {
    		String key = String.format("%-16s", uid);
//    		byte[] iv = "0123456789abcdef".getBytes();
	        Cipher cipher = Cipher.getInstance(alg);
	        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
	        IvParameterSpec ivParamSpec = new IvParameterSpec(iv);
	        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
	
	        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
	        return Base64.getEncoder().encodeToString(encrypted);
		} catch (NoSuchAlgorithmException e) {
			//log.error("encrypt fail. getInstance NoSuchPaddingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		} catch (NoSuchPaddingException e) {
			//log.error("encrypt fail. getInstance NoSuchPaddingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		} catch (InvalidKeyException e) {
			//log.error("encrypt fail. init InvalidKeyException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		} catch (InvalidAlgorithmParameterException e) {
			//log.error("encrypt fail. init InvalidAlgorithmParameterException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		} catch (IllegalBlockSizeException e) {
			//log.error("encrypt fail. doFinal IllegalBlockSizeException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		} catch (BadPaddingException e) {
			//log.error("encrypt fail. doFinal BadPaddingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		} catch (UnsupportedEncodingException e) {
			//log.error("encrypt fail. String UnsupportedEncodingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		}
    }

    public String decrypt(String uid, byte[] iv, String cipherText) {
    	try {
    		String key = String.format("%-16s", uid);
//    		byte[] iv = "0123456789abcdef".getBytes();
	        Cipher cipher = Cipher.getInstance(alg);
	        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
	        IvParameterSpec ivParamSpec = new IvParameterSpec(iv);
	        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
	
	        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
	        byte[] decrypted = cipher.doFinal(decodedBytes);
	        return new String(decrypted, "UTF-8");
		} catch (NoSuchAlgorithmException e) {
			//log.error("encrypt fail. getInstance NoSuchPaddingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		} catch (NoSuchPaddingException e) {
			//log.error("encrypt fail. getInstance NoSuchPaddingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		} catch (InvalidKeyException e) {
			//log.error("encrypt fail. init InvalidKeyException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		} catch (InvalidAlgorithmParameterException e) {
			//log.error("encrypt fail. init InvalidAlgorithmParameterException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		} catch (IllegalBlockSizeException e) {
			//log.error("encrypt fail. doFinal IllegalBlockSizeException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		} catch (BadPaddingException e) {
			//log.error("encrypt fail. doFinal BadPaddingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		} catch (UnsupportedEncodingException e) {
			//log.error("encrypt fail. String UnsupportedEncodingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("encrypt fail");
		}
    }

}