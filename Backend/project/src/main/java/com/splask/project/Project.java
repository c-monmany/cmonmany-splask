package com.splask.project;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.Date;


@Entity
class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer projectID;

    @Column
    String projectName;

    @Column
    String team;

    @Column
    String deadline;

    @Column
    String completedBy;

    @Column
    String creator;

    @Column
    String tasks;

    @Column
    Date dateCreated;

    Project() {
    }

    public Project(String projectName)
    {
        this.projectName = projectName;
        dateCreated = new Date();

    }

    public Integer getProjectID() {
        return projectID;
    }

    public String getProjectName()
    {
        return projectName;
    }


    public String getDeadline()
    {
        return deadline;
    }

    public String getCompletedBy()
    {
        return completedBy;
    }

    public String getTeam()
    {
        return team;
    }

    public String getCreator()
    {
        return creator;
    }

    public String getTasks()
    {
        return tasks;
    }
    /*
    public String getDateCreated()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        return sdf.format(dateCreated);
    }

     */
}
