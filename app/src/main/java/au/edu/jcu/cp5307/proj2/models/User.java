package au.edu.jcu.cp5307.proj2.models;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private int record;

    public String getId() {
        return id;
    }

    public int getRecord() {
        return record;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRecord(int record) {
        this.record = record;
    }
}
