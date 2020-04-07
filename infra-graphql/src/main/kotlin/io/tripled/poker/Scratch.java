package io.tripled.poker;

import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.openjsse.sun.security.rsa.RSAPublicKeyImpl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

class Scratch {
	public static void main(String[] args) throws InvalidKeyException {
		String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJYZUh4Q0xKUEN1MGl3RmZNQTZOMi1MdExLZ3ZKVmUxb3NyQkxqeWpKWkdrIn0.eyJqdGkiOiJmYWI1ZTExZS00NWRlLTQ4ZmEtODZmMS1kYzVhOTkxMzkyZDEiLCJleHAiOjE1ODYyOTAyMTUsIm5iZiI6MCwiaWF0IjoxNTg2Mjg5OTE1LCJpc3MiOiJodHRwczovL2tleWNsb2FrLm90ZC5yb3RhdGUtaXQuYmUvYXV0aC9yZWFsbXMvcG9rZXIiLCJhdWQiOiJsb2NhbHRlc3QiLCJzdWIiOiI1NTg4YTczNS0yODM1LTRkNmUtODgzMy00ZjIyMTdmNzU1YjIiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJsb2NhbHRlc3QiLCJhdXRoX3RpbWUiOjE1ODYyODk4MjEsInNlc3Npb25fc3RhdGUiOiIyZTVjZmJlMi1lNWZiLTQ0M2UtYjhkMy1hNTc4ZGFkNGEzMzYiLCJhY3IiOiIwIiwic2NvcGUiOiJvcGVuaWQifQ.Elxl72BENeGtYxfG_ErLAUSCRgnPIAXLNewf1eKszH16WcYu75bcdWasG7G4gt7XQvGkKbarnuXs7i42TQXaadXEdrxnoFe55TqRifhzeV24W8u5WvbFhrGhBfu5Wb9rKkQ0LoBo-luiyUTZm7E-hKVSEs-wCqKfzrdhM8d6WgGM6UDLaObI45CB2u_CSWaLLoh33PiOHa8D4g64-lnWtg69jxqXlEcNl41Tvo0Iq5jLqpdLpEWzzmzld4z4kdb9wHJ_sLAESqm77bYe8qsoXvfz6oPg1QG3xLbKYS3MfOoQ3L26RhkUWmRjtAs15VTMjFf-QKVhNKpsXJOs-SGyRA";
		try {
			DecodedJWT jwt =  JWT
					.decode(token);
			System.out.println(jwt.toString());
		} catch (JWTVerificationException exception){
			//Invalid signature/claims
		}

	}
}