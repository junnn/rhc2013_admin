package org.redhatchallenge.rhc2013.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.redhatchallenge.rhc2013.shared.Student;
import org.redhatchallenge.rhc2013.client.MassEmailService;
import org.redhatchallenge.rhc2013.shared.TimeSlotList;

import java.util.ArrayList;
import java.util.List;


public class MassEmailScreen extends Composite {
    interface MassEmailScreenUiBinder extends UiBinder<Widget, MassEmailScreen> {
    }

    private static MassEmailScreenUiBinder UiBinder = GWT.create(MassEmailScreenUiBinder.class);

    @UiField Label errorLabel;
    @UiField ListBox emailLanguage;
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
    private List<Student> list = new ArrayList<Student>();
    private int batchNo;


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
            }
        });
    }

    @UiHandler("emailLanguage")
    public void handleLanguageChange(ChangeEvent event) {
        emailList.clear();
        sendEmail.setEnabled(true);
        switch(emailLanguage.getSelectedIndex()){
            case 0:
                break;

            case 1:
                for(Student s : studentList){
                    if(s.getLanguage().equals("English")){
                        emailList.add(s);
                    }
                }
                break;

            case 2:
                for(Student s : studentList){
                    if(s.getLanguage().equals("Chinese (Simplified)")){
                        emailList.add(s);
                    }
                }
                break;

            case 3:
                for(Student s : studentList){
                    if(s.getLanguage().equals("Chinese (Traditional)")){
                        emailList.add(s);
                    }
                }
                break;
        }
    }


    @UiHandler("sendEmail")
    public void handleSendEmailButtonClick(ClickEvent event) {
        batchNo = 0;
        if(!emailLanguage.getItemText(emailLanguage.getSelectedIndex()).equals("Please Select")){
            sendEmail.setEnabled(false);
            if(emailList.size() != 0){
                String contents[] = contentField.getText().split("\n");
                contest1 = "";

                for (int i = 0; i<contents.length; i ++){
                    contest1 = contest1 + contents[i] + "<br/>";
                }

                for(TimeSlotList t : ListofTimeSlot){
                    long timeSlot = t.getTimeslot();
                    list.clear();

                    for(Student s : emailList){
                        if(s.getCountry().contains("China")){
                            if(s.getTimeslot() == timeSlot){
                                list.add(s);
                            }
                        }
                    }

                    if(list.size() != 0){
                        emailService.massEmailSending(list,subjectField.getText(),contest1, new AsyncCallback<Boolean>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                errorLabel.setText("Unable to Send Mail, Please Try Again later");
                            }

                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                if(aBoolean.equals(true)){
                                    batchNo++;
                                    errorLabel.setText("Mail Batch No. " + batchNo +" sent");


                                }
                                else{
                                    errorLabel.setText("Some Email might not Sent!");
                                }


                            }
                        });
                    }
                }
            }

            else{
                errorLabel.setText("*No contestant with " + emailLanguage.getItemText(emailLanguage.getSelectedIndex()) + " as their preferred language!");
            }
        }
        else{
            errorLabel.setText("Please select a language!");

        }
    }


    @UiHandler({"emailLanguage", "subjectField", "contentField"})
    public void handleFieldClick(ClickEvent event) {
        languageLabel.setText("");
        errorLabel.setText("");
    }

    @UiHandler("clearText")
    public void handleClearTextButton(ClickEvent event){
        contentField.setText("");
    }

}
