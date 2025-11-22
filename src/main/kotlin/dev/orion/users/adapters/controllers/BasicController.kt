/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
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
package dev.orion.users.adapters.controllers

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.security.SecureRandom

import javax.imageio.ImageIO

import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Hex
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.Claims
import org.modelmapper.ModelMapper

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix

import de.taimos.totp.TOTP
import dev.orion.users.adapters.gateways.entities.UserEntity
import dev.orion.users.frameworks.mail.MailTemplate
import dev.orion.users.frameworks.rest.ServiceException
import io.smallrye.jwt.build.Jwt
import io.smallrye.mutiny.Uni
import jakarta.ws.rs.core.Response

/**
 * The controller class.
 */
open class BasicController {

    /** The encoding used in the QR code. */
    private val UTF_8 = "UTF-8"

    /** Configure the issuer for JWT generation. */
    @ConfigProperty(name = "users.issuer", defaultValue = "orion-users")
    lateinit var issuer: String

    /** Set the validation url. */
    @ConfigProperty(name = "users.email.validation.url", defaultValue = "http://localhost:8080/users/validateEmail")
    lateinit var validateURL: String

    /** ModelMapper. */
    protected val mapper: ModelMapper = ModelMapper()

    /**
     * Creates a JWT (JSON Web Token) to a user.
     *
     * @param user : The user object
     * @return Returns the JWT
     */
    fun generateJWT(user: UserEntity): String {
        return Jwt.issuer(issuer)
            .upn(user.email)
            .groups(user.getRoleList().toSet())
            .claim(Claims.c_hash, user.hash)
            .claim(Claims.email, user.email)
            .sign()
    }

    /**
     * Verifies if the e-mail from the jwt is the same from request.
     *
     * @param email    : Request e-mail
     * @param jwtEmail : JWT e-mail
     * @return true if the e-mails are the same
     * @throws ServiceException Throw an exception (HTTP 400) if the e-mails are
     *                          different, indicating that possibly the JWT is
     *                          outdated.
     */
    fun checkTokenEmail(email: String, jwtEmail: String): Boolean {
        if (email != jwtEmail) {
            throw ServiceException("JWT outdated", Response.Status.BAD_REQUEST)
        }
        return true
    }

    /**
     * Send a message to the user validates the e-mail.
     *
     * @param user : A user object
     * @return Return a Uni<UserEntity> after to send an e-mail.
     */
    fun sendValidationEmail(user: UserEntity): Uni<UserEntity> {
        val url = StringBuilder()
        url.append(validateURL)
        url.append("?code=" + user.emailValidationCode)
        url.append("&email=" + user.email)

        return MailTemplate.validateEmail(url.toString())
            .to(user.email ?: "")
            .subject("E-mail confirmation")
            .send()
            .onItem().ifNotNull()
            .transform { user }
    }

    /**
     * Create Time-based one-time password.
     *
     * @param secretKey : The secret key
     * @return The Time-based one-time password code in String format
     * @throws IllegalArgumentException
     */
    fun getTOTPCode(secretKey: String): String {
        try {
            val base32 = Base32()
            val bytes = base32.decode(secretKey)
            val hexKey = Hex.encodeHexString(bytes)
            return TOTP.getOTP(hexKey)
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }

    /**
     * Create Google Bar Code.
     *
     * @param secretKey : The secret key
     * @param account   : The account name
     * @param issuer    : The issuer name
     * @return The Google Bar Code in String format
     * @throws IllegalArgumentException
     */
    fun getAuthenticatorBarCode(secretKey: String, account: String, issuer: String): String {
        try {
            return "otpauth://totp/" +
                URLEncoder.encode("$issuer:$account", UTF_8)
                    .replace("+", "%20") +
                "?secret=" + URLEncoder.encode(secretKey, UTF_8)
                    .replace("+", "%20") +
                "&issuer=" + URLEncoder.encode(issuer, UTF_8)
                    .replace("+", "%20")
        } catch (e: UnsupportedEncodingException) {
            throw IllegalStateException(e)
        } catch (e: NullPointerException) {
            throw IllegalStateException(e)
        }
    }

    /**
     * Create QrCode.
     *
     * @param barCodeData : The Google Bar Code
     * @return The QrCode with Google Bar Code in a array of byte format
     * @throws IllegalArgumentException
     */
    fun createQrCode(barCodeData: String): ByteArray {
        try {
            val matrix = MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, 400, 400)
            val image: BufferedImage = MatrixToImageWriter.toBufferedImage(matrix)
            val baos = ByteArrayOutputStream()
            ImageIO.write(image, "png", baos)
            return baos.toByteArray()
        } catch (e: WriterException) {
            throw IllegalStateException(e)
        } catch (e: IOException) {
            throw IllegalStateException(e)
        } catch (e: NullPointerException) {
            throw IllegalStateException(e)
        }
    }

    /**
     * Generate Secret Key.
     *
     * @return The Secret Key in String format
     * @throws IllegalArgumentException
     */
    fun generateSecretKey(): String {
        val random = SecureRandom()
        val bytes = ByteArray(20)
        random.nextBytes(bytes)
        val base32 = Base32()
        return base32.encodeToString(bytes)
    }
}

