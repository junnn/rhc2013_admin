package org.redhatchallenge.rhc2013.client;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import org.redhatchallenge.rhc2013.shared.FieldVerifier;
import org.redhatchallenge.rhc2013.shared.Student;
import org.redhatchallenge.rhc2013.shared.TimeSlotList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResetPasswordScreen extends Composite {
    interface ResetPasswordScreenUiBinder extends UiBinder<Widget, ResetPasswordScreen> {
    }

    private static ResetPasswordScreenUiBinder UiBinder = GWT.create(ResetPasswordScreenUiBinder.class);

    @UiField CellTable<Student> cellTable;
    @UiField MySimplePager pager;
    @UiField Button resetPasswordButton;
    @UiField TextBox passwordField;
    @UiField TextBox confirmPasswordField;
    @UiField Label resetPasswordLabel;
    @UiField Label newPasswordLabel;
    @UiField Label confirmPasswordLabel;
    @UiField TextBox searchField;
    @UiField ListBox searchTerms;
    @UiField Button searchButton;
    @UiField Button refreshButton;

    private UserServiceAsync userService = UserService.Util.getInstance();
    private List<Student> studentList;
    private List<Student> origStudentList;
    private ListDataProvider<Student> provider;
    private List<Student> listOfSelectedStudents = new ArrayList<Student>();
    private static final ProvidesKey<Student> KEY_PROVIDER = new ProvidesKey<Student>() {
        @Override
        public Object getKey(Student item) {
            return item.getEmail();
        }
    };


    public ResetPasswordScreen() {
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

                provider = new ListDataProvider<Student>(studentList);
                provider.addDataDisplay(cellTable);

                initCellTable();
            }
        });

        pager.setDisplay(cellTable);
        pager.setPageSize(10);

    }

    private void initCellTable() {

        List list = provider.getList();

        final MultiSelectionModel<Student> selectionModel = new MultiSelectionModel<Student>(KEY_PROVIDER);

        ColumnSortEvent.ListHandler<Student> sortHandler = new ColumnSortEvent.ListHandler<Student>(list);
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

        Column<Student, String> emailColumn = new Column<Student, String>(new TextCell()) {
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

        Column<Student, String> firstNameColumn = new Column<Student, String>(new TextCell()) {
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

        Column<Student, String> lastNameColumn = new Column<Student, String>(new TextCell()) {
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


        cellTable.addColumn(selectColumn, selectAllHeader);
        cellTable.addColumn(emailColumn, "Email");
        cellTable.addColumn(firstNameColumn, "First Name");
        cellTable.addColumn(lastNameColumn, "Last Name");
        cellTable.setSelectionModel(selectionModel, DefaultSelectionEventManager.<Student> createCheckboxManager(cellTable.getColumnIndex(selectColumn)));

    }

    @UiHandler("resetPasswordButton")
    public void handleResetPasswordButtonClick(ClickEvent event) {
        int successCounter = 0;

                if(FieldVerifier.passwordIsNull(passwordField.getText())){
                    newPasswordLabel.setText("Password field cannot be empty.");
                }
                else{
                    newPasswordLabel.setText("");
                    successCounter++;
                }

                if(FieldVerifier.passwordIsNull(confirmPasswordField.getText())){
                    confirmPasswordLabel.setText("Confirm password field cannot be empty.");
                }
                else if(!confirmPasswordField.getText().equals(passwordField.getText())){
                    confirmPasswordLabel.setText("Password does not match.");
                }
                else{
                    confirmPasswordLabel.setText("");
                    successCounter++;
                }

                if(successCounter == 2){
                    resetPassword();

                }

            }

    @UiHandler({"passwordField", "confirmPasswordField"})
    public void handleKeyUp(KeyUpEvent event) {
        if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            resetPassword();
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
                    if(s.getEmail().toLowerCase().contains(contains.toLowerCase())) {
                        list.add(s);
                    }
                }
            }

            else if(category.equalsIgnoreCase("First Name")) {
                for(Student s : origStudentList) {
                    if(s.getFirstName().toLowerCase().contains(contains.toLowerCase())) {
                        list.add(s);
                    }
                }
            }

            else if(category.equalsIgnoreCase("Last Name")) {
                for(Student s : origStudentList) {
                    if(s.getLastName().toLowerCase().contains(contains.toLowerCase())) {
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

    private void resetPassword() {

        userService = UserService.Util.getInstance();

        final String password = passwordField.getText();
        if (listOfSelectedStudents.size() != 0){
            for(Student s : listOfSelectedStudents){
                String email = s.getEmail();

                userService.resetPassword(password, email, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        resetPasswordLabel.setText("Reset Password Unsuccessful!");
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(result) {
                            resetPasswordLabel.setText("Reset Password Successful!");
                        }
                    }
                });
            }
        }

        else{
            resetPasswordLabel.setText("Please select at least one user.");
        }

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
