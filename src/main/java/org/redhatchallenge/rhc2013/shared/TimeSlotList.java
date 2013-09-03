package org.redhatchallenge.rhc2013.shared;

import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class TimeSlotList implements Serializable {

    private int timeslot_id;
    private long timeslot;

    public int getTimeslot_id() {
        return timeslot_id;
    }

    public void setTimeslot_id(int timeslot_id) {
        this.timeslot_id = timeslot_id;
    }

    public long getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(long timeslot) {
        this.timeslot = timeslot;
    }
}
