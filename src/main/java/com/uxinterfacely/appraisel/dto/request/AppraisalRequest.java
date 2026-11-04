
package com.uxinterfacely.appraisel.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AppraisalRequest(
    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "Employee Name must start with an English letter and contain only one space between words")
    String employeeName,
    @NotBlank String employeeId,
    @NotBlank @Email String employeeEmail,
    @NotBlank String designation,
    @NotBlank String department,

    @NotNull @DecimalMin("0.01")
    BigDecimal currentCtc,

    @NotNull @DecimalMin("0.00")
    BigDecimal revisedCtc,

    @NotNull LocalDate effectiveDate,

    @NotNull @DecimalMin("0.00")
    BigDecimal variablePay,

    @NotNull @DecimalMin("0.00")
    BigDecimal employeePf,

    @NotNull @DecimalMin("0.00")
    BigDecimal employerPf,

    @NotNull @DecimalMin("0.00")
    BigDecimal professionalTax
) {}
