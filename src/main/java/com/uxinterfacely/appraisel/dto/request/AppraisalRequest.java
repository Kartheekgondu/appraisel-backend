
package com.uxinterfacely.appraisel.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AppraisalRequest(
    @NotBlank
    @Size(min = 2, max = 60)
    @Pattern(regexp = "^[A-Za-z]{2,}(?: [A-Za-z]+)*$", message = "Employee Name must contain English letters and spaces")
    String employeeName,
    @NotBlank @Size(min = 2, max = 60)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9]{2,}$", message = "Employee ID must contain uppercase letters and numbers only, including at least one of each")
    String employeeId,
    @NotBlank @Email @Size(min = 2, max = 100)
    @Pattern(regexp = "^[\\s\\S]{2,}$", message = "Must contain at least 2 characters")
    String employeeEmail,
    @NotBlank @Size(min = 2, max = 60)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*(?: [A-Z][a-zA-Z]*)*$", message = "Designation must contain alphabets only and each word must start with a capital letter")
    String designation,
    @NotBlank @Size(min = 2, max = 60)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*(?: [A-Z][a-zA-Z]*)*$", message = "Department must contain alphabets only and each word must start with a capital letter")
    String department,

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
) {
    @AssertTrue(message = "Effective date must be within the current calendar year")
    public boolean isEffectiveDateInCurrentYear() {
        return effectiveDate == null || effectiveDate.getYear() == LocalDate.now().getYear();
    }

    @AssertTrue(message = "Revised Annual CTC must be greater than Current Annual CTC")
    public boolean isRevisedCtcGreaterThanCurrentCtc() {
        return currentCtc == null || revisedCtc == null || revisedCtc.compareTo(currentCtc) > 0;
    }
}
