package org.redhatchallenge.rhc2013.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.redhatchallenge.rhc2013.server.EmailUtil;
import org.redhatchallenge.rhc2013.shared.Student;
import org.redhatchallenge.rhc2013.shared.TimeSlotList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@RemoteServiceRelativePath("MassEmailService")
public interface MassEmailService extends RemoteService {
    public Boolean massEmailSending(List<Student> studentList, String subject, String content) throws IllegalArgumentException;

    public static class Util {
        private static final MassEmailServiceAsync Instance = (MassEmailServiceAsync) GWT.create(MassEmailService.class);

        public static MassEmailServiceAsync getInstance() {
            return Instance;
        }
    }


}
