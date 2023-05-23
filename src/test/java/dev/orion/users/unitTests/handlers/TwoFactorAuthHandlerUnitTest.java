package dev.orion.users.unitTests.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.junit.jupiter.MockitoExtension;
import com.google.zxing.WriterException;

import dev.orion.users.data.handlers.TwoFactorAuthHandler;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class TwoFactorAuthHandlerUnitTest {

    @InjectMocks
    private TwoFactorAuthHandler twoFactorHandler;

    @Test
    @Order(1)
    @DisplayName("Test create TOTP code with valid secret key")
    void shouldCreateTOTPCode() {
        String secretKey = "JBSWY3DPEHPK3PXP";
        String expectedCode = "432143";
        TwoFactorAuthHandler twoFactorHandler = mock(TwoFactorAuthHandler.class);
        when(twoFactorHandler.getTOTPCode(secretKey)).thenReturn(expectedCode);
        String actualCode = twoFactorHandler.getTOTPCode(secretKey);
        assertEquals(expectedCode, actualCode);
    }

    @Test()
    @Order(2)
    @DisplayName("Test create TOTP code with null secret key")
    void testGetTOTPCodeWithNullSecretKey() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    twoFactorHandler.getTOTPCode(null);
                });
    }

    @Test
    @Order(3)
    @DisplayName("Test create create the auth barcode")
    void shouldCreateAutheticatorBarCode() {
        String secretKey = "MFRGGZDFMZTWQ2LK";
        String account = "testuser";
        String issuer = "testcompany";
        String expectedBarCode = "otpauth://totp/testcompany%3Atestuser?secret=MFRGGZDFMZTWQ2LK&issuer=testcompany";
        String actualBarCode = twoFactorHandler.getAutheticatorBarCode(
                secretKey, account, issuer);
        assertEquals(expectedBarCode, actualBarCode);
    }

    @Test
    @Order(4)
    @DisplayName("Test create auth barcode with null secret key")
    void testGetAutheticatorBarCodeWithNullSecretKey() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> {
                    twoFactorHandler.getAutheticatorBarCode(null,
                            "account", "issuer");
                });

    }

    @Test
    @Order(5)
    @DisplayName("Test create auth barcode with null issuer")
    void testGetAuthenticatorBarCodeWithNullIssuer() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> {
                    twoFactorHandler.getAutheticatorBarCode("secretKey",
                            "account", null);
                });

    }

    @Test
    @Order(6)
    @DisplayName("Test create create the qrcode")
    void createQrCodeTest() throws WriterException, IOException {
        String barCodeData = "otpauth://totp/testcompany%3Atestuser?secret=MFRGGZDFMZTWQ2LK&issuer=testcompany";
        byte[] result = twoFactorHandler.createQrCode(barCodeData);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @Order(7)
    @DisplayName("Test create create qrcode with invalid barcode data")
    void testCreateQrCodeWithInvalidBarCodeData() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> {
                    twoFactorHandler.createQrCode(null);
                });
    }

    @Test
    @DisplayName("Test generate a secrete Key")
    @Order(14)
    void testGenerateSecretKey() {
        String secretKey = twoFactorHandler.generateSecretKey();

        Assertions.assertNotNull(secretKey);
        Assertions.assertTrue(secretKey.matches("[A-Z2-7]*"));
        Assertions.assertEquals(32, secretKey.length());
    }

}
