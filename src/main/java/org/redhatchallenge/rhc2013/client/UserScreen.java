package org.redhatchallenge.rhc2013.client;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import org.redhatchallenge.rhc2013.shared.Student;
import org.redhatchallenge.rhc2013.shared.TimeSlotList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

/**
 * @author: Terry Chia (terrycwk1994@gmail.com)
 */
public class UserScreen extends Composite {
    interface UserScreenUiBinder extends UiBinder<Widget, UserScreen> {
    }

    private static UserScreenUiBinder UiBinder = GWT.create(UserScreenUiBinder.class);

    @UiField TextBox searchField;
    @UiField ListBox searchTerms;
    @UiField Button searchButton;
    @UiField Button deleteButton;
    @UiField Button exportButton;
    @UiField Button refreshButton;
    @UiField CellTable<Student> cellTable;
    @UiField MySimplePager pager;
    @UiField Label registrationLabel;
    @UiField Label verifiedLabel;
    @UiField ListBox timeSlotList;
    @UiField Button timeSlotButton;
    @UiField Label errorLabel;


    private UserServiceAsync userService = UserService.Util.getInstance();
    private List<Student> studentList;
    private List<Student> origStudentList;
    private ListDataProvider<Student> provider;
    private List<Student> listOfSelectedStudents = new ArrayList<Student>();
    private List<TimeSlotList> ListofTimeSlot;
    private List<String> dateList = new ArrayList<String>();
    private static final ProvidesKey<Student> KEY_PROVIDER = new ProvidesKey<Student>() {
        @Override
        public Object getKey(Student item) {
            return item.getEmail();
        }
    };

    public UserScreen() {

        initWidget(UiBinder.createAndBindUi(this));

        userService.getListOfStudents(new AsyncCallback<List<Student>>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(List<Student> result) {
                origStudentList = new ArrayList<Student>(result);
                studentList = result;

                setUserCount();

                provider = new ListDataProvider<Student>(studentList);
                provider.addDataDisplay(cellTable);

                initCellTable();
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
                timeSlotList.insertItem("Select All", 0);
                for(int i = 0; i < dateList.size(); i++){
                    timeSlotList.insertItem(dateList.get(i).toString(),i+1);
                }
            }
        });


        pager.setDisplay(cellTable);
        pager.setPageSize(8);
    }

    private void initCellTable() {

        List list = provider.getList();

        final MultiSelectionModel<Student> selectionModel = new MultiSelectionModel<Student>(KEY_PROVIDER);

        ListHandler<Student> sortHandler = new ListHandler<Student>(list);
        cellTable.addColumnSortHandler(sortHandler);


        Column<Student, Boolean> selectColumn = new Column<Student, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(Student student) {
                return selectionModel.isSelected(student);

            }
        };

        selectColumn.setFieldUpdater(new FieldUpdater<Student, Boolean>() {
            @Override
            public void update(int index, Student student, Boolean value) {
                if(value) {
                    listOfSelectedStudents.add(student);
                }

                else {
                    listOfSelectedStudents.remove(student);
                }
            }
        });

        // checkbox header
        Header<Boolean> selectAllHeader = new Header<Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue() {
                for (Student student : cellTable.getVisibleItems()){
                    if (!selectionModel.isSelected(student)){
                        return false;
                    }
                }
                return cellTable.getVisibleItems().size() > 0;
            }
        };

        selectAllHeader.setUpdater(new ValueUpdater<Boolean>() {
            @Override
            public void update(Boolean aBoolean) {
                for(Student student : cellTable.getVisibleItems()){
                    selectionModel.setSelected(student, aBoolean);
                }
                if (aBoolean == true){
                    for (int i=0;i<cellTable.getVisibleItemCount(); i++){
                        if (!listOfSelectedStudents.contains(cellTable.getVisibleItem(i)))
                            listOfSelectedStudents.add(cellTable.getVisibleItem(i));
                    }
                }
                else if (aBoolean == false){
                    for (int i=0;i<cellTable.getVisibleItemCount(); i++){
                        listOfSelectedStudents.remove(cellTable.getVisibleItem(i));
                    }
                }
            }
        });//End of checkbox

        Column<Student, String> emailColumn = new Column<Student, String>(new EditTextCell()) {
            @Override
            public String getValue(Student student) {
                return student.getEmail();
            }
        };

        emailColumn.setSortable(true);
        sortHandler.setComparator(emailColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? o1.getEmail().compareTo(o2.getEmail()) : 1;
                }
                return -1;
            }
        });

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

        firstNameColumn.setSortable(true);
        sortHandler.setComparator(firstNameColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? o1.getFirstName().compareTo(o2.getFirstName()) : 1;
                }
                return -1;
            }
        });


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

        lastNameColumn.setSortable(true);
        sortHandler.setComparator(lastNameColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? o1.getLastName().compareTo(o2.getLastName()) : 1;
                }
                return -1;
            }
        });

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
            countryList.add("China/Beijing");
            countryList.add("China/Shanghai");
            countryList.add("China/Wuhan");
            countryList.add("China/Dalian");
            countryList.add("China/Jinan");
            countryList.add("China/Others");
            countryList.add("Hong Kong");
            countryList.add("Taiwan");

        Column<Student, String> countryColumn = new Column<Student, String>(new SelectionCell(countryList)) {
            @Override
            public String getValue(Student student) {
                return student.getCountry();
            }
        };

        countryColumn.setSortable(true);
        sortHandler.setComparator(countryColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? o1.getCountry().compareTo(o2.getCountry()) : 1;
                }
                return -1;
            }
        });

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
                        if (!result) {
                            displayErrorBox("Failed", "Update has failed");
                        } else {
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

        contactColumn.setSortable(true);
        sortHandler.setComparator(contactColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? o1.getContact().compareTo(o2.getContact()) : 1;
                }
                return -1;
            }
        });

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
                        if (!result) {
                            displayErrorBox("Failed", "Update has failed");
                        } else {
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


        schoolColumn.setSortable(true);
        sortHandler.setComparator(schoolColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? o1.getSchool().compareTo(o2.getSchool()) : 1;
                }
                return -1;
            }
        });

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
                        if (!result) {
                            displayErrorBox("Failed", "Update has failed");
                        } else {
                            cellTable.redraw();
                        }
                    }
                });
            }
        });

        Column<Student, String> timeSlotColumn = new Column<Student, String>(new TextCell()) {
            @Override
            public String getValue(Student student) {
                if(student.getTimeslot() == 0){
                    return "Time Slot is not Assigned";
                }
                else{
                    Date date = new Date(student.getTimeslot());
                    return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(date);
                }
            }
        };

        timeSlotColumn.setSortable(true);
        sortHandler.setComparator(timeSlotColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? String.valueOf(o1.getTimeslot()).compareTo(String.valueOf(o2.getTimeslot())) : 1;
                }
                return -1;
            }
        });

        Column<Student, String> lecturerFirstNameColumn = new Column<Student, String>(new EditTextCell()) {
            @Override
            public String getValue(Student student) {
                return student.getLecturerFirstName();
            }
        };


        lecturerFirstNameColumn.setSortable(true);
        sortHandler.setComparator(lecturerFirstNameColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? o1.getLecturerFirstName().compareTo(o2.getLecturerFirstName()) : 1;
                }
                return -1;
            }
        });

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


        lecturerLastNameColumn.setSortable(true);
        sortHandler.setComparator(lecturerLastNameColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? o1.getLecturerLastName().compareTo(o2.getLecturerLastName()) : 1;
                }
                return -1;
            }
        });

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

        lecturerEmailColumn.setSortable(true);
        sortHandler.setComparator(lecturerEmailColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? o1.getLecturerEmail().compareTo(o2.getLecturerEmail()) : 1;
                }
                return -1;
            }
        });

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


        languageColumn.setSortable(true);
        sortHandler.setComparator(languageColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? o1.getLanguage().compareTo(o2.getLanguage()) : 1;
                }
                return -1;
            }
        });

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
            public void update(int index, final Student object, Boolean value) {
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
                            userService.assignTimeslotAndQuestions(object.getEmail(), new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    caught.printStackTrace();
                                }

                                @Override
                                public void onSuccess(Void result) {
                                    cellTable.redraw();
                                    setUserCount();
                                }
                            });

                        }
                    }
                });

            }
        });

        verifiedColumn.setSortable(true);
        sortHandler.setComparator(verifiedColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? o1.getVerified().compareTo(o2.getVerified()) : 1;
                }
                return -1;
            }
        });

        cellTable.addColumn(selectColumn, selectAllHeader);
        cellTable.addColumn(emailColumn, "Email");
        cellTable.addColumn(firstNameColumn, "First Name");
        cellTable.addColumn(lastNameColumn, "Last Name");
        cellTable.addColumn(countryColumn, "Region");
        cellTable.addColumn(countryCodeColumn, "Country Code");
        cellTable.addColumn(contactColumn, "Contact");
        cellTable.addColumn(schoolColumn, "School");
        cellTable.addColumn(timeSlotColumn, "Time Slot");
        cellTable.addColumn(lecturerFirstNameColumn, "Lecturer's First Name");
        cellTable.addColumn(lecturerLastNameColumn, "Lecturer's Last Name");
        cellTable.addColumn(lecturerEmailColumn, "Lecturer's Email");
        cellTable.addColumn(languageColumn, "Language");
        cellTable.addColumn(verifiedColumn, "Verified");

        cellTable.setSelectionModel(selectionModel, DefaultSelectionEventManager.<Student> createCheckboxManager(cellTable.getColumnIndex(selectColumn)));
    }

    @UiHandler("timeSlotList")
    public void handleTimeSlot(ChangeEvent event){
        switch(timeSlotList.getSelectedIndex()){
            case 1:
                errorLabel.setText("");
            case 2:
                errorLabel.setText("");
            case 3:
                errorLabel.setText("");
            case 4:
                errorLabel.setText("");
            case 5:
                errorLabel.setText("");
            case 6:
                errorLabel.setText("");
            case 7:
                errorLabel.setText("");
            case 8:
                errorLabel.setText("");
            case 9:
                errorLabel.setText("");
            case 10:
                errorLabel.setText("");
            case 11:
                errorLabel.setText("");
            case 12:
                errorLabel.setText("");
        }
    }

    @UiHandler("searchButton")
    public void handleSearchButtonClick(ClickEvent event) {
        String contains = searchField.getText();
        List<Student> list = new ArrayList<Student>();

        if(contains.equals("")) {
            for (Student s : origStudentList){
                list.add(s);
            }
            provider.getList().clear();
            provider.getList().addAll(list);
        }

        else {
            String category = searchTerms.getItemText(searchTerms.getSelectedIndex());
            if(category.equalsIgnoreCase("Email")) {
                for(Student s : origStudentList) {
                    if(s.getEmail().contains(contains)) {
                        list.add(s);
                    }
                }
            }

            else if(category.equalsIgnoreCase("First Name")) {
                for(Student s : origStudentList) {
                    if(s.getFirstName().contains(contains)) {
                        list.add(s);
                    }
                }
            }

            else if(category.equalsIgnoreCase("Last Name")) {
                for(Student s : origStudentList) {
                    if(s.getLastName().contains(contains)) {
                        list.add(s);
                    }
                }
            }

            else if(category.equalsIgnoreCase("Contact")) {
                for(Student s : origStudentList) {
                    if(s.getContact().contains(contains)) {
                        list.add(s);
                    }
                }
            }

            else if(category.equalsIgnoreCase("Country")) {
                for(Student s : origStudentList) {
                    if(s.getCountry().contains(contains)) {
                        list.add(s);
                    }
                }
            }

            else if(category.equalsIgnoreCase("Country Code")) {
                for(Student s : origStudentList) {
                    if(s.getCountryCode().contains(contains)) {
                        list.add(s);
                    }
                }
            }

            else if(category.equalsIgnoreCase("School")) {
                for(Student s : origStudentList) {
                    if(s.getFirstName().contains(contains)) {
                        list.add(s);
                    }
                }
            }

            else if(category.equalsIgnoreCase("Lecturer's First Name")) {
                for(Student s : origStudentList) {
                    if(s.getLecturerFirstName().contains(contains)) {
                        list.add(s);
                    }
                }
            }

            else if(category.equalsIgnoreCase("Lecturer's Last Name")) {
                for(Student s : origStudentList) {
                    if(s.getLecturerLastName().contains(contains)) {
                        list.add(s);
                    }
                }
            }

            else if(category.equalsIgnoreCase("Lecturer's Email")) {
                for(Student s : origStudentList) {
                    if(s.getLecturerEmail().contains(contains)) {
                        list.add(s);
                    }
                }
            }

            else if(category.equalsIgnoreCase("Language")) {
                for(Student s : origStudentList) {
                    if(s.getLanguage().contains(contains)) {
                        list.add(s);
                    }
                }
            }

            else {
                for (Student s : origStudentList){
                    list.add(s);
                }
            }

            provider.getList().clear();
            provider.getList().addAll(list);
        }
    }

    @UiHandler("timeSlotButton")
    public void handleTimeSlotButtonClick(ClickEvent event) {
        final String timeSlot;
        if(!timeSlotList.getItemText(timeSlotList.getSelectedIndex()).equals("Please Select a Time Slot")){
            timeSlot = timeSlotList.getItemText(timeSlotList.getSelectedIndex());
            userService.assignTimeSlot(listOfSelectedStudents, timeSlot, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                }

                @Override
                public void onSuccess(Boolean result) {
                    if(!result) {
                        displayErrorBox("Error", "Unable to Assign Time Slot");
                    }

                    else {
                        registrationLabel.setText("Thank You");
                        ContentContainer.INSTANCE.setContent(new UserScreen());
                    }
                }
            });

            cellTable.redraw();
        }
        else{
            errorLabel.setText("No Time Slot Selected! Please try again!");
        }




    }

    @UiHandler("deleteButton")
    public void handleDeleteButtonClick(ClickEvent event) {
       Boolean delete =  Window.confirm("Are you sure you want to delete");
        if (delete.equals(true)){

            userService.deleteStudents(listOfSelectedStudents, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                }

                @Override
                public void onSuccess(Boolean result) {
                    if(!result) {
                        displayErrorBox("Error", "Error with deleting users");
                    }

                    else {
                        List<Student> toBeRemoved = new ArrayList<Student>();
                        for(Student s : studentList) {
                            if(listOfSelectedStudents.contains(s)) {
                                toBeRemoved.add(s);
                            }
                        }
                        studentList.removeAll(toBeRemoved);
                        provider.setList(studentList);
                        listOfSelectedStudents.clear(); //remove list of selected & deleted users
                        setUserCount();
                    }
                }
            });
        }
        else {
            Window.alert("User not Deleted.");
        }


    }

    @UiHandler("exportButton")
    public void handleExportButtonClick(ClickEvent event) {
        /**
         * The following two lines is to avoid the issue
         * of .getList() returning a ListWrapper type
         * instead of a Serializable list type which
         * causes a SerializationException to be thrown.
         *
         * See: http://blog.rubiconred.com/2011/04/gwt-serializationexception-on-rpc-call.html
         */
        List<Student> list = new ArrayList<Student>();
        list.addAll(provider.getList());

        userService.exportCsv(list, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(String result) {
                String url = GWT.getHostPageBaseURL() + "administration/download?file=" + result;

                Frame downloadFrame = Frame.wrap(Document.get().getElementById("__gwt_downloadFrame"));
                downloadFrame.setUrl(url);
            }
        });
    }

    @UiHandler("refreshButton")
    public void handleRefreshButtonClick(ClickEvent event) {
        ContentContainer.INSTANCE.setContent(new UserScreen());
    }

    private void setUserCount() {

        int count = 0;

        registrationLabel.setText("Total number of registered user: " + studentList.size());
        for (Student student : studentList) {
            if (student.getVerified()) {
                count++;
            }
        }

        verifiedLabel.setText("Total number of verified user:  " + count);
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