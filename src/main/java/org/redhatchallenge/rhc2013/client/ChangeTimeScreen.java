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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.redhatchallenge.rhc2013.shared.Student;
import org.redhatchallenge.rhc2013.shared.TimeSlotList;

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
    @UiField Label testing;

    private UserServiceAsync userService = UserService.Util.getInstance();
    private List<Student> studentList;
    private ListDataProvider<Student> provider;
    private List<Student> timeslotListOfStudent = new ArrayList<Student>();
    private List<Student> list = new ArrayList<Student>();
    private List<TimeSlotList> ListofTimeSlot;
    private List<String> dateList = new ArrayList<String>();
    private List<Student> listOfOriginStudents;

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
                listOfOriginStudents = new ArrayList<Student>(students);
                studentList = students;
                for (Student s : studentList){
                    list.add(s);
                }

                provider = new ListDataProvider<Student>(studentList);
                provider.addDataDisplay(readOnlyCellTable);

                initReadOnlyCellTable();
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
                listOfTiming.insertItem("Select All", 0);
                listOfTiming.insertItem("Without Time Slot", 1);
                for(int i = 0; i < dateList.size(); i++){
                    listOfTiming.insertItem(dateList.get(i).toString(),i+2);
                }
            }
        });
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

        String timing = listOfTiming.getItemText(listOfTiming.getSelectedIndex());

        if (timing.equals("Select All")){
            provider.getList().clear();
            for (Student s : listOfOriginStudents){
                provider.getList().add(s);
            }
            testing.setText("select all" + listOfOriginStudents);
        }

        else if (timing.equals("Without Time Slot")){
            timeslotListOfStudent.clear();
            for (Student s : listOfOriginStudents){
                if (s.getTimeslot() == 0){
                    timeslotListOfStudent.add(s);
                }
            }
            provider.getList().clear();

            for (Student s : timeslotListOfStudent){
                provider.getList().add(s);
            }
            testing.setText("without" + timeslotListOfStudent);
        }

        else {
            timeslotListOfStudent.clear();
            for (Student s :listOfOriginStudents){
                Date convertlongtodate = new Date(s.getTimeslot());
                String date = returnLongDateTime(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(convertlongtodate));

                if (date.equals(timing)){
                    timeslotListOfStudent.add(s);
                }
            }
            provider.getList().clear();
            for (Student s : timeslotListOfStudent){
                provider.getList().add(s);
            }
            testing.setText("DB" + timeslotListOfStudent);
        }
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
