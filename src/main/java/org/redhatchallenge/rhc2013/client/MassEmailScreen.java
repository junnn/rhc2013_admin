package org.redhatchallenge.rhc2013.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.redhatchallenge.rhc2013.shared.Student;
import org.redhatchallenge.rhc2013.client.MassEmailService;
import org.redhatchallenge.rhc2013.shared.TimeSlotList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MassEmailScreen extends Composite {
    interface MassEmailScreenUiBinder extends UiBinder<Widget, MassEmailScreen> {
    }

    private static MassEmailScreenUiBinder UiBinder = GWT.create(MassEmailScreenUiBinder.class);

    @UiField Label errorLabel;
    @UiField ListBox timeSlotList;
    @UiField TextBox subjectField;
    @UiField TextArea contentField;
    @UiField Button sendEmail;
    @UiField Button clearText;
    @UiField Label languageLabel;

    private UserServiceAsync userService = UserService.Util.getInstance();
    private MassEmailServiceAsync emailService = MassEmailService.Util.getInstance();

    private List<Student> studentList;
    private List<Student> emailList = new ArrayList<Student>();
    private String contest1;
    private List<TimeSlotList> ListofTimeSlot;
    private List<String> dateList = new ArrayList<String>();


    public MassEmailScreen(){
        initWidget(UiBinder.createAndBindUi(this));
        contentField.setCharacterWidth(85);
        contentField.setVisibleLines(30);
        userService.getListOfStudents(new AsyncCallback<List<Student>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Student> students) {
                studentList = students;
            }
        });
        userService.getListOfTimeSlot(new AsyncCallback<List<TimeSlotList>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<TimeSlotList> timeSlotLists) {
                ListofTimeSlot = new ArrayList<TimeSlotList>(timeSlotLists);
                for(TimeSlotList d : ListofTimeSlot){
                    Date date = convertTimeSlot(d.getTimeslot());
                    String formatedDate = returnLongDateTime(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(date));
                    dateList.add(formatedDate);

                }

                timeSlotList.clear();
                timeSlotList.insertItem("Please Select", 0);
                for(int i = 0; i < dateList.size(); i++){
                    timeSlotList.insertItem(dateList.get(i).toString(),i+1);
                }
            }
        });
    }

    @UiHandler("timeSlotList")
    public void handleTimeslotChangeEvent(ChangeEvent event) {

        emailList.clear();

        String timeslot = timeSlotList.getItemText(timeSlotList.getSelectedIndex());

        for (Student s : studentList){
            Date convertlongtodate = new Date(s.getTimeslot());
            String date = returnLongDateTime(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(convertlongtodate));
            if (timeslot.equals(date)){
                emailList.add(s);
            }
        }

        languageLabel.setText(""+emailList);
    }

    @UiHandler("sendEmail")
    public void handleSendEmailButtonClick(ClickEvent event) {
        String contents[] = contentField.getText().split("\n");
        contest1 = "";
        for (int i =0; i<contents.length; i ++){
            contest1 = contest1 + contents[i] + "<br/>";
        }

        if(emailList.size() != 0){
            sendEmail.setEnabled(false);
            emailService.massEmailSending(emailList,subjectField.getText(),contest1, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable throwable) {
                    throwable.printStackTrace();
                    errorLabel.setText("Unable to Send Mail, Please Try Again later");
                    sendEmail.setEnabled(true);
                }

                @Override
                public void onSuccess(Boolean aBoolean) {
                    errorLabel.setText("Mail Sent");
                    sendEmail.setEnabled(true);
                }
            });
        }
        else if(timeSlotList.getItemText(timeSlotList.getSelectedIndex()).equals("Please Select")){
            languageLabel.setText("*Please select a timeslot!");
        }
        else{
            errorLabel.setText("*No contestant within selected timeslot.");
        }
    }

    @UiHandler({"timeSlotList", "subjectField", "contentField"})
    public void handleFieldClick(ClickEvent event) {
        languageLabel.setText("");
        errorLabel.setText("");
    }

    @UiHandler("clearText")
    public void handleClearTextButton(ClickEvent event){
        contentField.setText("");
    }

    private String returnLongDateTime(String date){
        String LongDate;
        if(date.equals("2013-10-23 09:00")){
            LongDate = "23 October 2013, 9:00AM to 10:00AM";
            return LongDate;
        }
        else if(date.equals("2013-10-23 10:15")){
            LongDate = "23 October 2013, 10:15AM to 11:15AM";
            return LongDate;
        }

        else if(date.equals("2013-10-23 11:30")){
            LongDate = "23 October 2013, 11:30AM to 12:30PM";
            return LongDate;
        }
        else if(date.equals("2013-10-23 12:45")){
            LongDate = "23 October 2013, 12:45PM to 13:45PM";
            return LongDate;
        }
        else if(date.equals("2013-10-23 14:00")){
            LongDate = "23 October 2013, 14:00PM to 15:00PM";
            return LongDate;
        }
        else if(date.equals("2013-10-23 15:15")){
            LongDate = "23 October 2013, 15:15PM to 16:15PM";
            return LongDate;
        }
        else if(date.equals("2013-10-23 16:30")){
            LongDate = "23 October 2013, 16:30PM to 17:30PM";
            return LongDate;
        }
        else if(date.equals("2013-10-23 17:45")){
            LongDate = "23 October 2013, 17:45PM to 18:45PM";
            return LongDate;
        }
        else if(date.equals("2013-10-23 19:00")){
            LongDate = "23 October 2013, 19:00PM to 20:00PM";
            return LongDate;
        }
        else if(date.equals("2013-10-23 20:15")){
            LongDate = "23 October 2013, 20:15PM to 21.15PM";
            return LongDate;
        }

        else if(date.equals("2013-10-24 14:00")){
            LongDate = "24 October 2013, 14:00PM to 15.00PM";
            return LongDate;
        }

        else if(date.equals("2013-10-24 16:00")){
            LongDate = "24 October 2013, 16:00PM to 17.00PM";
            return LongDate;
        }
        else{
            LongDate = "Error";
            return LongDate;
        }
    }

    private Date convertTimeSlot(long unixTime){
        Date date = new Date(unixTime);
        return date;
    }
}
