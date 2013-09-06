package org.redhatchallenge.rhc2013.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

public enum ContentContainer {

    INSTANCE;

    /**
     * Changes the content's of the "content" panel.
     *
     * @param content  Composite object to assign to the panel.
     */
    public void setContent(Composite content) {
        RootPanel.get("content").clear();
        RootPanel.get("content").add(content);
    }
}
