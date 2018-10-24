package com.dc_walk.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "ParameterDropTable")
public class Para_DropDown {

    String paraId;
    String paraDropId;

    public String getParaId() {
        return paraId;
    }

    public void setParaId(String paraId) {
        this.paraId = paraId;
    }

    public String getParaDropId() {
        return paraDropId;
    }

    public void setParaDropId(String paraDropId) {
        this.paraDropId = paraDropId;
    }

    public String getParaDropName() {
        return paraDropName;
    }

    public void setParaDropName(String paraDropName) {
        this.paraDropName = paraDropName;
    }

    String paraDropName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;
}
