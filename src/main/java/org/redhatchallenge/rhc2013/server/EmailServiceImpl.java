package org.redhatchallenge.rhc2013.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.redhatchallenge.rhc2013.client.MassEmailService;
import org.redhatchallenge.rhc2013.shared.Student;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class EmailServiceImpl extends RemoteServiceServlet implements MassEmailService {

    @Override
    public Boolean massEmailSending(List<Student> studentList, String subject, String content) throws IllegalArgumentException{
        String html = null;
            for(Student s : studentList){

                try {
                    if(s.getLanguage().equalsIgnoreCase("English")) {
                        String path = getServletContext().getRealPath("emails/verified_en.html");
                        html = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
                        html = html.replaceAll("REPLACE1", "Hi " + s.getFirstName() + ",");
                        html = html.replaceAll("REPLACE2", content);

                    }

                    else if(s.getLanguage().equalsIgnoreCase("Chinese (Simplified)")) {
                        String path = getServletContext().getRealPath("emails/verified_en.html");
                        html = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
                        html = html.replaceAll("REPLACE1", s.getFirstName() + " ，您好");
                        html = html.replaceAll("REPLACE2", content);

                    }

                    else if(s.getLanguage().equals("Chinese (Traditional)")) {
                        String path = getServletContext().getRealPath("emails/verified_en.html");
                        html = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
                        html = html.replaceAll("REPLACE1", s.getFirstName() + "，您好");
                        html = html.replaceAll("REPLACE2", content);
                    }

                } catch(IOException e) {
                    e.printStackTrace();
                }

                EmailUtil.sendEmail(subject, html, "Thank You", s.getEmail());
            }
        return true;
    }
}
