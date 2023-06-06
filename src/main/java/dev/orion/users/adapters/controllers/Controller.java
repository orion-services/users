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

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.modelmapper.ModelMapper;

import dev.orion.users.adapters.gateways.entities.UserEntity;
import dev.orion.users.frameworks.mail.MailTemplate;
import io.smallrye.mutiny.Uni;

/**
 *  The controller class.
 */
public class Controller {

    /** The model mapper. */
    protected ModelMapper mapper = new ModelMapper();

    /** Set the validation url. */
    @ConfigProperty(name = "users.email.validation.url",
        defaultValue = "http://localhost:8080/api/users/validateEmail")
    String validateURL;

    /**
     * Send a message to the user validates the e-mail.
     *
     * @param user : A user object
     * @return Return a Uni<UserEntity> after to send an e-mail.
     */
    protected Uni<UserEntity> sendValidationEmail(final UserEntity user) {
        // Build the url
        StringBuilder url = new StringBuilder();
        url.append(validateURL);
        url.append("?code=" + user.getEmailValidationCode());
        url.append("&email=" + user.getEmail());

        // Sends the e-mail
        return MailTemplate.validateEmail(url.toString())
                .to(user.getEmail())
                .subject("E-mail confirmation")
                .send()
                .onItem().ifNotNull().transform(item -> user);
    }

}
