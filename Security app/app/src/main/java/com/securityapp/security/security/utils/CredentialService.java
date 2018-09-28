package com.securityapp.security.security.utils;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

/**
 * Created by Tyler on 5/22/2017.
 */

public class CredentialService {

    private final SharedPreferencesManager mSharedPrefManager;
    private static final String KEYSTORE_NAME = "AndroidKeyStore";
    private KeyStore mKeyStore;
    private final Context mContext;


    public CredentialService(Context context, SharedPreferencesManager sharedPreferencesManager) {
        this.mContext = context;
        mSharedPrefManager = sharedPreferencesManager;
    }

    /**
     * Creates new pair of public - private keys
     * @param publicKey user's username
     */
    public void createNewKeys(String publicKey) {
        try {
            mKeyStore = KeyStore.getInstance(KEYSTORE_NAME);
            mKeyStore.load(null);

            // Create new key if needed
            if (!mKeyStore.containsAlias(publicKey)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);

                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(mContext)
                        .setAlias(publicKey)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", KEYSTORE_NAME);
                generator.initialize(spec);

                generator.generateKeyPair();
                storePublicKey(publicKey);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Encrypt clear text with private key
     *
     * @param clearText string to encrypt
     * @param alias     KeyStore public key for this pair
     * @return cipher text string
     */
    private String encryptString(String clearText, String alias) {
        try {
            mKeyStore = KeyStore.getInstance(KEYSTORE_NAME);
            mKeyStore.load(null);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, inCipher);
            cipherOutputStream.write(clearText.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte[] vals = outputStream.toByteArray();
            return Base64.encodeToString(vals, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Decrypts unreadable cipher text using the private key
     *
     * @param cipherText string to decrypt
     * @param alias      KeyStore public key for this pair
     * @return clear text String
     */
    private String decryptString(String cipherText, String alias) {
        try {
            mKeyStore = KeyStore.getInstance(KEYSTORE_NAME);
            mKeyStore.load(null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(alias, null);

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i);
            }

            return new String(bytes, 0, bytes.length, "UTF-8");

        } catch (Exception e) {
            return null;
        }
    }

    public void storeUserCredentials(String user, String pass) {
        String encryptedPass = encryptString(pass, user);
        mSharedPrefManager.put(SharedPreferencesManager.Key.USERNAME_STR, user);
        mSharedPrefManager.put(SharedPreferencesManager.Key.PASSWORD_STR, encryptedPass);
    }

    private void storePublicKey(String publicKey) {
        mSharedPrefManager.put(SharedPreferencesManager.Key.USERKEY_STR, publicKey);
    }

    public void removeUserCredentials() {
        mSharedPrefManager.remove(SharedPreferencesManager.Key.USERKEY_STR,
                SharedPreferencesManager.Key.USERNAME_STR,
                SharedPreferencesManager.Key.PASSWORD_STR);
    }

    private static void removeKeyStorePairWithPublicKey(String key) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_NAME);
            keyStore.load(null);
            keyStore.deleteEntry(key);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public void removeAllKeyStorePairs() {
        try {
            mKeyStore = KeyStore.getInstance(KEYSTORE_NAME);
            mKeyStore.load(null);
            final Enumeration<String> entries = mKeyStore.aliases();

            while (entries.hasMoreElements()) {
                final String alias = entries.nextElement();
                mKeyStore.deleteEntry(alias);
            }
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return mSharedPrefManager.getString(SharedPreferencesManager.Key.USERNAME_STR);
    }

    public String getUserPassword() {
        String password = mSharedPrefManager.getString(SharedPreferencesManager.Key.PASSWORD_STR);
        return decryptString(password, getUsername());
    }
}

