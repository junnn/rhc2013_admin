package org.redhatchallenge.rhc2013.client;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.redhatchallenge.rhc2013.shared.Student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jun
 * Date: 3/9/13
 * Time: 12:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChangeTimeScreen extends Composite {
    interface ChangeTimeScreenUiBinder extends UiBinder<Widget, ChangeTimeScreen> {
    }

    private static ChangeTimeScreenUiBinder UiBinder = GWT.create(ChangeTimeScreenUiBinder.class);

    @UiField CellTable<Student> readOnlyCellTable;
    @UiField ListBox listOfTiming;
    @UiField MySimplePager pager;

    private UserServiceAsync userService = UserService.Util.getInstance();
    private List<Student> studentList;
    private List<Student> origStudentList;
    private ListDataProvider<Student> provider;
    private List<Student> timeslotListOfStudent = new ArrayList<Student>();
    private List<Student> list = new ArrayList<Student>();

    private static final ProvidesKey<Student> KEY_PROVIDER = new ProvidesKey<Student>() {
        @Override
        public Object getKey(Student item) {
            return item.getEmail();
        }
    };

    public ChangeTimeScreen() {
        initWidget(UiBinder.createAndBindUi(this));

        userService.getListOfStudents(new AsyncCallback<List<Student>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Student> students) {
                studentList = students;
                for (Student s : studentList){
                    list.add(s);
                }

                provider = new ListDataProvider<Student>(studentList);
                provider.addDataDisplay(readOnlyCellTable);

                initReadOnlyCellTable();
            }
        });

        listOfTiming.insertItem("Select All", 0);
        listOfTiming.insertItem("Without Time Slot", 1);

        pager.setDisplay(readOnlyCellTable);
        pager.setPageSize(8);
    }

    private void initReadOnlyCellTable(){
        List list = provider.getList();

        ColumnSortEvent.ListHandler<Student> sortHandler = new ColumnSortEvent.ListHandler<Student>(list);
        readOnlyCellTable.addColumnSortHandler(sortHandler);

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

        readOnlyCellTable.addColumn(emailColumn, "Email");
        readOnlyCellTable.addColumn(timeSlotColumn, "Time Slot");
    }

    @UiHandler("listOfTiming")
    public void timingChangeHandler(ChangeEvent event){
        switch (listOfTiming.getSelectedIndex()){
            case 0:
                provider.setList(studentList);
                break;
            case 1:
                for (Student s : studentList){
                    if (s.getTimeslot() == 0){
                        timeslotListOfStudent.add(s);
                    }
                }
                provider.setList(timeslotListOfStudent);
                break;
        }
    }
}
