package org.redhatchallenge.rhc2013.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author: Terry Chia (terrycwk1994@gmail.com)
 */
public class Entry implements EntryPoint {

    @Override
    public void onModuleLoad() {
        RootPanel.get("header").add(new Header());
        ContentContainer.INSTANCE.setContent(new UserScreen());
    }
}
