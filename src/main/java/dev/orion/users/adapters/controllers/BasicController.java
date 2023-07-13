/**
 * @License
 * Copyright 2023 Orion Services @ https://github.com/orion-services
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
package dev.orion.users.adapters.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.modelmapper.ModelMapper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import de.taimos.totp.TOTP;
import dev.orion.users.adapters.gateways.entities.UserEntity;
import dev.orion.users.frameworks.mail.MailTemplate;
import dev.orion.users.frameworks.rest.ServiceException;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;

/**
 *  The controller class.
 */
public class BasicController {

    /** The encoding used in the QR code. */
    private static final String UTF_8 = "UTF-8";

    /** Configure the issuer for JWT generation. */
    @ConfigProperty(name = "users.issuer")
    Optional<String> issuer;

    /** Set the validation url. */
    @ConfigProperty(name = "users.email.validation.url",
        defaultValue = "http://localhost:8080/users/validateEmail")
    String validateURL;

    /** ModelMapper. */
    ModelMapper mapper = new ModelMapper();

    /**
     * Creates a JWT (JSON Web Token) to a user.
     *
     * @param user : The user object
     *
     * @return Returns the JWT
     */
    public String generateJWT(final UserEntity user) {
        return Jwt.issuer(issuer.orElse("orion-users"))
            .upn(user.getEmail())
            .groups(new HashSet<>(user.getRoleList()))
            .claim(Claims.c_hash, user.getHash())
            .claim(Claims.email, user.getEmail())
            .sign();
    }

    /**
     * Verifies if the e-mail from the jwt is the same from request.
     *
     * @param email    : Request e-mail
     * @param jwtEmail : JWT e-mail
     * @return true if the e-mails are the same
     * @throws ServiceException Throw an exception (HTTP 400) if the e-mails are
     * different, indicating that possibly the JWT is outdated.
     */
    public boolean checkTokenEmail(final String email,
            final String jwtEmail) {
        if (!email.equals(jwtEmail)) {
            throw new ServiceException("JWT outdated",
                Response.Status.BAD_REQUEST);
        }
        return true;
    }

    /**
     * Send a message to the user validates the e-mail.
     *
     * @param user : A user object
     * @return Return a Uni<UserEntity> after to send an e-mail.
     */
    public Uni<UserEntity> sendValidationEmail(final UserEntity user) {
        StringBuilder url = new StringBuilder();
        url.append(validateURL);
        url.append("?code=" + user.getEmailValidationCode());
        url.append("&email=" + user.getEmail());

        return MailTemplate.validateEmail(url.toString())
                .to(user.getEmail())
                .subject("E-mail confirmation")
                .send()
                .onItem().ifNotNull()
                .transform(item -> user);
    }

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
    public String getAuthenticatorBarCode(String secretKey, String account, String issuer) {
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

    /**
     * Generate Secret Key.
     *
     * @return The Secret Key in String format
     * @throws IllegalArgumentException
     */
    public String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

}
