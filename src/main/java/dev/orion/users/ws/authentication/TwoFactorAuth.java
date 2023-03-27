package dev.orion.users.ws.authentication;

import javax.ws.rs.Produces;
import javax.inject.Inject;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dev.orion.users.model.User;
import dev.orion.users.usecase.UseCase;
import dev.orion.users.ws.BaseWS;
import dev.orion.users.ws.exceptions.UserWSException;
import dev.orion.users.ws.utils.GoogleUtils;
import io.smallrye.mutiny.Uni;
import lombok.val;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.jboss.resteasy.reactive.RestForm;

@Path("api/users")
public class TwoFactorAuth extends BaseWS {

    @Inject
    protected GoogleUtils googleUtils;

    @Inject
    protected UseCase useCase;

    @POST
    @Path("google/2FAuth/auth/qrCode")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("image/png")
    public Uni<byte[]> googleAuth2FAQrCode(
            @RestForm @NotEmpty @Email final String email,
            @RestForm @NotEmpty final String password) {

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
                    val secret = user.getSecret2FA();
                    val userEmail = user.getEmail();
                    val barCodeData = googleUtils.getGoogleAutheticatorBarCode(secret, userEmail, "Orion Test");
                    return googleUtils.createQrCode(barCodeData);
                })
                .onItem()
                .ifNull()
                .failWith(new UserWSException("User not found",
                        Response.Status.UNAUTHORIZED));
    }

    // @POST
    // @Path("google/2FAuth/createAuth/qrCode")
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces("image/png")
    // public Response googleCreateAuth2FAQrCode() {
    // return null;
    // }

    @POST
    @Path("google/2FAuth/validate")
    @Retry(maxRetries = 1, delay = 2000)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> google2FAValidate(
            @RestForm @NotEmpty @Email final String email,
            @RestForm @NotEmpty final String code) {

        return useCase.findUserByEmail(email)
                .onItem()
                .ifNotNull()
                .transform(user -> {
                    if (!user.isUsing2FA()) {
                        return null;
                    }
                    val secret = user.getSecret2FA();
                    val userCode = googleUtils.getTOTPCode(secret);
                    if (!userCode.toString().equals(code)) {
                        System.out.println(code);
                        return null;
                    }
                    System.out.println(user.toString());
                    return generateJWT(user);
                })
                .onItem()
                .ifNull()
                .failWith(new UserWSException("User not found",
                        Response.Status.UNAUTHORIZED));
    }
}
