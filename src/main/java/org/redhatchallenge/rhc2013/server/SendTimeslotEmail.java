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


public class SendTimeslotEmail implements Runnable {
    private final ServletContext servletContext;
    private final String email;
    private final String timeslot;

    SendTimeslotEmail(String email,String timeslot, ServletContext servletContext) {
        this.email = email;
        this.timeslot = timeslot;
        this.servletContext = servletContext;
    }

    @Override
    public void run() {

        String html = null;

        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        currentSession.beginTransaction();

        Criteria criteria = currentSession.createCriteria(Student.class);
        criteria.add(Restrictions.eq("email", email));
        Student student = (Student) criteria.uniqueResult();

        try {
            if (student.getLanguage().equalsIgnoreCase("English")) {
                String path = servletContext.getRealPath("emails/timeslot_en.html");
                html = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
                html = html.replaceAll("REPLACEME", timeslot);
            } else if (student.getLanguage().equalsIgnoreCase("Chinese (Simplified)")) {
                String path = servletContext.getRealPath("emails/timeslot_ch.html");
                html = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
                html = html.replaceAll("REPLACEME", timeslot);
            } else if (student.getLanguage().equals("Chinese (Traditional)")) {
                String path = servletContext.getRealPath("emails/timeslot_zh.html");
                html = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
                html = html.replaceAll("REPLACEME", timeslot);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        EmailUtil.sendEmail("Timeslot Assignment", html, "Your client does not support HTML messages, your password is " +timeslot,
                email);

        currentSession.close();
    }
}
