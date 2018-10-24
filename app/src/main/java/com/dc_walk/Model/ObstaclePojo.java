package com.dc_walk.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "ObstacleTable")
public class ObstaclePojo {


    public String getObstacleId() {
        return obstacleId;
    }

    public void setObstacleId(String obstacleId) {
        this.obstacleId = obstacleId;
    }

    public String getObstacleName() {
        return obstacleName;
    }

    public void setObstacleName(String obstacleName) {
        this.obstacleName = obstacleName;
    }


    String obstacleId;
    String obstacleName;

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
