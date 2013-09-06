package org.redhatchallenge.rhc2013.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
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

    @UiField MenuBar menuBar;
    @UiField MenuItem menuItem1;
    @UiField MenuItem menuItem2;
    @UiField MenuItem menuItem3;
    @UiField MenuItem menuItem4;
    @UiField MenuItem menuItem5;
    @UiField MenuItem menuItem6;
    @UiField MenuItem menuItem7;

    public Header(){
        initWidget(UiBinder.createAndBindUi(this));
        initMenuBar();
    }

    private void initMenuBar(){
        menuItem1.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ContentContainer.INSTANCE.setContent(new UserScreen());
            }
        });

        menuItem2.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ContentContainer.INSTANCE.setContent(new RegisterScreen());
            }
        });

        menuItem3.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ContentContainer.INSTANCE.setContent(new TimeslotScreen());
            }
        });

        menuItem4.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ContentContainer.INSTANCE.setContent(new ChangeTimeScreen());
            }
        });

        menuItem5.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ContentContainer.INSTANCE.setContent(new ResetPasswordScreen());
            }
        });

        menuItem6.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ContentContainer.INSTANCE.setContent(new MassEmailScreen());
            }
        });

        menuItem7.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ContentContainer.INSTANCE.setContent(new DeletedFlagUser());
            }
        });

    }
}
