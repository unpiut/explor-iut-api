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
import java.util.Arrays;
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

    private static final Log LOG = LogFactory.getLog(MailSendingRequestTokenServiceImpl.class);

    private final static String ENC_ALGO = "AES/CBC/PKCS5Padding";
    private final static int SALT_SIZE = 10;
    private final static int IV_SIZE = 16;

    private final MailSendingProperties mailSendingProperties;

    private final SecureRandom secureRnd;

    public MailSendingRequestTokenServiceImpl(MailSendingProperties mailSendingProperties) {
        this.mailSendingProperties = mailSendingProperties;
        this.secureRnd = new SecureRandom();
    }

    @Override
    public String createValidationToken(String mailSendingRequestId) throws ValidationException {
        // merge id and salt
        try {
            // Generate salt
            final byte[] salt = this.generateSalt();
            // Create secret key. Use salt as salt
            final SecretKey key = getKeyFromPassword(this.mailSendingProperties.getTokenSecret(), salt);
            // create IV
            final byte[] iv = this.generateIv();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            // encrypt id with key and iv
            final byte[] cipherData = encrypt(mailSendingRequestId, key, ivParameterSpec);
            // Concatenate cipherData with salt and iv
            byte[] tokenData = new byte[cipherData.length + SALT_SIZE + IV_SIZE];
            int i = 0;
            for (int k = 0; k < cipherData.length; k++) {
                tokenData[i++] = cipherData[k];
            }
            for (int k = 0; k < SALT_SIZE; k++) {
                tokenData[i++] = salt[k];
            }
            for (int k = 0; k < IV_SIZE; k++) {
                tokenData[i++] = iv[k];
            }
            // encode the full token in base64
            return encodeBase64(tokenData);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
                | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException
                | InvalidKeyException ex) {
            LOG.error("Cannot create validation token", ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String decodeToken(String token) throws ValidationException, IllegalArgumentException {
        // decode token from base64 (may throw IllegalArgumentException)
        final byte[] tokenData = decodeBase64(token);
        // check token length is at minimum IV size + Salt size + 1
        if (tokenData.length <= SALT_SIZE + IV_SIZE) {
            throw new IllegalArgumentException("Invalid token composition.");
        }
        // prepare ciphe, salt and iv
        final byte[] cipherData = Arrays.copyOfRange(tokenData, 0, tokenData.length - SALT_SIZE - IV_SIZE);
        final byte[] salt = Arrays.copyOfRange(tokenData, cipherData.length, cipherData.length + SALT_SIZE);
        final byte[] iv = Arrays.copyOfRange(tokenData, cipherData.length + SALT_SIZE, tokenData.length);
        try {
            // Prepare key and ivParameterSpec
            final SecretKey key = getKeyFromPassword(this.mailSendingProperties.getTokenSecret(), salt);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            // Decrypt cipherData
            final String mailSendingRequestId = decrypt(cipherData, key, ivParameterSpec);
            return mailSendingRequestId;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            LOG.error("Cannot create validation token, bad algorithm.", ex);
            throw new IllegalStateException(ex);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException
                | InvalidKeyException | NoSuchPaddingException ex) {
            throw new IllegalArgumentException("Invalid token.");
        }
    }

    protected byte[] generateSalt() {
        byte[] bytes = new byte[SALT_SIZE];
        secureRnd.nextBytes(bytes);
        return bytes;
    }

    protected byte[] generateIv() {
        byte[] iv = new byte[IV_SIZE];
        secureRnd.nextBytes(iv);
        return iv;
    }

    protected static SecretKey getKeyFromPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

    protected static byte[] encrypt(String input, SecretKey key,
            IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ENC_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return cipherText;
    }

    protected static String decrypt(byte[] cipherData, SecretKey key,
            IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ENC_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(cipherData);
        return new String(plainText);
    }

    protected static String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    protected static byte[] decodeBase64(String str) {
        return Base64.getDecoder().decode(str);
    }
}
