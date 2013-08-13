package org.redhatchallenge.rhc2013.client;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import org.redhatchallenge.rhc2013.shared.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Terry Chia (terrycwk1994@gmail.com)
 */
public class UserScreen extends Composite {
    interface UserScreenUiBinder extends UiBinder<Widget, UserScreen> {
    }

    private static UserScreenUiBinder UiBinder = GWT.create(UserScreenUiBinder.class);

    @UiField CellTable<Student> cellTable;
    @UiField SimplePager pager;

    private UserServiceAsync userService = UserService.Util.getInstance();

    public UserScreen() {
        initWidget(UiBinder.createAndBindUi(this));

        initCellTable();

        AsyncDataProvider<Student> provider = new AsyncDataProvider<Student>() {
            @Override
            protected void onRangeChanged(HasData<Student> display) {
                userService.getListOfStudents(new AsyncCallback<List<Student>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(List<Student> result) {
                        updateRowData(0, result);
                        updateRowCount(result.size(), true);

                    }
                });
            }
        };

        provider.addDataDisplay(cellTable);
        pager.setDisplay(cellTable);
    }

    private void initCellTable() {

        Column<Student, String> emailColumn = new Column<Student, String>(new EditTextCell()) {
            @Override
            public String getValue(Student student) {
                return student.getEmail();
            }
        };

        emailColumn.setFieldUpdater(new FieldUpdater<Student, String>() {
            @Override
            public void update(int index, Student object, String value) {
                object.setEmail(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            displayErrorBox("Failed", "Update has failed");
                        }

                        else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        Column<Student, String> firstNameColumn = new Column<Student, String>(new EditTextCell()) {
            @Override
            public String getValue(Student student) {
                return student.getFirstName();
            }
        };

        firstNameColumn.setFieldUpdater(new FieldUpdater<Student, String>() {
            @Override
            public void update(int index, Student object, String value) {
                object.setFirstName(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            displayErrorBox("Failed", "Update has failed");
                        }

                        else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        Column<Student, String> lastNameColumn = new Column<Student, String>(new EditTextCell()) {
            @Override
            public String getValue(Student student) {
                return student.getLastName();
            }
        };

        lastNameColumn.setFieldUpdater(new FieldUpdater<Student, String>() {
            @Override
            public void update(int index, Student object, String value) {
                object.setLastName(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            displayErrorBox("Failed", "Update has failed");
                        }

                        else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        ArrayList<String> countryList = new ArrayList<String>();
            countryList.add("Singapore");
            countryList.add("Malaysia");
            countryList.add("Thailand");
            countryList.add("China");
            countryList.add("Hong Kong");
            countryList.add("Taiwan");

        Column<Student, String> countryColumn = new Column<Student, String>(new SelectionCell(countryList)) {
            @Override
            public String getValue(Student student) {
                return student.getCountry();
            }
        };

        countryColumn.setFieldUpdater(new FieldUpdater<Student, String>() {
            @Override
            public void update(int index, Student object, String value) {
                object.setCountry(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            displayErrorBox("Failed", "Update has failed");
                        }

                        else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        ArrayList<String> countryCodeList = new ArrayList<String>();
            countryCodeList.add("+65");
            countryCodeList.add("+60");
            countryCodeList.add("+66");
            countryCodeList.add("+86");
            countryCodeList.add("+852");
            countryCodeList.add("+886");

        Column<Student, String> countryCodeColumn = new Column<Student, String>(new SelectionCell(countryCodeList)) {
            @Override
            public String getValue(Student student) {
                return student.getCountryCode();
            }
        };

        countryCodeColumn.setFieldUpdater(new FieldUpdater<Student, String>() {
            @Override
            public void update(int index, Student object, String value) {
                object.setCountryCode(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            displayErrorBox("Failed", "Update has failed");
                        }

                        else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        Column<Student, String> contactColumn = new Column<Student, String>(new EditTextCell()) {
            @Override
            public String getValue(Student student) {
                return student.getContact();
            }
        };

        contactColumn.setFieldUpdater(new FieldUpdater<Student, String>() {
            @Override
            public void update(int index, Student object, String value) {
                object.setContact(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            displayErrorBox("Failed", "Update has failed");
                        }

                        else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        Column<Student, String> schoolColumn = new Column<Student, String>(new EditTextCell()) {
            @Override
            public String getValue(Student student) {
                return student.getSchool();
            }
        };

        schoolColumn.setFieldUpdater(new FieldUpdater<Student, String>() {
            @Override
            public void update(int index, Student object, String value) {
                object.setSchool(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            displayErrorBox("Failed", "Update has failed");
                        }

                        else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        Column<Student, String> lecturerFirstNameColumn = new Column<Student, String>(new EditTextCell()) {
            @Override
            public String getValue(Student student) {
                return student.getLecturerFirstName();
            }
        };

        lecturerFirstNameColumn.setFieldUpdater(new FieldUpdater<Student, String>() {
            @Override
            public void update(int index, Student object, String value) {
                object.setLecturerFirstName(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            displayErrorBox("Failed", "Update has failed");
                        }

                        else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        Column<Student, String> lecturerLastNameColumn = new Column<Student, String>(new EditTextCell()) {
            @Override
            public String getValue(Student student) {
                return student.getLecturerLastName();
            }
        };


        lecturerLastNameColumn.setFieldUpdater(new FieldUpdater<Student, String>() {
            @Override
            public void update(int index, Student object, String value) {
                object.setLecturerLastName(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            displayErrorBox("Failed", "Update has failed");
                        }

                        else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        Column<Student, String> lecturerEmailColumn = new Column<Student, String>(new EditTextCell()) {
            @Override
            public String getValue(Student student) {
                return student.getLecturerEmail();
            }
        };

        lecturerEmailColumn.setFieldUpdater(new FieldUpdater<Student, String>() {
            @Override
            public void update(int index, Student object, String value) {
                object.setLecturerEmail(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            displayErrorBox("Failed", "Update has failed");
                        }

                        else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        ArrayList<String> languageList = new ArrayList<String>();
            languageList.add("English");
            languageList.add("Chinese (Simplified)");
            languageList.add("Chinese (Tranditional)");

        Column<Student, String> languageColumn = new Column<Student, String>(new SelectionCell(languageList)) {
            @Override
            public String getValue(Student student) {
                return student.getLanguage();
            }
        };

        languageColumn.setFieldUpdater(new FieldUpdater<Student, String>() {
            @Override
            public void update(int index, Student object, String value) {
                object.setLanguage(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            displayErrorBox("Failed", "Update has failed");
                        }

                        else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        Column<Student, Boolean> verifiedColumn = new Column<Student, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(Student student) {
                return student.getVerified();
            }
        };

        verifiedColumn.setFieldUpdater(new FieldUpdater<Student, Boolean>() {
            @Override
            public void update(int index, Student object, Boolean value) {
                object.setVerified(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if (!result) {
                            displayErrorBox("Failed", "Update has failed");
                        } else {
                            cellTable.redraw();
                        }
                    }
                });

            }
        });

        Column<Student, Boolean> statusColumn = new Column<Student, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(Student student) {
                return student.getStatus();
            }
        };

        statusColumn.setFieldUpdater(new FieldUpdater<Student, Boolean>() {
            @Override
            public void update(int index, Student object, Boolean value) {
                object.setStatus(value);
                userService.updateStudentData(object, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            displayErrorBox("Failed", "Update has failed");
                        }

                        else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        cellTable.addColumn(emailColumn, "Email");
        cellTable.addColumn(firstNameColumn, "First Name");
        cellTable.addColumn(lastNameColumn, "Last Name");
        cellTable.addColumn(countryColumn, "Country");
        cellTable.addColumn(countryCodeColumn, "Country Code");
        cellTable.addColumn(contactColumn, "Contact");
        cellTable.addColumn(schoolColumn, "School");
        cellTable.addColumn(lecturerFirstNameColumn, "Lecturer's First Name");
        cellTable.addColumn(lecturerLastNameColumn, "Lecturer's Last Name");
        cellTable.addColumn(lecturerEmailColumn, "Lecturer's Email");
        cellTable.addColumn(languageColumn, "Language");
        cellTable.addColumn(verifiedColumn, "Verified");
        cellTable.addColumn(statusColumn, "Status");
    }

    private void displayErrorBox(String errorHeader, String message) {
        final DialogBox errorBox = new DialogBox();
        errorBox.setText(errorHeader);
        final HTML errorLabel = new HTML();
        errorLabel.setHTML(message);
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        final Button closeButton = new Button("Close");
        closeButton.setEnabled(true);
        closeButton.getElement().setId("close");
        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                errorBox.hide();
            }
        });
        verticalPanel.add(errorLabel);
        verticalPanel.add(closeButton);
        errorBox.setWidget(verticalPanel);
        errorBox.center();
    }

}