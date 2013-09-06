package org.redhatchallenge.rhc2013.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.redhatchallenge.rhc2013.shared.Student;
import org.redhatchallenge.rhc2013.shared.TimeSlotList;

import java.util.List;


public interface MassEmailServiceAsync {
    void massEmailSending(List<Student> studentList, String subject, String content, AsyncCallback<Boolean> async);

}
