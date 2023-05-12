/**
 * @License
 * Copyright 2022 Orion Services @ https://github.com/orion-services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.orion.users.presentation.services.authentication;

import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import dev.orion.users.domain.usecases.UseCase;
import dev.orion.users.presentation.exceptions.UserWSException;
import dev.orion.users.presentation.handlers.TwoFactorAuthHandler;
import dev.orion.users.presentation.services.BaseWS;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.faulttolerance.Retry;

/**
 * Two Factor Authenticate.
 */
@Path("api/users")
public class TwoFactorAuth extends BaseWS {

    /** Google auth utilities */
    @Inject
    protected TwoFactorAuthHandler twoFactorAuthHandler;

    /** Business logic */
    @Inject
    protected UseCase useCase;

    /**
     * Authenticate and returns a qrCode to two factor auth.
     *
     * @return The return is in image/png format
     * @throws UserWSException Returns a HTTP 401 if credentials not found
     */
    @POST
    @Path("twoFactorAuth/qrCode")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("image/png")
    @WithSession
    public Uni<byte[]> generateTwoFactorAuthQrCode(
            @FormParam("email") @NotEmpty @Email final String email,
            @FormParam("password") @NotEmpty final String password) {

        return useCase.authenticate(email, password)
                .onItem().ifNotNull()
                .transformToUni(user -> {
                    user.setUsing2FA(true);
                    return useCase.updateUser(user);
                })
                .onItem().ifNotNull()
                .transform(user -> {
                    String secret = user.getSecret2FA();
                    String userEmail = user.getEmail();
                    String barCodeData = twoFactorAuthHandler.getAutheticatorBarCode(
                            secret, userEmail, "Orion User Service");
                    return twoFactorAuthHandler.createQrCode(barCodeData);
                })
                .onItem().ifNull()
                .failWith(new UserWSException("Credentials not found",
                        Response.Status.UNAUTHORIZED));
    }

    /**
     * Validate two factor auth code
     *
     * @return The return is a string with token
     * @throws UserWSException Returns a HTTP 401 if credentials not found
     */
    @POST
    @Path("twoFactorAuth/validate")
    @Retry(maxRetries = 1, delay = 2000)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> validateTwoFactorAuthCode(
            @FormParam("email") @NotEmpty @Email final String email,
            @FormParam("password") @NotEmpty final String password,
            @FormParam("code") @NotEmpty final String code) {

        return useCase.authenticate(email, password)
                .onItem().ifNotNull()
                .transform(user -> {
                    String secret = user.getSecret2FA();
                    String userCode = twoFactorAuthHandler.getTOTPCode(secret);
                    if (!user.isUsing2FA()) {
                        return null;
                    }
                    if (!userCode.equals(code)) {
                        return null;
                    }
                    return generateJWT(user);
                })
                .onItem().ifNull()
                .failWith(new UserWSException("Credentials not found or 2FAuth not activated",
                        Response.Status.UNAUTHORIZED));
    }
}
