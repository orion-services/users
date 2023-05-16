package dev.orion.users.data.handlers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.awt.image.BufferedImage;

import jakarta.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import de.taimos.totp.TOTP;

/**
 * Google Utilities
 */
@ApplicationScoped
public class TwoFactorAuthHandler {
    private static final String UTF_8 = "UTF-8";

    /**
     * Create Time-based one-time password.
     *
     * @return The Time-based one-time password code in String format
     * @throws IllegalArgumentException
     */
    public String getTOTPCode(String secretKey) {
        try {
            Base32 base32 = new Base32();
            byte[] bytes = base32.decode(secretKey);
            String hexKey = Hex.encodeHexString(bytes);
            return TOTP.getOTP(hexKey);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }

    /**
     * Create Google Bar Code.
     *
     * @return The Google Bar Code in String format
     * @throws IllegalArgumentException
     */
    public String getAutheticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, UTF_8).replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, UTF_8).replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, UTF_8).replace("+", "%20");
        } catch (UnsupportedEncodingException | NullPointerException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Create QrCode.
     *
     * @return The QrCode with Google Bar Code in a array of byte format
     * @throws IllegalArgumentException
     */
    public byte[] createQrCode(String barCodeData) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, 400, 400);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (WriterException | IOException | NullPointerException e) {
            throw new IllegalStateException(e);
        }
    }

    public String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

}
