package com.leanx.app.model.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Employee implements Serializable {

    private static final long serialVersionUID = 10L;

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer managerId;
    private String jobTitle;
    private String department;
    private EmploymentType employmentType;
    private EmploymentStatus employmentStatus;
    private Date hireDate;
    private Date startDate;
    private Date terminationDate;
    private String terminationReason;
    private Date retentionEndDate;
    private Timestamp createdAt;
    private Integer createdBy;
    private Integer lastUpdatedBy;
    private Timestamp lastUpdatedAt;

    public enum EmploymentType {
        FULL_TIME, PART_TIME, INTERN
    }

    public enum EmploymentStatus {
        ACTIVE, TERMINATED, RESIGNED, RETIRED, ON_LEAVE, SUSPENDED
    }

    // Default constructor
    public Employee() {}

    // Constructor with all fields except auto-generated and default values
    public Employee(String firstName, String lastName, String email, Integer managerId, String jobTitle, String department,
                    EmploymentType employmentType, EmploymentStatus employmentStatus, Date hireDate,
                    Integer createdBy) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.managerId = managerId;
        this.jobTitle = jobTitle;
        this.department = department;
        this.employmentType = employmentType;
        this.employmentStatus = employmentStatus;
        this.hireDate = hireDate;
        this.createdBy = createdBy;
    }

    public Employee(Integer id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getters
    public Integer getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public Integer getManagerId() { return managerId; }
    public String getJobTitle() { return jobTitle; }
    public String getDepartment() { return department; }
    public EmploymentType getEmploymentType() { return employmentType; }
    public EmploymentStatus getEmploymentStatus() { return employmentStatus; }
    public Date getHireDate() { return hireDate; }
    public Date getStartDate() { return startDate; }
    public Date getTerminationDate() { return terminationDate; }
    public String getTerminationReason() { return terminationReason; }
    public Date getRetentionEndDate() { return retentionEndDate; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Integer getCreatedBy() { return createdBy; }
    public Integer getLastUpdatedBy() { return lastUpdatedBy; }
    public Timestamp getLastUpdatedAt() { return lastUpdatedAt; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public void setDepartment(String department) { this.department = department; }
    public void setEmploymentType(EmploymentType employmentType) { this.employmentType = employmentType; }
    public void setEmploymentStatus(EmploymentStatus employmentStatus) { this.employmentStatus = employmentStatus; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public void setTerminationDate(Date terminationDate) { this.terminationDate = terminationDate; }
    public void setTerminationReason(String terminationReason) { this.terminationReason = terminationReason; }
    public void setRetentionEndDate(Date retentionEndDate) { this.retentionEndDate = retentionEndDate; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public void setLastUpdatedBy(Integer lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
    public void setLastUpdatedAt(Timestamp lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", managerId=" + managerId +
                ", jobTitle='" + jobTitle + '\'' +
                ", department='" + department + '\'' +
                ", employmentType=" + employmentType +
                ", employmentStatus=" + employmentStatus +
                ", hireDate=" + hireDate +
                ", startDate=" + startDate +
                ", terminationDate=" + terminationDate +
                ", terminationReason=" + terminationReason +
                ", retentionEndDate=" + retentionEndDate +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdatedAt=" + lastUpdatedAt +
                '}';
    }
}