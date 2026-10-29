
package com.uxinterfacely.appraisel.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AppraisalResponse(
    Long id,
    String employeeName,
    String employeeEmail,
    String designation,
    String department,
    BigDecimal currentCtc,
    BigDecimal revisedCtc,
    LocalDate effectiveDate,
    BigDecimal variablePay,
    BigDecimal employeePf,
    BigDecimal employerPf,
    BigDecimal professionalTax
) {}
