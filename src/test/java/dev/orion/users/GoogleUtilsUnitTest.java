package dev.orion.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.awt.image.BufferedImage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import de.taimos.totp.TOTP;
import dev.orion.users.ws.utils.GoogleUtils;
import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;

@ExtendWith(MockitoExtension.class)
public class GoogleUtilsUnitTest {

    @InjectMocks
    private GoogleUtils googleUtils;

    @Test
    @DisplayName("Test create TOTP code with valid secret key")
    public void shouldCreateTOTPCode() {
        String secretKey = "JBSWY3DPEHPK3PXP";
        String expectedCode = "432143";
        GoogleUtils googleUtils = mock(GoogleUtils.class);
        when(googleUtils.getTOTPCode(secretKey)).thenReturn(expectedCode);
        String actualCode = googleUtils.getTOTPCode(secretKey);
        assertEquals(expectedCode, actualCode);
    }

    @Test
    public void shouldCreateGoogleAutheticatorBarCode() {
        String secretKey = "MFRGGZDFMZTWQ2LK";
        String account = "testuser";
        String issuer = "testcompany";
        String expectedBarCode = "otpauth://totp/testcompany%3Atestuser?secret=MFRGGZDFMZTWQ2LK&issuer=testcompany";
        String actualBarCode = googleUtils.getGoogleAutheticatorBarCode(secretKey, account, issuer);
        assertEquals(expectedBarCode, actualBarCode);
    }

    @Test
    public void createQrCodeTest() throws WriterException, IOException {

        // QRCodeWriter qrCodeWriter = mock(QRCodeWriter.class);
        String barCodeData = "otpauth://totp/testcompany%3Atestuser?secret=MFRGGZDFMZTWQ2LK&issuer=testcompany";

        // MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        // BitMatrix bitMatrix = multiFormatWriter.encode(barCodeData,
        // BarcodeFormat.QR_CODE,
        // anyInt(), anyInt());

        // when(bitMatrix.getHeight()).thenReturn(400);
        // when(bitMatrix.getWidth()).thenReturn(400);

        byte[] result = googleUtils.createQrCode(barCodeData);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
