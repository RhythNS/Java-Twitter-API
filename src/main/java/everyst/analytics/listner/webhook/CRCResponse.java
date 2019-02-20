package everyst.analytics.listner.webhook;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import everyst.analytics.listner.KeyManager;
import everyst.analytics.listner.dataManagement.Logger;

/**
 * 
 * https://gist.github.com/andypiper/1aa7ed86429747bb025a4c3112669d0e
 * 
 * @author andypiper
 */
public class CRCResponse {

	private KeyManager keyManager;

	public CRCResponse(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	public String getChallengeResponse(String crcToken) {
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(keyManager.getConsumerSecret().getBytes("UTF-8"),
					"HmacSHA256");
			sha256_HMAC.init(secret_key);

			String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(crcToken.getBytes("UTF-8")));
			return "sha256=" + hash;
		} catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
			Logger.getInstance().handleError(e);
		}
		return null;
	}

}
