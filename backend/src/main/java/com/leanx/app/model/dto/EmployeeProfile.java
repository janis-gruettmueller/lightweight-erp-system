package com.leanx.app.model.dto;

import java.io.Serializable;
import java.util.Date;

import com.leanx.app.model.entity.Employee.EmploymentStatus;
import com.leanx.app.model.entity.Employee.EmploymentType;

/**
 * Data Transfer Object (DTO) representing the profile information of an employee.
 * This class is used to transfer employee data between different layers of the
 * application, such as the service and presentation layers. It includes basic
 * employee details along with the first and last name of their manager.
 */
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

    /**
     * Constructs an {@code EmployeeProfile} object with the specified details.
     *
     * @param id              The unique identifier of the employee.
     * @param firstName       The first name of the employee.
     * @param lastName        The last name of the employee.
     * @param email           The email address of the employee.
     * @param managerFirstName The first name of the employee's manager.
     * @param managerLastName  The last name of the employee's manager.
     * @param jobTitle        The job title of the employee.
     * @param department      The department the employee belongs to.
     * @param employmentType  The type of employment (e.g., FULL_TIME, PART_TIME).
     * @param employmentStatus The current employment status (e.g., ACTIVE, INACTIVE).
     * @param startDate       The date when the employee started their current role.
     */
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

    /**
     * Returns the unique identifier of the employee.
     *
     * @return The employee ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the employee.
     *
     * @param id The employee ID.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the first name of the employee.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the employee.
     *
     * @param firstName The first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of the employee.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the employee.
     *
     * @param lastName The last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the email address of the employee.
     *
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the employee.
     *
     * @param email The email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the first name of the employee's manager.
     *
     * @return The manager's first name.
     */
    public String getManagerFirstName() {
        return managerFirstName;
    }

    /**
     * Sets the first name of the employee's manager.
     *
     * @param managerFirstName The manager's first name.
     */
    public void setManagerFirstName(String managerFirstName) {
        this.managerFirstName = managerFirstName;
    }

    /**
     * Returns the last name of the employee's manager.
     *
     * @return The manager's last name.
     */
    public String getManagerLastName() {
        return managerLastName;
    }

    /**
     * Sets the last name of the employee's manager.
     *
     * @param managerLastName The manager's last name.
     */
    public void setManagerLastName(String managerLastName) {
        this.managerLastName = managerLastName;
    }

    /**
     * Returns the job title of the employee.
     *
     * @return The job title.
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Sets the job title of the employee.
     *
     * @param jobTitle The job title.
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**
     * Returns the department the employee belongs to.
     *
     * @return The department.
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Sets the department the employee belongs to.
     *
     * @param department The department.
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Returns the type of employment of the employee.
     *
     * @return The employment type.
     */
    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    /**
     * Sets the type of employment of the employee.
     *
     * @param employmentType The employment type.
     */
    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    /**
     * Returns the current employment status of the employee.
     *
     * @return The employment status.
     */
    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    /**
     * Sets the current employment status of the employee.
     *
     * @param employmentStatus The employment status.
     */
    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    /**
     * Returns the date when the employee started their current role.
     *
     * @return The start date.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the date when the employee started their current role.
     *
     * @param startDate The start date.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}