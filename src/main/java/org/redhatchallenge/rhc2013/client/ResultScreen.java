package org.redhatchallenge.rhc2013.client;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
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
 * Date: 13/9/13
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */

public class ResultScreen extends Composite {
    interface ResultScreenUiBinder extends UiBinder<Widget, ResultScreen>{
    }

    private static ResultScreenUiBinder UiBinder = GWT.create(ResultScreenUiBinder.class);

    @UiField CellTable<Student> resultCellTable;
    @UiField MySimplePager pager;
    @UiField Label resultLabel;
    @UiField Button refreshButton;
    @UiField Button exportButton;
    @UiField TextBox searchField;
    @UiField Button searchButton;
    @UiField Button resetButton;
    @UiField Button currentTakerButton;
    @UiField Label currentTakerLabel;
    @UiField ListBox viewResultList;
    @UiField Label filterLabel;

    private UserServiceAsync userService = UserService.Util.getInstance();
    private ListDataProvider<Student> provider;
    private List<Student> studentList;
    private List<Student> listOfOriginStudents;
    private List<Student> list = new ArrayList<Student>();
    private List<Student> listOfSelectedStudents = new ArrayList<Student>();
    private List<Student> currentTakerList = new ArrayList<Student>();

    private static final ProvidesKey<Student> KEY_PROVIDER = new ProvidesKey<Student>() {
        @Override
        public Object getKey(Student item) {
            return item.getEmail();
        }
    };

    public ResultScreen() {
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
                for (Student s : studentList) {
                    if (s.getStartTime() != null){
                        if (s.getEndTime() == null){
                                currentTakerList.add(s);
                        }
                    }
                    list.add(s);
                }

                provider = new ListDataProvider<Student>(studentList);
                provider.addDataDisplay(resultCellTable);

                initResultCellTable();
                currentTakerLabel.setText("Number of contestants currently taking the challenge: " + currentTakerList.size());

            }
        });

        pager.setDisplay(resultCellTable);
        pager.setPageSize(8);
        resultLabel.setVisible(false);
        viewResultList.insertItem("All", 0);
        viewResultList.insertItem("Bronze", 1);
        viewResultList.insertItem("Silver", 2);
        viewResultList.insertItem("Gold", 3);
        filterLabel.setText("Filter by: ");

    }

    private void initResultCellTable() {
        List list = provider.getList();
        final MultiSelectionModel<Student> selectionModel = new MultiSelectionModel<Student>(KEY_PROVIDER);

        ColumnSortEvent.ListHandler<Student> sortHandler = new ColumnSortEvent.ListHandler<Student>(list);
        resultCellTable.addColumnSortHandler(sortHandler);

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
                for (Student student : resultCellTable.getVisibleItems()){
                    if (!selectionModel.isSelected(student)){
                        return false;
                    }
                }
                return resultCellTable.getVisibleItems().size() > 0;
            }
        };

        selectAllHeader.setUpdater(new ValueUpdater<Boolean>() {
            @Override
            public void update(Boolean aBoolean) {
                for(Student student : resultCellTable.getVisibleItems()){
                    selectionModel.setSelected(student, aBoolean);
                }
                if (aBoolean == true){
                    for (int i=0;i<resultCellTable.getVisibleItemCount(); i++){
                        if (!listOfSelectedStudents.contains(resultCellTable.getVisibleItem(i)))
                            listOfSelectedStudents.add(resultCellTable.getVisibleItem(i));
                    }
                }
                else if (aBoolean == false){
                    for (int i=0;i<resultCellTable.getVisibleItemCount(); i++){
                        listOfSelectedStudents.remove(resultCellTable.getVisibleItem(i));
                    }
                }
            }

        });//End of checkbox

        //Email Column
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
        //End of Email

        //Start of First Name
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
        //End of First Name


        //Tiimeslot Column
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
        //End of Timeslot

        //score column
        Column<Student, String> scoreColumn = new Column<Student, String>(new TextCell()) {
            @Override
            public String getValue(Student student) {
                return String.valueOf(student.getScore());
            }
        };

        scoreColumn.setSortable(true);
        sortHandler.setComparator(scoreColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? String.valueOf(o1.getScore()).compareTo(String.valueOf(o2.getScore())) : 1;
                }
                return -1;
            }
        });
        //End of score

        //startTime column
        Column<Student, String> startTimeColumn = new Column<Student, String>(new TextCell()) {
            @Override
            public String getValue(Student student) {
                return String.valueOf(student.getStartTime());
            }
        };

        startTimeColumn.setSortable(true);
        sortHandler.setComparator(startTimeColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? String.valueOf(o1.getStartTime()).compareTo(String.valueOf(o2.getStartTime())) : 1;
                }
                return -1;
            }
        });

        //EndTime column
        Column<Student, String> EndTimeColumn = new Column<Student, String>(new TextCell()) {
            @Override
            public String getValue(Student student) {
                return String.valueOf(student.getEndTime());
            }
        };

        EndTimeColumn.setSortable(true);
        sortHandler.setComparator(EndTimeColumn, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ? String.valueOf(o1.getEndTime()).compareTo(String.valueOf(o2.getEndTime())) : 1;
                }
                return -1;
            }
        });
        //End of Endtime.

        resultCellTable.addColumn(selectColumn, selectAllHeader);
        resultCellTable.addColumn(emailColumn, "Email");
        resultCellTable.addColumn(firstNameColumn, "Name");
        resultCellTable.addColumn(timeSlotColumn, "Time Slot");
        resultCellTable.addColumn(scoreColumn, "Score");
        resultCellTable.addColumn(startTimeColumn, "Start Time");
        resultCellTable.addColumn(EndTimeColumn, "End Time");

        resultCellTable.setSelectionModel(selectionModel, DefaultSelectionEventManager.<Student>createCheckboxManager(resultCellTable.getColumnIndex(selectColumn)));
    }

    @UiHandler("refreshButton")
    public void handleRefreshButton(ClickEvent event) {
//        Timer timer = new Timer() {
//            @Override
//            public void run() {
//                ContentContainer.INSTANCE.setContent(new ResultScreen());
//            }
//        };
//        timer.scheduleRepeating(1000*60); //1 minute
          ContentContainer.INSTANCE.setContent(new ResultScreen());

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
    @UiHandler("searchField")
    public void handleSearchField(ClickEvent event) {
        searchField.setFocus(true);
        searchField.selectAll();
    }

    @UiHandler("searchButton")
    public void handleSearchButton(ClickEvent event) {
        String criteria = searchField.getText();

        List<Student> list = new ArrayList<Student>();

        if(criteria.equals("")){
            for (Student s : listOfOriginStudents){
                list.add(s);
            }
            provider.getList().clear();
            provider.getList().addAll(list);
        }
        else {
            for (Student s : listOfOriginStudents){
                if (s.getEmail().toLowerCase().contains(criteria.toLowerCase())){
                    list.add(s);
                }

                if (s.getFirstName().toLowerCase().contains(criteria.toLowerCase())){
                    list.add(s);
                }
            }
            provider.getList().clear();
            provider.getList().addAll(list);
        }
    }

    @UiHandler("resetButton")
    public void handleResetButton(ClickEvent event) {
        List<Student> listToChange = listOfSelectedStudents;
        if (listToChange.size() != 0){
            for (Student s : listToChange) {
                userService.resetTestDetails(s, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        resultLabel.setText("Reset Unsuccessful.");
                        resultLabel.setVisible(true);
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        resultLabel.setText("Reset Successful" + listOfSelectedStudents.size());
                        resultLabel.setVisible(true);
                    }
                });
            }
            ContentContainer.INSTANCE.setContent(new ResultScreen());
        }
        else {
            resultLabel.setText("Please select atleast 1 contestant before proceeding.");
            resultLabel.setVisible(true);
        }
    }

    @UiHandler("currentTakerButton")
    public void handleCurrentTakerButton(ClickEvent event){
        provider.getList().clear();
        provider.getList().addAll(currentTakerList);
    }

    @UiHandler("viewResultList")
    public void handleViewScoreList(ChangeEvent event){

        int selectedItem = viewResultList.getSelectedIndex();

        int GOLD = 100;
        int SILVER = 75;
        int BRONZE = 50;

        List<Student> resultList = new ArrayList<Student>();

        if (selectedItem == 0){
            for (Student s : listOfOriginStudents){
                if (s.getScore() >= 0){
                    resultList.add(s);
                }
            }
        }

        else if (selectedItem == 1){
            for (Student s : listOfOriginStudents){
                if (s.getScore() >= BRONZE)
                    if (s.getScore() < SILVER){
                        resultList.add(s);
                    }
            }
        }

        else if (selectedItem == 2){
            for (Student s : listOfOriginStudents){
                if (s.getScore() >= SILVER){
                    if (s.getScore() < GOLD){
                        resultList.add(s);
                    }
                }
            }
        }

        else if (selectedItem == 3){
            for (Student s : listOfOriginStudents){
                if (s.getScore() >= GOLD){
                    resultList.add(s);
                }
            }
        }
        provider.getList().clear();
        provider.getList().addAll(resultList);
    }
}
