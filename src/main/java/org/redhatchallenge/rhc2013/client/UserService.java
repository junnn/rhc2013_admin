package org.redhatchallenge.rhc2013.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import org.redhatchallenge.rhc2013.shared.RegStatus;
import org.redhatchallenge.rhc2013.shared.Student;
import org.redhatchallenge.rhc2013.shared.TimeSlotList;

import java.io.IOException;
import java.util.List;

@RemoteServiceRelativePath("UserService")
public interface UserService extends RemoteService {

    int getCacheQuestion(String id) throws IllegalArgumentException;

    List<Student> getListOfStudents() throws IllegalArgumentException;

    List<Student> getListOfDeletedStudents() throws IllegalArgumentException;

    List<TimeSlotList> getListOfTimeSlot() throws IllegalArgumentException;

    Boolean updateStudentData(Student student) throws IllegalArgumentException;

    Boolean deleteStudents(List<Student> students) throws IllegalArgumentException;

    Boolean assignTimeSlot(List<Student> students, String timeSlot) throws IllegalArgumentException;
    public Boolean registerStudent(String email, String password, String firstName, String lastName, String contact,
                                 String country, String countryCode, String school, String lecturerFirstName, String lecturerLastName,
                                 String lecturerEmail, String language, Boolean verified) throws IllegalArgumentException;

    Boolean resetTestDetails(Student student) throws IllegalArgumentException;

    String exportCsv(List<Student> students) throws IllegalArgumentException;

    void assignTimeslotAndQuestions(String email) throws IllegalArgumentException;

    public boolean resetPassword(String password, List<Student> students) throws IllegalArgumentException;

    public List<RegStatus> getRegStatus() throws IllegalArgumentException;

    public Boolean updateRegistraionStatus(RegStatus status) throws IllegalArgumentException;

    public static class Util {
        private static final UserServiceAsync Instance = (UserServiceAsync) GWT.create(UserService.class);

        public static UserServiceAsync getInstance() {
            return Instance;
        }
    }
}
