/*
 * Copyright (C) 2024 IUT Laval - Le Mans Université.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package explorateurIUT.services.mailManagement;

import jakarta.validation.ValidationException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author Remi Venant
 */
@Service
@Validated
public class MailSendingRequestTokenServiceImpl implements MailSendingRequestTokenService {

    private final static String ENC_ALGO = "AES/CBC/PKCS5Padding";

    private static final Log LOG = LogFactory.getLog(MailSendingRequestTokenServiceImpl.class);

    private final MailSendingProperties mailSendingProperties;

    private final SecureRandom secureRnd;

    private SecretKey secretKey;

    public MailSendingRequestTokenServiceImpl(MailSendingProperties mailSendingProperties) {
        this.mailSendingProperties = mailSendingProperties;
        this.secureRnd = new SecureRandom();
    }

    @Override
    public String generateSalt() {
        byte[] bytes = new byte[10];
        secureRnd.nextBytes(bytes);
        return encodeBase64(bytes);
    }

    @Override
    public String createValidationToken(String mailSendingRequestId, String salt) throws ValidationException {
        // merge id and salt
        try {
            // Create secret key. Use salt as salt
            final SecretKey key = getKeyFromPassword(this.mailSendingProperties.getTokenSecret(), salt);
            // create IV
            IvParameterSpec ivParameterSpec = generateIv();
            // encrypt id with key and iv
            final String cipherText = encrypt(mailSendingRequestId, key, ivParameterSpec);
            // Concatenate citherText with salt and iv
            final String token = cipherText + "." + salt + "." + encodeBase64(ivParameterSpec.getIV());
            // encode the full token in base64
            return encodeBase64(token.getBytes());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
                | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException
                | InvalidKeyException ex) {
            LOG.error("Cannot create validation token", ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String decodeToken(String token) throws ValidationException, IllegalArgumentException {
        // Attempt to split token in cipher, salt and iv
        final String unbasedToken = new String(decodeBase64(token));
        final String[] components = unbasedToken.split("\\.");
        if (components.length != 3) {
            throw new IllegalArgumentException("Invalid token composition.");
        }
        try {
            // Prepare cipher ,salt, iv
            final String cipher = components[0];
            final String salt = components[1];
            final SecretKey key = getKeyFromPassword(this.mailSendingProperties.getTokenSecret(), salt);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(decodeBase64(components[2]));
            // attempt to decryp
            final String mailSendingRequestId = decrypt(cipher, key, ivParameterSpec);
            return mailSendingRequestId;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            LOG.error("Cannot create validation token, bad algorithm.", ex);
            throw new IllegalStateException(ex);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException
                | InvalidKeyException | NoSuchPaddingException ex) {
            throw new IllegalArgumentException("Invalid token.");
        }
    }

    protected static String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    protected static byte[] decodeBase64(String str) {
        return Base64.getDecoder().decode(str);
    }

    protected static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }

    protected static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    protected static String encrypt(String input, SecretKey key,
            IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(ENC_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return encodeBase64(cipherText);
    }

    protected static String decrypt(String cipherText, SecretKey key,
            IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(ENC_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(decodeBase64(cipherText));
        return new String(plainText);
    }
}
