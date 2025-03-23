package com.leanx.app.model.dto;

import java.io.Serializable;
import java.util.Date;

import com.leanx.app.model.entity.Employee.EmploymentStatus;
import com.leanx.app.model.entity.Employee.EmploymentType;

public class EmployeeProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String managerFirstName;
    private String managerLastName;
    private String jobTitle;
    private String department;
    private EmploymentType employmentType;
    private EmploymentStatus employmentStatus;
    private Date startDate;

    public EmployeeProfile(Integer id, String firstName, String lastName, String email, String managerFirstName, String managerLastName, 
                            String jobTitle, String department, EmploymentType employmentType,  EmploymentStatus employmentStatus, Date startDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.managerFirstName = managerFirstName;
        this.managerLastName = managerLastName;
        this.jobTitle = jobTitle;
        this.department = department;
        this.employmentType = employmentType;
        this.employmentStatus = employmentStatus;
        this.startDate = startDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getManagerFirstName() {
        return managerFirstName;
    }

    public void setManagerFirstName(String managerFirstName) {
        this.managerFirstName = managerFirstName;
    }

    public String getManagerLastName() {
        return managerLastName;
    }

    public void setManagerLastName(String managerLastName) {
        this.managerLastName = managerLastName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}