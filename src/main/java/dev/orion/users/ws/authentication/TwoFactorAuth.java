package dev.orion.users.ws.authentication;

import javax.ws.rs.Produces;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.validator.routines.DomainValidator.Item;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.oracle.svm.core.annotate.Inject;

import de.taimos.totp.TOTP;
import dev.orion.users.model.User;
import dev.orion.users.usecase.UseCase;
import dev.orion.users.usecase.UserUC;
import dev.orion.users.ws.BaseWS;
import dev.orion.users.ws.exceptions.UserWSException;
import io.quarkus.oidc.IdToken;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import lombok.val;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;

@Path("api/users")
public class TwoFactorAuth extends BaseWS{

    @Inject
    SecurityIdentity securityIdentity;

    private UseCase uc = new UserUC();

    private static final Logger LOG = Logger.getLogger(TwoFactorAuth.class);

    public String getTOTPCode(String secretKey){
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    public String getGoogleAutheticatorBarCode(String secretKey, String account, String issuer){
        try {
            return "otpauth://totp/"
            + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
            + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
            + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }


    public byte[] createQrCode(String barCodeData){
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, 400, 400);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageData = baos.toByteArray();
            return imageData;
        } catch (WriterException e) {
            throw new IllegalStateException(e);
        }catch(IOException e){
            throw new IllegalStateException(e);
        }
    }
    
    
    @POST
    @Path("google/2FAuth/auth/qrCode")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("image/png")
    public Uni<byte[]> googleAuth2FAQrCode(
        @RestForm @NotEmpty @Email final String email,
        @RestForm @NotEmpty final String password
    ){
        
       return uc.authenticate(email, password)
        .onItem()
        .ifNotNull()
        .transform(user -> {
            val secret = user.getSecret2FA();
            val userEmail = user.getEmail();
            val barCodeData = getGoogleAutheticatorBarCode(secret,userEmail, "Orion Test");
            return createQrCode(barCodeData);
        })
        .onItem()
        .ifNull()
        .failWith(new UserWSException("User not found",
                        Response.Status.UNAUTHORIZED));
    }

    @POST
    @Path("google/2FAuth/createAuth/qrCode")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("image/png")
    public Response googleCreateAuth2FAQrCode(){
        return null;
    }


    @POST
    @Path("google/2FAuth/validate")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    // @Produces(MediaType.TEXT_PLAIN)
    public Uni<Boolean> google2FAValidate( 
        @RestForm @NotEmpty @Email final String email,
        @RestForm @NotEmpty final String code,
        @RestForm @NotEmpty final String password
        ){

        return uc.authenticate(email, password)
        .onItem()
        .ifNotNull()
        .transform(user -> {
            val secret = user.getSecret2FA();
            val userCode = getTOTPCode(secret);
            
            return userCode.toString().equals(code);
        })
        .onItem()
        .ifNull()
        .failWith(new UserWSException("User not found",
                        Response.Status.UNAUTHORIZED));
    }
}
