
package com.uxinterfacely.appraisel.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.uxinterfacely.appraisel.entity.Appraisal;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfGenerationService {

    private static final Color INK = new Color(26, 26, 26);
    private static final Color BLUE = new Color(54, 91, 141);
    private static final Color FOOTER_BLUE = new Color(54, 91, 141);
    private static final Color GRID = new Color(184, 188, 196);
    private static final Color HEADER_BG = new Color(226, 226, 226);

    private final NumberFormat money =
            NumberFormat.getNumberInstance(new Locale("en", "IN"));

    public byte[] generatePdf(Appraisal a) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Document document = new Document(
                PageSize.A4, 36, 36, 38, 62
        );

        try {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            writer.setPageEvent(new GstFooterEvent());

            document.open();

            Font body = new Font(Font.HELVETICA, 9.5f, Font.NORMAL, INK);
            Font bold = new Font(Font.HELVETICA, 9.5f, Font.BOLD, INK);
            Font title = new Font(Font.HELVETICA, 11f, Font.BOLD, BLUE);
            Font tableHead = new Font(
                    Font.HELVETICA, 9.5f, Font.BOLD, INK
            );

            // COMPANY LOGO: match the on-screen letter (top-left, full logo + wordmark).
            try {
                Image logo = Image.getInstance(
                        new ClassPathResource(
                                "static/assets/company-logo.png"
                        ).getURL()
                );

                logo.scaleToFit(100, 92);

                PdfPTable logoTable = new PdfPTable(1);
                logoTable.setWidthPercentage(100);
                logoTable.setHorizontalAlignment(Element.ALIGN_LEFT);
                logoTable.setSpacingAfter(20);

                PdfPCell logoCell = new PdfPCell();
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setPadding(0);
                logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                logoCell.setVerticalAlignment(Element.ALIGN_TOP);

                logo.setAlignment(Image.ALIGN_LEFT);
                logoCell.addElement(logo);
                logoTable.addCell(logoCell);

                document.add(logoTable);

            } catch (Exception e) {
                throw new IllegalStateException(
                        "Unable to load company-logo.png", e
                );
            }

            Paragraph heading = new Paragraph(
                    "Employee Performance Appraisal and Salary Revision Letter",
                    title
            );
            heading.setAlignment(Element.ALIGN_CENTER);
            heading.setSpacingAfter(24);
            document.add(heading);

            document.add(new Paragraph(
                    "Dear " + safe(a.getEmployeeName()) + ",", body
            ));

            Paragraph greeting = new Paragraph();
            greeting.add(new Phrase("Greetings from ", body));
            greeting.add(new Phrase(
                    "UXINTERFACELY IT SOLUTIONS LLP!", bold
            ));
            greeting.setSpacingBefore(8);
            greeting.setSpacingAfter(9);
            document.add(greeting);

            Paragraph intro = new Paragraph(
                    "We are pleased to inform you that, following a review " +
                    "of your performance, contributions, and overall progress " +
                    "during the appraisal period, the management has decided " +
                    "to provide you with a performance appraisal and salary " +
                    "revision. We appreciate your dedication, commitment, " +
                    "and efforts toward achieving the organization's goals. " +
                    "Your contributions to the team and the company are valued, " +
                    "and we encourage you to continue maintaining the same " +
                    "level of professionalism and responsibility in your role.",
                    body
            );
            intro.setAlignment(Element.ALIGN_JUSTIFIED);
            intro.setLeading(13.2f);
            intro.setSpacingAfter(11);
            document.add(intro);

            Paragraph detailsHeading = new Paragraph(
                    "Please find your appraisal details below:", body
            );
            detailsHeading.setSpacingAfter(8f);
            document.add(detailsHeading);

            // Use a two-column table for bullets so the gap between the
            // bullet and its text renders consistently in the generated PDF.
            PdfPTable details = new PdfPTable(new float[]{18f, 482f});
            details.setWidthPercentage(100);
            details.setSpacingBefore(3f);
            details.setSpacingAfter(3f);
            details.setKeepTogether(true);

            addDetail(details,
                    "Employee Name: " + safe(a.getEmployeeName()), body);
            addDetail(details,
                    "Designation: " + safe(a.getDesignation()), body);
            addDetail(details,
                    "Department: " + safe(a.getDepartment()), body);
            addDetail(details,
                    "Current Annual CTC: ₹" + fmt(a.getCurrentCtc())
                            + " per annum", body);
            addDetail(details,
                    "Revised Annual CTC: ₹" + fmt(a.getRevisedCtc())
                            + " per annum", body);

            BigDecimal current = n(a.getCurrentCtc());
            BigDecimal revised = n(a.getRevisedCtc());

            BigDecimal pct = current.signum() == 0
                    ? BigDecimal.ZERO
                    : revised.subtract(current)
                            .multiply(new BigDecimal("100"))
                            .divide(current, 2, RoundingMode.HALF_UP);

            addDetail(details,
                    "Increment Percentage: " + pct.toPlainString() + "%",
                    body);

            addDetail(details,
                    "Effective Date: " +
                            (a.getEffectiveDate() == null
                                    ? "DD/MM/YYYY"
                                    : a.getEffectiveDate().format(
                                            DateTimeFormatter.ofPattern(
                                                    "dd/MM/yyyy"
                                            )
                                    )),
                    body);

            Paragraph beforeDetails = new Paragraph(" ");
            beforeDetails.setSpacingBefore(3);
            beforeDetails.setSpacingAfter(3);
            document.add(beforeDetails);

            document.add(details);

            Paragraph afterDetails = new Paragraph(" ");
            afterDetails.setSpacingBefore(3);
            afterDetails.setSpacingAfter(10);
            document.add(afterDetails);

            Paragraph salaryHeading = new Paragraph(
                    "Revised Annual CTC for " +
                            safe(a.getEmployeeName()) + ":", bold
            );
            salaryHeading.setSpacingBefore(8);
            salaryHeading.setSpacingAfter(8);
            document.add(salaryHeading);

            PdfPTable table = new PdfPTable(
                    new float[]{2.2f, 1.3f, 1.3f}
            );
            table.setWidthPercentage(92);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.setHeaderRows(1);
            table.setKeepTogether(true); // Keep the complete salary table on one page
            table.setSpacingAfter(12);

            cell(table, "Component", tableHead, HEADER_BG,
                    Element.ALIGN_CENTER);
            cell(table, "Monthly", tableHead, HEADER_BG,
                    Element.ALIGN_CENTER);
            cell(table, "Yearly", tableHead, HEADER_BG,
                    Element.ALIGN_CENTER);

            BigDecimal ctc = revised;
            BigDecimal twelve = new BigDecimal("12");
            BigDecimal monthlyCtc = ctc.divide(twelve, 2, RoundingMode.HALF_UP);
            BigDecimal basic = monthlyCtc.multiply(new BigDecimal("0.50"));
            BigDecimal hra = basic.multiply(new BigDecimal("0.40"));
            BigDecimal pf = basic.compareTo(new BigDecimal("25000")) >= 0
                    ? new BigDecimal("3000")
                    : basic.multiply(new BigDecimal("0.12"));
            BigDecimal special = monthlyCtc.subtract(basic).subtract(hra).subtract(pf);
            BigDecimal totalCtcMonthly = basic.add(hra).add(special);
            BigDecimal annualVariablePay = a.getVariablePay() == null
                    ? BigDecimal.ZERO : a.getVariablePay().max(BigDecimal.ZERO);
            BigDecimal totalCtcYearly = totalCtcMonthly.multiply(twelve).add(annualVariablePay);

            addSalaryRow(table, "Basic",
                    basic, basic.multiply(twelve), body);

            addSalaryRow(table, "House Rent Allowance (HRA)",
                    hra, hra.multiply(twelve), body);

            addSalaryRow(table, "Special Allowance",
                    special, special.multiply(twelve), body);

            addSalaryRow(table, "Total CTC", totalCtcMonthly, totalCtcYearly, bold);

            addSalaryRow(table, "Variable Pay", null, annualVariablePay, body);

            cell(table, "Deductions", bold, Color.WHITE,
                    Element.ALIGN_LEFT);
            cell(table, "", body, Color.WHITE, Element.ALIGN_RIGHT);
            cell(table, "", body, Color.WHITE, Element.ALIGN_RIGHT);

            BigDecimal epf = pf;
            BigDecimal cpf = pf;
            BigDecimal tax = new BigDecimal("200");

            addSalaryRow(table, "PF-Employee",
                    epf, epf.multiply(new BigDecimal("12")), body);
            addSalaryRow(table, "PF-Employer",
                    cpf, cpf.multiply(new BigDecimal("12")), body);
            addSalaryRow(table, "Professional Tax",
                    tax, tax.multiply(new BigDecimal("12")), body);

            BigDecimal monthlyDed = epf.add(cpf).add(tax);
            BigDecimal yearlyDed = monthlyDed.multiply(twelve);

            addSalaryRow(table, "Total Deductions",
                    monthlyDed, yearlyDed, bold);

            addSalaryRow(table, "Take Home",
                    totalCtcMonthly.subtract(monthlyDed),
                    totalCtcYearly.subtract(yearlyDed), bold);

            document.add(table);

            // Keep page 1 focused on the appraisal details and complete salary table.
            // All explanatory text and sign-off start on page 2, avoiding footer overlap.
            document.newPage();

            addPara(document,
                    "The revised salary will be applicable from the effective " +
                    "date mentioned above. The updated salary structure, " +
                    "including applicable allowances and deductions, will be " +
                    "shared separately or attached to this letter.", body);

            addPara(document,
                    "We expect you to continue improving your skills, taking " +
                    "ownership of your responsibilities, collaborating " +
                    "effectively with your team, and contributing to the " +
                    "company's continued growth and success.", body);

            addPara(document,
                    "Kindly acknowledge receipt of this letter and contact " +
                    "the HR department if you require any clarification " +
                    "regarding your appraisal or revised compensation.", body);

            addPara(document,
                    "Congratulations on your appraisal! We look forward to " +
                    "your continued growth and valuable contributions to " +
                    "UXINTERFACELY IT SOLUTIONS LLP.", body);

            Paragraph signoff = new Paragraph(
                    "Yours faithfully,\n" +
                    "For UXINTERFACELY IT SOLUTIONS LLP,", body
            );
            signoff.setSpacingBefore(18);
            signoff.setSpacingAfter(4);
            signoff.setKeepTogether(true);
            document.add(signoff);

            // ACTUAL SIGNATURE AND COMPANY STAMP.
            try {
                Image signatureStamp = Image.getInstance(
                        new ClassPathResource(
                                "static/assets/signature.png"
                        ).getURL()
                );

                signatureStamp.scaleToFit(120, 75);
                signatureStamp.setAlignment(Image.ALIGN_LEFT);
                signatureStamp.setSpacingBefore(4);
                signatureStamp.setSpacingAfter(8);

                document.add(signatureStamp);

            } catch (Exception e) {
                throw new IllegalStateException(
                        "Unable to load signature.png", e
                );
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to generate appraisal PDF", e
            );
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return output.toByteArray();
    }

    // GST footer is rendered on every PDF page.
    private static class GstFooterEvent extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();

            float left = document.left();
            float right = document.right();
            float y = document.bottom() - 5;

            canvas.saveState();

            canvas.setColorStroke(GRID);
            canvas.setLineWidth(1f);
            canvas.moveTo(left, y + 15);
            canvas.lineTo(right, y + 15);
            canvas.stroke();

            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_CENTER,
                    new Phrase(
                            "GST-36AAIFU2638L1ZQ",
                            new Font(
                                    Font.HELVETICA,
                                    9f,
                                    Font.BOLD,
                                    FOOTER_BLUE
                            )
                    ),
                    (left + right) / 2,
                    y,
                    0
            );

            canvas.restoreState();
        }
    }

    private void addDetail(
            PdfPTable details,
            String text,
            Font font
    ) {
        PdfPCell bullet = new PdfPCell(new Phrase("•", font));
        bullet.setBorder(Rectangle.NO_BORDER);
        bullet.setPaddingLeft(4f);
        bullet.setPaddingRight(0f);
        bullet.setPaddingTop(1f);
        bullet.setPaddingBottom(3f);
        bullet.setVerticalAlignment(Element.ALIGN_TOP);

        PdfPCell content = new PdfPCell(new Phrase(text, font));
        content.setBorder(Rectangle.NO_BORDER);
        content.setPaddingLeft(7f); // Visible horizontal gap after the bullet
        content.setPaddingRight(0f);
        content.setPaddingTop(1f);
        content.setPaddingBottom(3f);
        content.setVerticalAlignment(Element.ALIGN_TOP);

        details.addCell(bullet);
        details.addCell(content);
    }

    private void addPara(
            Document document,
            String text,
            Font font
    ) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        paragraph.setLeading(13.2f);
        paragraph.setSpacingBefore(6);
        paragraph.setSpacingAfter(6);
        document.add(paragraph);
    }

    private void cell(
            PdfPTable table,
            String text,
            Font font,
            Color background,
            int alignment
    ) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBorderColor(INK);
        cell.setHorizontalAlignment(alignment);

        if (background != null) {
            cell.setBackgroundColor(background);
        }

        table.addCell(cell);
    }

    private void addSalaryRow(
            PdfPTable table,
            String name,
            BigDecimal monthly,
            BigDecimal yearly,
            Font font
    ) {
        cell(table, name, font, Color.WHITE, Element.ALIGN_LEFT);
        cell(table, monthly == null ? "" : fmt(monthly),
                font, Color.WHITE, Element.ALIGN_RIGHT);
        cell(table, fmt(yearly),
                font, Color.WHITE, Element.ALIGN_RIGHT);
    }

    private BigDecimal n(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String fmt(BigDecimal value) {
        return money.format(
                n(value).setScale(0, RoundingMode.HALF_UP)
        );
    }

    private String safe(String value) {
        return value == null || value.isBlank()
                ? "[Not provided]"
                : value;
    }
}
