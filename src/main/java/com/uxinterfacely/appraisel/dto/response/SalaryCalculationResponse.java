
package com.uxinterfacely.appraisel.dto.response;

import java.math.BigDecimal;

public record SalaryCalculationResponse(
    BigDecimal incrementPercentage,
    BigDecimal basic,
    BigDecimal hra,
    BigDecimal specialAllowance,
    BigDecimal monthlyGross,
    BigDecimal monthlyNet
) {}
