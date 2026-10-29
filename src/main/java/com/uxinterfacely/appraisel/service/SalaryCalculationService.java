package com.uxinterfacely.appraisel.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class SalaryCalculationService {

    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal PF_LIMIT = new BigDecimal("25000");
    private static final BigDecimal PF_FIXED = new BigDecimal("3000");
    private static final BigDecimal PF_RATE = new BigDecimal("0.12");
    private static final BigDecimal PROFESSIONAL_TAX = new BigDecimal("200");

    public Map<String, BigDecimal> calculate(
            BigDecimal currentCtc,
            BigDecimal revisedCtc,
            BigDecimal variablePay,
            BigDecimal employerPf) {

        if (currentCtc == null || revisedCtc == null
                || currentCtc.signum() <= 0 || revisedCtc.signum() < 0) {
            throw new IllegalArgumentException("Invalid salary inputs");
        }

        BigDecimal increment = revisedCtc.subtract(currentCtc)
                .multiply(HUNDRED)
                .divide(currentCtc, 2, RoundingMode.HALF_UP);

        BigDecimal monthlyCtc = revisedCtc.divide(TWELVE, 2, RoundingMode.HALF_UP);
        BigDecimal basic = monthlyCtc.multiply(new BigDecimal("0.50"));
        BigDecimal hra = basic.multiply(new BigDecimal("0.40"));

        BigDecimal pf = basic.compareTo(PF_LIMIT) >= 0
                ? PF_FIXED
                : basic.multiply(PF_RATE).setScale(2, RoundingMode.HALF_UP);

        BigDecimal employeePfAmount = pf;
        BigDecimal employerPfAmount = pf;
        BigDecimal specialAllowance = monthlyCtc.subtract(basic).subtract(hra).subtract(employerPfAmount);

        // Total CTC = Basic + HRA + Special Allowance.
        BigDecimal totalCtc = basic.add(hra).add(specialAllowance);
        BigDecimal annualVariablePay = variablePay == null ? BigDecimal.ZERO : variablePay.max(BigDecimal.ZERO);
        BigDecimal totalAnnualCtc = totalCtc.multiply(TWELVE).add(annualVariablePay);
        BigDecimal grossPay = totalCtc;

        BigDecimal tds = BigDecimal.ZERO;
        // Total Deductions = Employee PF + Employer PF + Professional Tax + TDS.
        BigDecimal deductions = employeePfAmount.add(employerPfAmount).add(PROFESSIONAL_TAX).add(tds);
        BigDecimal netPay = grossPay.subtract(deductions);

        return Map.ofEntries(
                Map.entry("incrementPercentage", increment),
                Map.entry("monthlyCtc", monthlyCtc),
                Map.entry("basic", basic),
                Map.entry("hra", hra),
                Map.entry("employeePf", employeePfAmount),
                Map.entry("employerPf", employerPfAmount),
                Map.entry("specialAllowance", specialAllowance),
                Map.entry("totalCtc", totalCtc),
                Map.entry("totalAnnualCtc", totalAnnualCtc),
                Map.entry("variablePay", annualVariablePay),
                Map.entry("grossPay", grossPay),
                Map.entry("deductions", deductions),
                Map.entry("netPay", netPay),
                Map.entry("fixedCtc", revisedCtc)
        );
    }
}
