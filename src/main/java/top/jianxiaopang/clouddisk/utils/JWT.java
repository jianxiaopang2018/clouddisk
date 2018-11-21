package top.jianxiaopang.clouddisk.utils;

import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 生成token和解密token
 */
public class JWT {

	private static final String SECRET = "XX#$%()(#*!()!KL<><MQLMNQNQJQK sdfkjsdrow32234545fdf>?N<:{LWPW";

	//加密，传入一个对象和有效期
	public static <T> String sign(T object, long maxAge) {
		try {
			final JWTSigner signer = new JWTSigner(SECRET);
			final Map<String, Object> claims = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.writeValueAsString(object);
			claims.put("payload", jsonString);
			claims.put("exp", System.currentTimeMillis() + maxAge);
			return signer.sign(claims);
		} catch (Exception e) {
			return null;
		}
	}

	//解密，传入一个加密后的token字符串和解密后的类型
	public static <T> T unsign(String jwt, Class<T> classT) {
		final JWTVerifier verifier = new JWTVerifier(SECRET);
		try {
			final Map<String, Object> claims = verifier.verify(jwt);
			if (claims.containsKey("exp") && claims.containsKey("payload")) {
				long exp = (Long) claims.get("exp");
				long currentTimeMillis = System.currentTimeMillis();
				if (exp > currentTimeMillis) {
					String json = (String) claims.get("payload");
					ObjectMapper objectMapper = new ObjectMapper();
					return objectMapper.readValue(json, classT);
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
