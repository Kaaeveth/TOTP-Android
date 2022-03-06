package de.bofloos.totpandroid;

import de.bofloos.totpandroid.model.OTPHashAlgorithms;
import de.bofloos.totpandroid.model.OneTimePassword;
import org.apache.commons.codec.binary.Base32;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class OneTimePasswordUnitTest {

    String key = "HXDMVJECJJWSRB3HWIZR4IFUGFTMXBOZ";
    OTPHashAlgorithms alg = OTPHashAlgorithms.SHA1;
    short period = 30;

    @Test
    public void correctTOTP() throws NoSuchAlgorithmException, InvalidKeyException {
        String res = OneTimePassword.generateTOTP(new Base32().decode(key), period, alg);
        System.out.println(res);
    }
}
