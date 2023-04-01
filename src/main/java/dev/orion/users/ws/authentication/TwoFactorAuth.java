package dev.orion.users.ws.authentication;

import javax.ws.rs.Produces;
import javax.inject.Inject;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dev.orion.users.usecase.UseCase;
import dev.orion.users.ws.BaseWS;
import dev.orion.users.ws.exceptions.UserWSException;
import dev.orion.users.ws.utils.GoogleUtils;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.faulttolerance.Retry;

@Path("api/users")
public class TwoFactorAuth extends BaseWS {

    @Inject
    protected GoogleUtils googleUtils;

    @Inject
    protected UseCase useCase;

    @POST
    @Path("google/2FAuth/qrCode")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("image/png")
    public Uni<byte[]> googleAuth2FAQrCode(
            @FormParam("email") @NotEmpty @Email final String email,
            @FormParam("password") @NotEmpty final String password) {

        return useCase.authenticate(email, password)
                .onItem()
                .ifNotNull()
                .transformToUni(user -> {
                    user.setUsing2FA(true);
                    return useCase.updateUser(user);
                })
                .onItem()
                .ifNotNull()
                .transform(user -> {
                    String secret = user.getSecret2FA();
                    String userEmail = user.getEmail();
                    String barCodeData = googleUtils.getGoogleAutheticatorBarCode(secret, userEmail,
                            "Orion User Service");
                    return googleUtils.createQrCode(barCodeData);
                })
                .onItem()
                .ifNull()
                .failWith(new UserWSException("User not found",
                        Response.Status.UNAUTHORIZED));
    }

    @POST
    @Path("google/2FAuth/validate")
    @Retry(maxRetries = 1, delay = 2000)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> google2FAValidate(
            @FormParam("email") @NotEmpty @Email final String email,
            @FormParam("code") @NotEmpty final String code) {

        return useCase.findUserByEmail(email)
                .onItem()
                .ifNotNull()
                .transform(user -> {
                    String secret = user.getSecret2FA();
                    String userCode = googleUtils.getTOTPCode(secret);
                    if (!user.isUsing2FA()) {
                        return null;
                    }
                    if (!userCode.equals(code)) {
                        return null;
                    }
                    return generateJWT(user);
                })
                .onItem()
                .ifNull()
                .failWith(new UserWSException("Credentials not found",
                        Response.Status.UNAUTHORIZED));
    }
}
