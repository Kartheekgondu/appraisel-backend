package com.uxinterfacely.appraisel.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "appraisels")
public class Appraisal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeName;
    @Column(length = 254)
    private String employeeEmail;
    private String designation;
    private String department;

    @Column(precision = 15, scale = 2)
    private BigDecimal currentCtc;

    @Column(precision = 15, scale = 2)
    private BigDecimal revisedCtc;

    private LocalDate effectiveDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal variablePay;

    @Column(precision = 12, scale = 2)
    private BigDecimal employeePf;

    @Column(precision = 12, scale = 2)
    private BigDecimal employerPf;

    @Column(precision = 12, scale = 2)
    private BigDecimal professionalTax;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getCurrentCtc() {
        return currentCtc;
    }

    public void setCurrentCtc(BigDecimal currentCtc) {
        this.currentCtc = currentCtc;
    }

    public BigDecimal getRevisedCtc() {
        return revisedCtc;
    }

    public void setRevisedCtc(BigDecimal revisedCtc) {
        this.revisedCtc = revisedCtc;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public BigDecimal getVariablePay() {
        return variablePay;
    }

    public void setVariablePay(BigDecimal variablePay) {
        this.variablePay = variablePay;
    }

    public BigDecimal getEmployeePf() {
        return employeePf;
    }

    public void setEmployeePf(BigDecimal employeePf) {
        this.employeePf = employeePf;
    }

    public BigDecimal getEmployerPf() {
        return employerPf;
    }

    public void setEmployerPf(BigDecimal employerPf) {
        this.employerPf = employerPf;
    }

    public BigDecimal getProfessionalTax() {
        return professionalTax;
    }

    public void setProfessionalTax(BigDecimal professionalTax) {
        this.professionalTax = professionalTax;
    }
}