package com.mupei.assistant.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("singleton")
@Component
public class EncryptUtil {

	/**
	 * @description 散列算法加密
	 * @param message 明文
	 * @param hash 散列加密算法类型：SHA-1、SHA-256、MD5等
	 * @return String 密文
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public String encryptWithSHA(String message, String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// 密码盐值，防止使用彩虹表对密码进行爆破
		String salt;
		
		if(message.length() < 5 ) {
			salt = "abcdefghijklmnopqrstuvwxyz";
		}
		salt = message.substring(0, 5);// 从序号0开始，截取5个字符长度
		
		String messageWithSalt = message + salt;

		// 生成实现指定摘要算法的MessageDigest对象
		MessageDigest md = MessageDigest.getInstance(algorithm);
		
		// 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
		byte[] digest = md.digest(messageWithSalt.getBytes("UTF-8"));
		
		// 返回Base64字符串
		return Base64.getEncoder().encodeToString(digest);
		
//		md.update("123".getBytes("UTF-8")); // 使用指定的字节更新摘要
//		byte[] digest = md.digest(); // 通过执行诸如填充之类的最终操作完成哈希计算

		/* 进制转换 */
//		String outputstr=new BigInterger("str",10); // 实例化一个10进制的字符串		
//		BigInteger bigInt = new BigInteger(1, digest); // 指定byte数组转换为正数BigInteger
//		bigInt.toString(16)//进行16进制转换
		
		/* Base64字符转换 */
//		byte[] decode = Base64.getDecoder().decode(str); // base64字符串转byte[]
//	    String encodeToString = Base64.getEncoder().encodeToString(byte[]); // byte[]转base64
	    
	}
	
	//获取AES密钥(单例)--16位十六进制字符串
	private static String keyOfAES = null; // 静态变量赋初值只在函数第一次调用时起作用
	public String getKeyOfAES() {
		final int LENGTH = 16;
		
		//null表示这个字符串不指向任何的东西，如果这时候你调用它的方法，那么就会出现空指针异常。
		//""表示它指向一个长度为0的字符串，这时候调用它的方法是安全的。
		//null不是对象，""是对象，所以null没有分配空间，""分配了空间
		//str.equals("");//str.isEmpty();//str.length() == 0
		if(keyOfAES == null  || "".equals(keyOfAES)) {
			try {
				StringBuffer result = new StringBuffer();
				for(int i=0; i<LENGTH; i++) {
					result.append(Integer.toHexString(new Random().nextInt(16)));
				}
				keyOfAES = result.toString().toUpperCase();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return keyOfAES;
	}
    
	//AES的初始向量，可以通过手动更改VI控制Token的有效性
	private final String VI = "ABCDEF1221FEDCBA";
	//向外提供获取VI的接口
	public String getVI() {
		return this.VI;
	}
	
	/**
	 * @description AES加密
	 * @param message 待加密信息
	 * @param key 密钥
	 * @return String Base64编码字符串形式的密文
	 */
	
    public String encryptWithAES(String message, String key) {
    	final String ALGORITHM = "AES/CBC/PKCS5Padding";
    	
        if (key == null || "".equals(key)) {
            return null;
        }
        if (key.length() != 16) {
            return null;
        }
        try {
        	// 获取密钥
            byte[] raw = key.getBytes();  //获得密码的字节数组
            SecretKeySpec skey = new SecretKeySpec(raw, "AES"); //根据密码生成AES密钥
            
            // 初始化AES密码器
            Cipher cipher = Cipher.getInstance(ALGORITHM);  //根据指定算法ALGORITHM自成密码器
            cipher.init(Cipher.ENCRYPT_MODE, skey, new IvParameterSpec(VI.getBytes())); //初始化密码器，第一个参数为加密(ENCRYPT_MODE)或者解密(DECRYPT_MODE)操作，第二个参数为生成的AES密钥
           
            // 加密
            byte [] byteMessage = message.getBytes("utf-8"); //获取加密内容的字节数组(设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码            
            byte [] encodeMessage = cipher.doFinal(byteMessage); //密码器加密数据
           
            return Base64.getEncoder().encodeToString(encodeMessage); //将加密后的数据转换为Base64编码字符串返回
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }  
    }
    
    /**
     * @description AES解密
     * @param message Base64编码字符串的密文
     * @param key 密钥
     * @return String 明文
     */
    public String decryptWithAES(String message, String key) {
    	final String ALGORITHM = "AES/CBC/PKCS5Padding";
    	
        if (key == null || "".equals(key)) {
            return null;
        }
        if (key.length() != 16) {
            return null;
        }
        try {
        	// 获取密钥
            byte[] raw = key.getBytes();  //获得密码的字节数组
            SecretKeySpec skey = new SecretKeySpec(raw, "AES"); //根据密码生成AES密钥
            
            // 初始化AES密码器
            Cipher cipher = Cipher.getInstance(ALGORITHM);  //根据指定算法ALGORITHM自成密码器
            cipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(VI.getBytes())); //初始化密码器，第一个参数为加密(ENCRYPT_MODE)或者解密(DECRYPT_MODE)操作，第二个参数为生成的AES密钥    
            
            // 解密
            byte [] encodeMessage = Base64.getDecoder().decode(message); //把密文Base64编码字符串转回密文字节数组
            byte [] byteMessage = cipher.doFinal(encodeMessage); //密码器解密数据
            
            return new String(byteMessage,"utf-8"); //将解密后的数据转换为字符串返回
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }  
    }
    
 

}
