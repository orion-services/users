/**
 * @License
 * Copyright 2024 Orion Services @ https://orion-services.dev
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
package dev.orion.users.frameworks.mail;

import io.quarkus.mailer.MailTemplate.MailTemplateInstance;
import io.quarkus.qute.CheckedTemplate;

/**
 * Class to load mail templates.
 */
@CheckedTemplate
public final class MailTemplate {

    /**
     * Sends the message to the user with a new password.
     *
     * @param password : The new password of the user
     * @return A MailTemplateInstance object
     */
    public static native MailTemplateInstance recoverPwd(String password);

    /**
     * Sends a message to the user validates the e-mail.
     *
     * @param url : The URL to the user validate the e-mail
     * @return A MailTemplateInstance object
     */
    public static native MailTemplateInstance validateEmail(String url);

}

