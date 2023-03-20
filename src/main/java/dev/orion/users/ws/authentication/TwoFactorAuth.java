package dev.orion.users.ws.authentication;

import javax.ws.rs.Produces;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import de.taimos.totp.TOTP;
import io.smallrye.mutiny.Uni;
import lombok.val;

@Path("api/users")
public class TwoFactorAuth  {
    
    public static String generateSecretKey(){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    public static String getTOTPCode(String secretKey){
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    public static String getGoogleAutheticatorBarCode(String secretKey, String account, String issuer){
        try {
            return "otpauth://totp/"
            + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
            + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
            + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }


    public BufferedImage createQrCode(String barCodeData){
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, 400, 400);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            throw new IllegalStateException(e);
        }
    }

    @GET
    @Path("google/2FAuth/qrCode")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces("image/png")
    public Response google2FAQrCode() throws IOException{
        val secretKey = generateSecretKey();
        val barCodeData = getGoogleAutheticatorBarCode(secretKey,"giovanisouza15@gmail.com", "Orion Test");
        BufferedImage image = createQrCode(barCodeData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageData = baos.toByteArray();
        return Response.ok(imageData).build();
    }

    @GET
    @Path("google/2FAuth/validate")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> google2FAValidate(){
        return null;
    }
}
