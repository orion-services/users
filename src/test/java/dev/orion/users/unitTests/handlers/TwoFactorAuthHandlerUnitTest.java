package dev.orion.users.unitTests.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import com.google.zxing.WriterException;

import dev.orion.users.data.handlers.TwoFactorAuthHandler;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class TwoFactorAuthHandlerUnitTest {

    @InjectMocks
    private TwoFactorAuthHandler twoFactorHandler;

    @BeforeEach
    public void setup() {
        twoFactorHandler = mock(TwoFactorAuthHandler.class);
    }

    @Test
    @Order(1)
    @DisplayName("Test create TOTP code with valid secret key")
    void shouldCreateTOTPCode() {
        String secretKey = "JBSWY3DPEHPK3PXP";
        String expectedCode = "432143";
        Mockito.when(twoFactorHandler.getTOTPCode(secretKey)).thenReturn(expectedCode);

        String actualCode = twoFactorHandler.getTOTPCode(secretKey);

        assertEquals(expectedCode, actualCode);
    }

    @Test()
    @Order(2)
    @DisplayName("Test create TOTP code with null secret key")
    void testGetTOTPCodeWithNullSecretKey() {
        Mockito.when(twoFactorHandler.getTOTPCode(null)).thenCallRealMethod();

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

        Mockito.when(twoFactorHandler.getAutheticatorBarCode(
                secretKey, account, issuer)).thenCallRealMethod();

        String actualBarCode = twoFactorHandler.getAutheticatorBarCode(
                secretKey, account, issuer);
        assertEquals(expectedBarCode, actualBarCode);
    }

    @Test
    @Order(4)
    @DisplayName("Test create auth barcode with null secret key")
    void testGetAutheticatorBarCodeWithNullSecretKey() {
        Mockito.when(twoFactorHandler.getAutheticatorBarCode(null,
                "account", "issuer")).thenCallRealMethod();

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
        Mockito.when(twoFactorHandler.getAutheticatorBarCode("secretKey",
                "account", null)).thenCallRealMethod();

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
        Mockito.when(twoFactorHandler.createQrCode(barCodeData)).thenCallRealMethod();

        byte[] result = twoFactorHandler.createQrCode(barCodeData);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @Order(7)
    @DisplayName("Test create create qrcode with invalid barcode data")
    void testCreateQrCodeWithInvalidBarCodeData() {
        Mockito.when(twoFactorHandler.createQrCode(null)).thenCallRealMethod();
        Assertions.assertThrows(IllegalStateException.class,
                () -> {
                    twoFactorHandler.createQrCode(null);
                });
    }

    @Test
    @DisplayName("Test generate a secrete Key")
    @Order(14)
    void testGenerateSecretKey() {

        Mockito.when(twoFactorHandler.generateSecretKey()).thenCallRealMethod();

        String secretKey = twoFactorHandler.generateSecretKey();

        Assertions.assertNotNull(secretKey);
        Assertions.assertTrue(secretKey.matches("[A-Z2-7]*"));
        Assertions.assertEquals(32, secretKey.length());
    }

}
