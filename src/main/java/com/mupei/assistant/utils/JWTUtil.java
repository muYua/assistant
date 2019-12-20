package com.mupei.assistant.utils;

import java.util.Random;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("singleton") // 默认单例模式，prototype多例模式
@Component
public class JWTUtil {
	/**
	 * RsaJsonWebKeyBuilder 单例生成密钥对
	 *
	 * @ 单例模式：在整个Spring IoC容器中，只生成一个实例，每次请求都将获得相同的实例
	 * @ 采用单例模式获取rsaJsonWebKey,调用该方法时总是获取相同的密钥对
	 */
//	private static class RsaJsonWebKeyBuilder {
//		private static volatile RsaJsonWebKey rsaJsonWebKey;
//
//		private RsaJsonWebKeyBuilder() {
//		}
//
//		public static RsaJsonWebKey getRasJsonWebKeyInstance() {
//			if (rsaJsonWebKey == null) {
//				synchronized (RsaJsonWebKey.class) {
//					if (rsaJsonWebKey == null) {
//						try {
//							// 生成一个RSA密钥对，该密钥对将被包装在JWK中用于签名和验证JWT
//							rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
//							// 给JWT一个Key ID
//							rsaJsonWebKey.setKeyId(String.valueOf(new Random().nextLong()));
//						} catch (Exception e) {
//							return null;
//						}
//					}
//				}
//			}
//			return rsaJsonWebKey;
//		}
//	}
//----- 以下是通过利用Spring容器完成对Bean组件作用域（生命周期）的管理-----//
	// 宕机会丢失key，因为不再是原来的class类
	// static变量是属于该类的静态变量，只要使用类是单例模式[@Scope("singleton")]且未被销毁过，static变量值不变
	private static RsaJsonWebKey rsaJsonWebKey = null;

	private RsaJsonWebKey getRasJsonWebKey() {

		if (rsaJsonWebKey == null) {
			try {
				// 生成一个RSA密钥对，该密钥对将被包装在JWK中用于签名和验证JWT
				rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
				// 给JWT一个Key ID
				rsaJsonWebKey.setKeyId(String.valueOf(new Random().nextLong()));

				// 通过Redis存取密钥对，进行多个tomcat的密钥对同步

			} catch (Exception e) {
				return null;
			}

		}
		return rsaJsonWebKey;
	}

	/**
	 * 生成JWT令牌
	 *
	 * @param iss         签发方
	 * @param aud         接收方
	 * @param sub         主题(类似用户的session ID，用户的email)
	 * @param exp_minutes 令牌到期时间，单位：分钟
	 * @return JWT令牌的紧凑序列
	 * @throws Exception
	 */
	public String generateToken(String iss, String aud, String sub, int exp_minutes) throws Exception {
		JwtClaims jwtClaims = new JwtClaims();// 通过JWT传达的声明

		jwtClaims.setIssuer(iss); // 发行方，谁创建令牌并对其进行签名
		jwtClaims.setAudience(aud); // 受众群众
		jwtClaims.setSubject(sub); // 令牌所针对的主题/主体,可以存放什么userid，roleid之类的，作为什么用户的唯一标志
		jwtClaims.setExpirationTimeMinutesInTheFuture(exp_minutes); // 令牌到期时间，minutes为单位（从现在开始）
		jwtClaims.setGeneratedJwtId(); // 令牌唯一标识
		jwtClaims.setIssuedAtToNow(); // 发行/创建令牌的时间（现在）
		jwtClaims.setNotBeforeMinutesInThePast(2); // 令牌尚未生效的时间（2分钟前）,令牌生效所需时间->考虑多服务器环境，服务器之间的时间不一致问题

		// JWE：JSON Web Encryption，Encryption specification（加密）
		// JWS：JSON Web Signature，Digital signature/HMAC specification（签名）
		JsonWebSignature jsonWebSignature = new JsonWebSignature();

		// JWS的有效负载是JWT Claims的JSON内容
		jsonWebSignature.setPayload(jwtClaims.toJson());
		// JWT使用私钥签名
		jsonWebSignature.setKey(getRasJsonWebKey().getPrivateKey());
		// Set the Key ID (kid) header
		// 促进顺利过渡密钥的过程
		jsonWebSignature.setKeyIdHeaderValue(getRasJsonWebKey().getKeyId());
		// 在JWT/JWS上设置签名算法，以完整性保护声明
		jsonWebSignature.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
		// 紧凑的序列化
		// Sign the JWS and produce the compact serialization or the complete JWT/JWS
		// representation, which is a string consisting of three dot ('.') separated
		// base64url-encoded parts in the form Header.Payload.Signature
		// If you wanted to encrypt it, you can simply set this jwt as the payload
		// of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
		String jwt = jsonWebSignature.getCompactSerialization();

		return jwt;
	}

	/**
	 * 解析校验令牌
	 *
	 * @param jwt        JWT令牌序列
	 * @param iss        发布JWT的人
	 * @param aud        JWT的目标用户
	 * @param maxMinutes 最大未来有效时间（分钟）
	 * @return JwtClaims:JWT核心内容
	 */
	public JwtClaims parseToken(String jwt, String iss, String aud, int maxMinutes) {

		// JwtConsumer用于验证和处理JWT
		JwtConsumer jwtConsumer = new JwtConsumerBuilder()
				.setExpectedIssuer(iss) // 发布JWT的人
				.setExpectedAudience(aud) // JWT的目标用户
				.setRequireExpirationTime() // JWT必须具有到期时间
				.setRequireSubject() // JWT必须具有主题声明
				.setMaxFutureValidityInMinutes(maxMinutes) // 最大未来有效时间(分钟)
				.setAllowedClockSkewInSeconds(30) // 允许在验证基于时间的声明时留有余地以解决时钟偏移
				.setVerificationKey(getRasJsonWebKey().getPublicKey()) // 使用公共密钥验证签名
				.build(); // 创建JwtConsumer实例
		try {
			// 验证JWT并将其处理为Claims
			JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
			return jwtClaims;
		} catch (InvalidJwtException e) {
			// 如果JWT无论如何仍无法通过处理或验证，将抛出InvalidJwtException
			return null;
		}
	}
}
