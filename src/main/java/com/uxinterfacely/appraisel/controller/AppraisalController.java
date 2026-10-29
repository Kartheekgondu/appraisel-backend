package com.uxinterfacely.appraisel.controller;

import com.uxinterfacely.appraisel.dto.request.AppraisalRequest;
import com.uxinterfacely.appraisel.dto.response.AppraisalResponse;
import com.uxinterfacely.appraisel.entity.Appraisal;
import com.uxinterfacely.appraisel.mapper.AppraisalMapper;
import com.uxinterfacely.appraisel.service.AppraisalService;
import com.uxinterfacely.appraisel.service.PdfGenerationService;
import com.uxinterfacely.appraisel.service.SalaryCalculationService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appraisels")
public class AppraisalController {

    private final AppraisalService appraisalService;
    private final AppraisalMapper appraisalMapper;
    private final SalaryCalculationService salaryCalculationService;
    private final PdfGenerationService pdfGenerationService;
    private final com.uxinterfacely.appraisel.service.AppraisalEmailService appraisalEmailService;

    public AppraisalController(AppraisalService appraisalService,
                               AppraisalMapper appraisalMapper,
                               SalaryCalculationService salaryCalculationService,
                               PdfGenerationService pdfGenerationService,
                               com.uxinterfacely.appraisel.service.AppraisalEmailService appraisalEmailService) {
        this.appraisalService = appraisalService;
        this.appraisalMapper = appraisalMapper;
        this.salaryCalculationService = salaryCalculationService;
        this.pdfGenerationService = pdfGenerationService;
        this.appraisalEmailService = appraisalEmailService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "message", "Appraisal backend is running");
    }

    @PostMapping("/calculate")
    public Map<String, BigDecimal> calculate(@RequestBody @Valid AppraisalRequest request) {
        return salaryCalculationService.calculate(
                request.currentCtc(), request.revisedCtc(),
                request.variablePay(), request.employerPf());
    }

    @PostMapping
    public ResponseEntity<AppraisalResponse> create(@Valid @RequestBody AppraisalRequest request) {
        Appraisal saved = appraisalService.save(appraisalMapper.toEntity(request));
        return ResponseEntity.status(201).body(appraisalMapper.toResponse(saved));
    }

    @GetMapping
    public List<AppraisalResponse> getAll() {
        return appraisalService.getAll().stream().map(appraisalMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public AppraisalResponse getById(@PathVariable Long id) {
        return appraisalMapper.toResponse(appraisalService.getById(id));
    }

    @PostMapping("/{id}/email")
    public ResponseEntity<Map<String, String>> sendEmail(@PathVariable Long id) {
        Appraisal appraisal = appraisalService.getById(id);
        appraisalEmailService.sendAppraisal(appraisal);
        return ResponseEntity.ok(Map.of(
                "status", "sent",
                "message", "Appraisal PDF sent to " + appraisal.getEmployeeEmail() + "."
        ));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Appraisal appraisal = appraisalService.getById(id);
        byte[] pdf = pdfGenerationService.generatePdf(appraisal);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("appraisal-" + id + ".pdf").build().toString())
                .body(pdf);
    }
}
