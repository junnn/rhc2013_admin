package org.redhatchallenge.rhc2013.server;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.redhatchallenge.rhc2013.shared.ConfirmationTokens;
import org.redhatchallenge.rhc2013.shared.Student;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: Jun
 * Date: 4/9/13
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class SendPasswordResetEmail implements Runnable{
    private final ServletContext servletContext;
    private final String email;
    private final String password;

    SendPasswordResetEmail(String email,String password, ServletContext servletContext) {
        this.email = email;
        this.password = password;
        this.servletContext = servletContext;
    }

    @Override
    public void run() {

        ConfirmationTokens token = new ConfirmationTokens();

        String html = null;


        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        currentSession.beginTransaction();

        Criteria criteria = currentSession.createCriteria(Student.class);
        criteria.add(Restrictions.eq("email", email));
        Student student = (Student) criteria.uniqueResult();

        try {
            if (student.getLanguage().equalsIgnoreCase("English")) {
                String path = servletContext.getRealPath("emails/reset_en.html");
                html = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
                html = html.replaceAll("REPLACEME", password);
            } else if (student.getLanguage().equalsIgnoreCase("Chinese (Simplified)")) {
                String path = servletContext.getRealPath("emails/reset_ch.html");
                html = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
                html = html.replaceAll("REPLACEME", password);
            } else if (student.getLanguage().equals("Chinese (Traditional)")) {
                String path = servletContext.getRealPath("emails/reset_zh.html");
                html = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
                html = html.replaceAll("REPLACEME", password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        EmailUtil.sendEmail("Password Reset", html, "Your client does not support HTML messages, your password is " +password,
                email);

        currentSession.close();
    }
}