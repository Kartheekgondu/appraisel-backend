
package com.uxinterfacely.appraisel.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AppraisalRequest(
    @NotBlank
    @Size(min = 2, max = 60)
    @Pattern(regexp = "^(?!([A-Za-z])\\1+$)[A-Za-z]{2,}(?: [A-Za-z]+)*$", message = "Employee Name must start with an English letter and contain only one space between words")
    String employeeName,
    @NotBlank @Size(min = 2, max = 60)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{2,}$", message = "Employee ID must contain at least one letter and one number; only letters and numbers are allowed")
    String employeeId,
    @NotBlank @Email @Size(min = 2, max = 100)
    @Pattern(regexp = "^(?!([\\s\\S])\\1+$)[\\s\\S]{2,}$", message = "Must contain at least 2 characters and cannot consist of one repeated character")
    String employeeEmail,
    @NotBlank @Size(min = 2, max = 60)
    @Pattern(regexp = "^(?!.*([A-Za-z])\\1{2})(?!([A-Za-z])\\2+$)[A-Z][a-zA-Z]*(?: [A-Z][a-zA-Z]*)*$", message = "Designation must contain alphabets only and each word must start with a capital letter")
    String designation,
    @NotBlank @Size(min = 2, max = 60)
    @Pattern(regexp = "^(?!.*([A-Za-z])\\1{2})(?!([A-Za-z])\\2+$)[A-Z][a-zA-Z]*(?: [A-Z][a-zA-Z]*)*$", message = "Department must contain alphabets only and each word must start with a capital letter")
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
}
