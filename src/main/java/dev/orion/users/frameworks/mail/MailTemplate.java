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
