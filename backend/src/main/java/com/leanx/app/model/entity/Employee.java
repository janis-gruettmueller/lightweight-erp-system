package com.leanx.app.model.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Entity representing an employee within the LeanX application.
 * Contains details such as personal information, job specifics, employment status,
 * and audit timestamps.
 */
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

    /**
     * Enumeration defining the different types of employment.
     */
    public enum EmploymentType {
        FULL_TIME, PART_TIME, INTERN
    }

    /**
     * Enumeration defining the various statuses an employee can have.
     */
    public enum EmploymentStatus {
        ACTIVE, TERMINATED, RESIGNED, RETIRED, ON_LEAVE, SUSPENDED
    }

    /**
     * Default constructor for the Employee class.
     */
    public Employee() {}

    /**
     * Constructor for creating an Employee with essential details.
     * Excludes auto-generated fields and those with default values.
     *
     * @param firstName        The first name of the employee.
     * @param lastName         The last name of the employee.
     * @param email            The email address of the employee.
     * @param managerId        The ID of the employee's manager.
     * @param jobTitle         The job title of the employee.
     * @param department       The department the employee belongs to.
     * @param employmentType   The type of employment (e.g., FULL_TIME).
     * @param employmentStatus The current employment status (e.g., ACTIVE).
     * @param hireDate         The date the employee was hired.
     * @param createdBy        The ID of the user who created this employee record.
     */
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

    /**
     * Constructor for creating a basic Employee object with ID, name, and email.
     *
     * @param id        The unique identifier of the employee.
     * @param firstName The first name of the employee.
     * @param lastName  The last name of the employee.
     * @param email     The email address of the employee.
     */
    public Employee(Integer id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Gets the unique identifier of the employee.
     *
     * @return The employee ID.
     */
    public Integer getId() { return id; }

    /**
     * Gets the first name of the employee.
     *
     * @return The first name.
     */
    public String getFirstName() { return firstName; }

    /**
     * Gets the last name of the employee.
     *
     * @return The last name.
     */
    public String getLastName() { return lastName; }

    /**
     * Gets the email address of the employee.
     *
     * @return The email address.
     */
    public String getEmail() { return email; }

    /**
     * Gets the ID of the employee's manager.
     *
     * @return The manager's ID.
     */
    public Integer getManagerId() { return managerId; }

    /**
     * Gets the job title of the employee.
     *
     * @return The job title.
     */
    public String getJobTitle() { return jobTitle; }

    /**
     * Gets the department the employee belongs to.
     *
     * @return The department.
     */
    public String getDepartment() { return department; }

    /**
     * Gets the type of employment of the employee.
     *
     * @return The employment type.
     */
    public EmploymentType getEmploymentType() { return employmentType; }

    /**
     * Gets the current employment status of the employee.
     *
     * @return The employment status.
     */
    public EmploymentStatus getEmploymentStatus() { return employmentStatus; }

    /**
     * Gets the date the employee was hired.
     *
     * @return The hire date.
     */
    public Date getHireDate() { return hireDate; }

    /**
     * Gets the start date of the employee's current role.
     *
     * @return The start date.
     */
    public Date getStartDate() { return startDate; }

    /**
     * Gets the date the employee's employment was terminated.
     *
     * @return The termination date.
     */
    public Date getTerminationDate() { return terminationDate; }

    /**
     * Gets the reason for the employee's termination.
     *
     * @return The termination reason.
     */
    public String getTerminationReason() { return terminationReason; }

    /**
     * Gets the date until which the employee's data should be retained.
     *
     * @return The retention end date.
     */
    public Date getRetentionEndDate() { return retentionEndDate; }

    /**
     * Gets the timestamp when this employee record was created.
     *
     * @return The creation timestamp.
     */
    public Timestamp getCreatedAt() { return createdAt; }

    /**
     * Gets the ID of the user who created this employee record.
     *
     * @return The ID of the creating user.
     */
    public Integer getCreatedBy() { return createdBy; }

    /**
     * Gets the ID of the user who last updated this employee record.
     *
     * @return The ID of the last updating user.
     */
    public Integer getLastUpdatedBy() { return lastUpdatedBy; }

    /**
     * Gets the timestamp when this employee record was last updated.
     *
     * @return The last updated timestamp.
     */
    public Timestamp getLastUpdatedAt() { return lastUpdatedAt; }

    /**
     * Sets the unique identifier of the employee.
     *
     * @param id The employee ID.
     */
    public void setId(Integer id) { this.id = id; }

    /**
     * Sets the first name of the employee.
     *
     * @param firstName The first name.
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * Sets the last name of the employee.
     *
     * @param lastName The last name.
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Sets the email address of the employee.
     *
     * @param email The email address.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Sets the ID of the employee's manager.
     *
     * @param managerId The manager's ID.
     */
    public void setManagerId(Integer managerId) { this.managerId = managerId; }

    /**
     * Sets the job title of the employee.
     *
     * @param jobTitle The job title.
     */
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    /**
     * Sets the department the employee belongs to.
     *
     * @param department The department.
     */
    public void setDepartment(String department) { this.department = department; }

    /**
     * Sets the type of employment of the employee.
     *
     * @param employmentType The employment type.
     */
    public void setEmploymentType(EmploymentType employmentType) { this.employmentType = employmentType; }

    /**
     * Sets the current employment status of the employee.
     *
     * @param employmentStatus The employment status.
     */
    public void setEmploymentStatus(EmploymentStatus employmentStatus) { this.employmentStatus = employmentStatus; }

    /**
     * Sets the date the employee was hired.
     *
     * @param hireDate The hire date.
     */
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }

    /**
     * Sets the start date of the employee's current role.
     *
     * @param startDate The start date.
     */
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    /**
     * Sets the date the employee's employment was terminated.
     *
     * @param terminationDate The termination date.
     */
    public void setTerminationDate(Date terminationDate) { this.terminationDate = terminationDate; }

    /**
     * Sets the reason for the employee's termination.
     *
     * @param terminationReason The termination reason.
     */
    public void setTerminationReason(String terminationReason) { this.terminationReason = terminationReason; }

    /**
     * Sets the date until which the employee's data should be retained.
     *
     * @param retentionEndDate The retention end date.
     */
    public void setRetentionEndDate(Date retentionEndDate) { this.retentionEndDate = retentionEndDate; }

    /**
     * Sets the timestamp when this employee record was created.
     *
     * @param createdAt The creation timestamp.
     */
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /**
     * Sets the ID of the user who created this employee record.
     *
     * @param createdBy The ID of the creating user.
     */
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    /**
     * Sets the ID of the user who last updated this employee record.
     *
     * @param lastUpdatedBy The ID of the last updating user.
     */
    public void setLastUpdatedBy(Integer lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }

    /**
     * Sets the timestamp when this employee record was last updated.
     *
     * @param lastUpdatedAt The last updated timestamp.
     */
    public void setLastUpdatedAt(Timestamp lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    /**
     * Returns a string representation of the Employee object.
     *
     * @return A string containing the values of all fields of the Employee.
     */
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
                ", terminationReason='" + terminationReason + '\'' +
                ", retentionEndDate=" + retentionEndDate +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdatedAt=" + lastUpdatedAt +
                '}';
    }
}