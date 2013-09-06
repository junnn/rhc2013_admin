package org.redhatchallenge.rhc2013.client;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import org.redhatchallenge.rhc2013.shared.Student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jun
 * Date: 6/9/13
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class DeletedFlagUser extends Composite{
    interface DeletedFlagUserUiBinder extends UiBinder<Widget, DeletedFlagUser> {
    }

    private static DeletedFlagUserUiBinder UiBinder = GWT.create(DeletedFlagUserUiBinder.class);

    @UiField CellTable<Student> deletedUserCellTable;
    @UiField Button restoreButton;
    @UiField MySimplePager pager;
    @UiField Label resultLabel;

    private UserServiceAsync userService = UserService.Util.getInstance();
    private ListDataProvider<Student> provider;
    private List<Student> listOfOriginStudents;
    private List<Student> studentList;
    private List<Student> list = new ArrayList<Student>();
    private List<Student> listOfSelectedStudents = new ArrayList<Student>();

    private static final ProvidesKey<Student> KEY_PROVIDER = new ProvidesKey<Student>() {
        @Override
        public Object getKey(Student item) {
            return item.getEmail();
        }
    };

    public DeletedFlagUser(){
        initWidget(UiBinder.createAndBindUi(this));

        userService.getListOfDeletedStudents(new AsyncCallback<List<Student>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Student> students) {
                listOfOriginStudents = new ArrayList<Student>(students);
                studentList = students;
                for (Student s : studentList) {
                    list.add(s);
                }

                provider = new ListDataProvider<Student>(studentList);
                provider.addDataDisplay(deletedUserCellTable);

                initReadOnlyCellTable();
                resultLabel.setText("Successfuly Restored.");
            }
        });
        pager.setDisplay(deletedUserCellTable);
        pager.setPageSize(8);
        resultLabel.setVisible(false);
    }


    private void initReadOnlyCellTable(){
        List list = provider.getList();
        final MultiSelectionModel<Student> selectionModel = new MultiSelectionModel<Student>(KEY_PROVIDER);

        ColumnSortEvent.ListHandler<Student> sortHandler = new ColumnSortEvent.ListHandler<Student>(list);
        deletedUserCellTable.addColumnSortHandler(sortHandler);

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
        com.google.gwt.user.cellview.client.Header<Boolean> selectAllHeader = new com.google.gwt.user.cellview.client.Header<Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue() {
                for (Student student : deletedUserCellTable.getVisibleItems()){
                    if (!selectionModel.isSelected(student)){
                        return false;
                    }
                }
                return deletedUserCellTable.getVisibleItems().size() > 0;
            }
        };

        selectAllHeader.setUpdater(new ValueUpdater<Boolean>() {
            @Override
            public void update(Boolean aBoolean) {
                for(Student student : deletedUserCellTable.getVisibleItems()){
                    selectionModel.setSelected(student, aBoolean);
                }
                if (aBoolean == true){
                    for (int i=0;i<deletedUserCellTable.getVisibleItemCount(); i++){
                        if (!listOfSelectedStudents.contains(deletedUserCellTable.getVisibleItem(i)))
                            listOfSelectedStudents.add(deletedUserCellTable.getVisibleItem(i));
                    }
                }
                else if (aBoolean == false){
                    for (int i=0;i<deletedUserCellTable.getVisibleItemCount(); i++){
                        listOfSelectedStudents.remove(deletedUserCellTable.getVisibleItem(i));
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
        deletedUserCellTable.addColumn(selectColumn, selectAllHeader);
        deletedUserCellTable.addColumn(emailColumn, "Email");
        deletedUserCellTable.addColumn(timeSlotColumn, "Time Slot");

        deletedUserCellTable.setSelectionModel(selectionModel, DefaultSelectionEventManager.<Student> createCheckboxManager(deletedUserCellTable.getColumnIndex(selectColumn)));

    }

    @UiHandler("restoreButton")
    public void restoreButtonClickHandler(ClickEvent event){

        for (Student s : listOfSelectedStudents){
            s.setStatus(Boolean.TRUE);
            userService.updateStudentData(s, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void onSuccess(Boolean aBoolean) {
                    resultLabel.setVisible(true);

                    studentList.removeAll(listOfSelectedStudents);
                    provider.setList(studentList);
                    listOfSelectedStudents.clear();
                }
            });
        }
    }
}


