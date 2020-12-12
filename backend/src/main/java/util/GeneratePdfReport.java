package util;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;


public class GeneratePdfReport {
    private static final Logger logger = LoggerFactory.getLogger(GeneratePdfReport.class);
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static Paragraph rechnungPar;
    private static Paragraph userPar;
    private static Paragraph eventPar;
    private static Paragraph gesetzPar;
    private static PdfPTable table;
    private static PdfPTable images_table;
    private static PdfPTable intro_table;
    private static Paragraph showPar;
    private final String uploadDir = Paths.get("").toAbsolutePath().toString();

    private static void initializeData(List<Ticket> tickets,
                                       ApplicationUser applicationuser,
                                       Long orderId,
                                       LocalDate datePurchase) throws BadElementException, IOException {
        String imageFile = Paths.get("").toAbsolutePath().toString() + File.separator
            + "src/main/resources/templates/Illustration.png";
        Image logo = Image.getInstance(imageFile);
        logo.scaleAbsolute(110, 90);
        logo.setAlignment(Image.ALIGN_LEFT);


        final Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

        String user = applicationuser.getSurname() + " "
            + applicationuser.getFirstName() + "\n" + applicationuser.getEmail();
        userPar = new Paragraph(user, new Font(Font.FontFamily.HELVETICA, 11.0f));
        userPar.setIndentationLeft(20);


        BufferedImage bufferedQrcode = null;
        try {
            bufferedQrcode = generateQRcodeImage(applicationuser.getSurname() + tickets.get(0).getId());
        } catch (Exception e) {
            LOGGER.info("QR code could not be generated");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedQrcode, "png", baos);
        Image qrcode = Image.getInstance(baos.toByteArray());
        qrcode.scaleAbsolute(150, 150);
        qrcode.setAlignment(Image.ALIGN_RIGHT);

        images_table = new PdfPTable(2);
        PdfPCell hcell;
        hcell = new PdfPCell(logo);
        hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hcell.setBorder(Rectangle.NO_BORDER);

        images_table.addCell(hcell);

        hcell = new PdfPCell(qrcode);
        hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        hcell.setBorder(Rectangle.NO_BORDER);

        images_table.addCell(hcell);
        images_table.setWidthPercentage(93);

        String company = "Firma\n"
            + "Ticketline Austria GmbH\n"
            + "ticketline@email.at\n"
            + "Wiedner Hauptstraße 10\n"
            + "1040 Wien";

        intro_table = new PdfPTable(2);
        hcell = new PdfPCell(new Phrase(company, new Font(Font.FontFamily.HELVETICA, 11.0f)));
        hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hcell.setBorder(Rectangle.NO_BORDER);
        intro_table.addCell(hcell);

        String formatter = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String rechnungsdaten = "Rechnungsnummer " + orderId + "\n"
            + "            Rechnungsdatum: " + datePurchase + "\n"
            + "            Liefer-/Leistungsdatum: " + formatter;


        hcell = new PdfPCell(new Phrase(rechnungsdaten, new Font(Font.FontFamily.HELVETICA, 11.0f)));
        hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        hcell.setBorder(Rectangle.NO_BORDER);
        intro_table.addCell(hcell);

        intro_table.setWidthPercentage(93);

        String event = "Event: " + tickets.get(0).getShow().getEvent().getTitle();
        eventPar = new Paragraph(event, new Font(Font.FontFamily.HELVETICA, 11.0f));
        eventPar.setIndentationLeft(20);

        String show = "Show: " + tickets.get(0).getShow().getTitle();
        showPar = new Paragraph(show, new Font(Font.FontFamily.HELVETICA, 11.0f));
        showPar.setIndentationLeft(20);

        String gesetz = "Umsatzsteuerbefreit – Kleinunternehmer gem. § 6 Abs. 1 Z 27 UStG";
        gesetzPar = new Paragraph(gesetz);
        gesetzPar.setIndentationLeft(20);


        table = new PdfPTable(4);
        table.setWidthPercentage(93);
        try {
            table.setWidths(new int[]{5, 9, 4, 4});
        } catch (DocumentException e) {
            LOGGER.warn("Could not generate PDF");
        }


        hcell = new PdfPCell(new Phrase("Artikel", headFont));
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        hcell.setBorder(Rectangle.NO_BORDER);
        table.addCell(hcell);


        hcell = new PdfPCell(new Phrase("Bezeichnung", headFont));
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        hcell.setBorder(Rectangle.NO_BORDER);
        table.addCell(hcell);


        hcell = new PdfPCell(new Phrase("Menge", headFont));
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        hcell.setBorder(Rectangle.NO_BORDER);
        table.addCell(hcell);

        hcell = new PdfPCell(new Phrase("Gesamtpreis", headFont));
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        hcell.setBorder(Rectangle.NO_BORDER);
        table.addCell(hcell);

    }

    public static BufferedImage generateQRcodeImage(String barcodeText) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix =
            barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static ByteArrayInputStream ticketsPurchaseReport(List<Ticket> tickets) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            initializeData(tickets, tickets.get(0).getUser(), tickets.get(0).getOrderId(),
                tickets.get(0).getDateOfPurchase());
        } catch (BadElementException e) {
            LOGGER.warn("PDF elements not valid!");
        } catch (IOException e) {
            LOGGER.debug("Could not generate PDF");
        }

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20.0f, Font.BOLD);
        String rechnung = "Rechnung";
        rechnungPar = new Paragraph(rechnung, titleFont);
        rechnungPar.setIndentationLeft(20);

        try {

            int ticketId = 1;
            float sum = 0;

            for (Ticket ticket : tickets) {

                PdfPCell cell;

                cell = new PdfPCell(new Phrase("Ticket " + ticketId));

                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);


                cell = new PdfPCell(new Phrase(
                    "Sector: " + ticket.getSector() + ", Row: " + ticket.getSeat().getRowNr() + ", Seat: "
                        + ticket.getSeat().getSeatNr()));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("1"));

                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);


                cell = new PdfPCell(new Phrase(String.valueOf(ticket.getPrice())));

                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPaddingRight(5);
                table.addCell(cell);

                sum += ticket.getPrice();


                ticketId++;

                PdfPCell blankRow = new PdfPCell(new Phrase("\n\n"));
                blankRow.setFixedHeight(3f);
                blankRow.setColspan(4);
                blankRow.setBorder(Rectangle.NO_BORDER);
                table.addCell(blankRow);
            }

            PdfPCell sumcell;

            sumcell = new PdfPCell(new Phrase("Entgelt"));
            sumcell.setBorder(Rectangle.TOP);
            sumcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            sumcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(sumcell);


            sumcell = new PdfPCell();
            sumcell.setBorder(Rectangle.TOP);
            table.addCell(sumcell);

            sumcell = new PdfPCell();
            sumcell.setBorder(Rectangle.TOP);
            table.addCell(sumcell);


            sumcell = new PdfPCell(new Phrase(String.valueOf(df.format(sum))));
            sumcell.setBorder(Rectangle.TOP);
            sumcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sumcell);

            PdfWriter.getInstance(document, out);
            document.open();
            document.add(images_table);
            document.add(Chunk.NEWLINE);
            document.add(rechnungPar);
            document.add(Chunk.NEWLINE);
            document.add(intro_table);
            document.add(Chunk.NEWLINE);
            document.add(userPar);
            document.add(Chunk.NEWLINE);
            document.add(eventPar);
            document.add(showPar);
            document.add(Chunk.NEWLINE);
            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(gesetzPar);

            document.close();

        } catch (DocumentException ex) {

            logger.error("Error occurred: {0}", ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }


    public static ByteArrayInputStream ticketsCancelReport(List<Ticket> tickets, List<Ticket> canceledTicket,
                                                           ApplicationUser applicationuser,
                                                           Long orderId,
                                                           Long bonusPoints) {


        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20.0f, Font.BOLD);
        String rechnung = "Stornorechnung";
        rechnungPar = new Paragraph(rechnung, titleFont);
        rechnungPar.setIndentationLeft(20);

        try {
            initializeData(tickets, applicationuser, orderId, LocalDate.now());
        } catch (BadElementException e) {
            LOGGER.warn("PDF elements not valid!");
        } catch (IOException e) {
            LOGGER.debug("Could not generate PDF");
        }


        int ticketId = 1;
        double sum = 0;


        for (Ticket ticket : tickets) {

            PdfPCell cell;

            cell = new PdfPCell(new Phrase("Ticket " + ticketId));

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);


            cell = new PdfPCell(new Phrase(
                "Sector: " + ticket.getSector() + ", Row: " + ticket.getSeat().getRowNr() + ", Seat: "
                    + ticket.getSeat().getSeatNr()));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("1"));

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);


            cell = new PdfPCell(new Phrase(String.valueOf(ticket.getPrice())));

            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPaddingRight(5);
            table.addCell(cell);

            sum += ticket.getPrice();


            ticketId++;

            PdfPCell blankRow = new PdfPCell(new Phrase("\n\n"));
            blankRow.setFixedHeight(3f);
            blankRow.setColspan(4);
            blankRow.setBorder(Rectangle.NO_BORDER);
            table.addCell(blankRow);
        }

        blankLine(table);
        blankLine(table);


        ticketId = tickets.size() - canceledTicket.size() + 1;
        for (Ticket ticket : canceledTicket) {

            PdfPCell cell;

            cell = new PdfPCell(new Phrase("Ticket " + ticketId));

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);


            cell = new PdfPCell(new Phrase(
                "Sector: " + ticket.getSector() + ", Row: " + ticket.getSeat().getRowNr() + ", Seat: "
                    + ticket.getSeat().getSeatNr()));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("1"));

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);


            cell = new PdfPCell(new Phrase("-" + ticket.getPrice()));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPaddingRight(5);
            table.addCell(cell);

            sum -= ticket.getPrice();


            ticketId++;

            PdfPCell blankRow = new PdfPCell(new Phrase("\n\n"));
            blankRow.setFixedHeight(3f);
            blankRow.setColspan(4);
            blankRow.setBorder(Rectangle.NO_BORDER);
            table.addCell(blankRow);

        }

        PdfPCell sumcell;

        sumcell = new PdfPCell(new Phrase("Kosten fuer: "));
        sumcell.setBorder(Rectangle.TOP);
        sumcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        sumcell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(sumcell);


        sumcell = new PdfPCell(new Phrase("eingeloeste Tickets"));
        sumcell.setBorder(Rectangle.TOP);
        sumcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        sumcell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(sumcell);

        sumcell = new PdfPCell();
        sumcell.setBorder(Rectangle.TOP);
        table.addCell(sumcell);


        sumcell = new PdfPCell(new Phrase(df.format(bonusPoints)));
        sumcell.setBorder(Rectangle.TOP);
        sumcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(sumcell);


        sumcell = new PdfPCell(new Phrase("Entgelt"));
        sumcell.setBorder(Rectangle.NO_BORDER);
        sumcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        sumcell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(sumcell);


        sumcell = new PdfPCell();
        sumcell.setBorder(Rectangle.NO_BORDER);
        table.addCell(sumcell);

        sumcell = new PdfPCell();
        sumcell.setBorder(Rectangle.NO_BORDER);
        table.addCell(sumcell);


        sumcell = new PdfPCell(new Phrase(String.valueOf(df.format(sum + bonusPoints))));
        sumcell.setBorder(Rectangle.NO_BORDER);
        sumcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(sumcell);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);

            document.open();
            document.add(images_table);
            document.add(Chunk.NEWLINE);
            document.add(rechnungPar);
            document.add(Chunk.NEWLINE);
            document.add(intro_table);
            document.add(Chunk.NEWLINE);
            document.add(userPar);
            document.add(Chunk.NEWLINE);
            document.add(eventPar);
            document.add(showPar);
            document.add(Chunk.NEWLINE);
            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(gesetzPar);

        } catch (DocumentException e) {
            LOGGER.warn("Error ocurred when creating the Document!");
        }
        document.close();


        return new ByteArrayInputStream(out.toByteArray());
    }


    public static void blankLine(PdfPTable table) {
        PdfPCell cell;

        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);


        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

    }
}
