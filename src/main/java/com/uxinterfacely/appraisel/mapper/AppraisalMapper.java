
package com.uxinterfacely.appraisel.mapper;

import com.uxinterfacely.appraisel.dto.request.AppraisalRequest;
import com.uxinterfacely.appraisel.dto.response.AppraisalResponse;
import com.uxinterfacely.appraisel.entity.Appraisal;
import org.springframework.stereotype.Component;

@Component
public class AppraisalMapper {

    public Appraisal toEntity(AppraisalRequest request) {
        Appraisal appraisal = new Appraisal();

        appraisal.setEmployeeName(request.employeeName());
        appraisal.setEmployeeEmail(request.employeeEmail());
        appraisal.setDesignation(request.designation());
        appraisal.setDepartment(request.department());
        appraisal.setCurrentCtc(request.currentCtc());
        appraisal.setRevisedCtc(request.revisedCtc());
        appraisal.setEffectiveDate(request.effectiveDate());
        appraisal.setVariablePay(request.variablePay());
        appraisal.setEmployeePf(request.employeePf());
        appraisal.setEmployerPf(request.employerPf());
        appraisal.setProfessionalTax(request.professionalTax());

        return appraisal;
    }

    public AppraisalResponse toResponse(Appraisal appraisal) {
        return new AppraisalResponse(
            appraisal.getId(),
            appraisal.getEmployeeName(),
            appraisal.getEmployeeEmail(),
            appraisal.getDesignation(),
            appraisal.getDepartment(),
            appraisal.getCurrentCtc(),
            appraisal.getRevisedCtc(),
            appraisal.getEffectiveDate(),
            appraisal.getVariablePay(),
            appraisal.getEmployeePf(),
            appraisal.getEmployerPf(),
            appraisal.getProfessionalTax()
        );
    }
}
