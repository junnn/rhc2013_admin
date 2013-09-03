package org.redhatchallenge.rhc2013.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created with IntelliJ IDEA.
 * User: Jun
 * Date: 3/9/13
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class Header extends Composite{
    interface HeaderUiBinder extends UiBinder<Widget, Header> {
    }

    private static HeaderUiBinder UiBinder = GWT.create(HeaderUiBinder.class);

    @UiField Hyperlink timeslotManagement; //go to Timeslot Screen
    @UiField Hyperlink home;    //go to User Screen
    @UiField Hyperlink changeTimimgOfSlot; //go to Change Time Screen
    @UiField Hyperlink addContestant; // go to register Screen
    @UiField Hyperlink resetPassword;

    public Header(){
        initWidget(UiBinder.createAndBindUi(this));
    }

    @UiHandler("addContestant")
    public void handleRegisterButtonClick(ClickEvent event) {
        ContentContainer.INSTANCE.setContent(new RegisterScreen());
    }

    @UiHandler("timeslotManagement")
    public void handleTimeSlotMngButtonClick(ClickEvent event) {
        ContentContainer.INSTANCE.setContent(new TimeslotScreen());
    }

    @UiHandler("home")
    public void homeLinkClick(ClickEvent event) {
        ContentContainer.INSTANCE.setContent(new UserScreen());
    }

    @UiHandler("changeTimimgOfSlot")
    public void changeTimeLinkClick(ClickEvent event) {
        ContentContainer.INSTANCE.setContent(new ChangeTimeScreen());
    }

    @UiHandler("resetPassword")
    public void resetPasswordLinkClick(ClickEvent event) {
        ContentContainer.INSTANCE.setContent(new ResetPasswordScreen());
    }
}
