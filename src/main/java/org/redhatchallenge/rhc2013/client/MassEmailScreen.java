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

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gerald
 * Date: 9/4/13
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class MassEmailScreen extends Composite {
    interface MassEmailScreenUiBinder extends UiBinder<Widget, MassEmailScreen> {
    }

    private static MassEmailScreenUiBinder UiBinder = GWT.create(MassEmailScreenUiBinder.class);

    @UiField Label errorLabel;
    @UiField ListBox emailLanguage;
    @UiField TextBox subjectField;
    @UiField TextArea contentField;
    @UiField Button sendEmail;

    private UserServiceAsync userService = UserService.Util.getInstance();
    private MassEmailServiceAsync emailService = MassEmailService.Util.getInstance();

    private List<Student> studentList;
    private List<Student> emailList = new ArrayList<Student>();
    private String contest1;


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
    }

    @UiHandler("emailLanguage")
    public void handleLanguageChange(ChangeEvent event) {
        emailList.clear();
        switch(emailLanguage.getSelectedIndex()){
            case 0:
                errorLabel.setText("Please Select a Target Group");
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
        String contents[] = contentField.getText().split("\n");
        contest1 = "";
        for (int i =0; i<contents.length; i ++){
            contest1 = contest1 + contents[i] + "<br/>";
        }

        if(emailList.size() != 0){
            emailService.massEmailSending(emailList,subjectField.getText(),contest1, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable throwable) {
                    errorLabel.setText("Unable to Send Mail, Please Try Again later");
                }

                @Override
                public void onSuccess(Boolean aBoolean) {
                    errorLabel.setText("Mail Sent");
                }
            });
        }
        else{
            errorLabel.setText("Please Select a Target Group of Contestant");
        }
    }
}
