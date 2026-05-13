package study.ywork.doc.itext.form;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

import java.io.IOException;

public class ChoiceFields {
    private static final String[] RESULT = {
        "choiceFields.pdf",
        "choiceFields_filled.pdf"
    };
    private static final String DEST = RESULT[0];
    private static final String[] LANGUAGES =
        {"English", "German", "French", "Spanish", "Dutch"};
    private static final String[] EXPORT_VALUES =
        {"EN", "DE", "FR", "ES", "NL"};

    public static void main(String[] args) {
        new ChoiceFields().manipulatePdf();
    }

    public void createPdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            Cell space = new Cell(1, 2)
                .setBorder(Border.NO_BORDER)
                .setHeight(8);
            Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            Style leftCellStyle = new Style()
                .setBorder(Border.NO_BORDER)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);
            table.addCell(new Cell().add(new Paragraph("Language of the movie:")).addStyle(leftCellStyle));
            Cell cell = new Cell().setHeight(20);
            cell.setNextRenderer(new ChoiceCellRenderer(cell, 1));
            table.addCell(cell);
            table.addCell(space.clone(true));
            table.addCell(new Cell().add(new Paragraph("Subtitle languages:")).addStyle(leftCellStyle));
            cell = new Cell().setHeight(71);
            cell.setNextRenderer(new ChoiceCellRenderer(cell, 2));
            table.addCell(cell);
            table.addCell(space.clone(true));
            table.addCell(new Cell().add(new Paragraph("Select preferred language")).addStyle(leftCellStyle));
            cell = new Cell();
            cell.setNextRenderer(new ChoiceCellRenderer(cell, 3));
            table.addCell(cell);
            table.addCell(space.clone(true));
            table.addCell(new Cell().add(new Paragraph("Language of the director:"))
                .setBorder(Border.NO_BORDER)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT));
            cell = new Cell();
            cell.setNextRenderer(new ChoiceCellRenderer(cell, 4));
            table.addCell(cell);
            doc.add(table);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void fillPdf(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            form.getField("choice_1").setValue("NL");
            PdfArray array = (PdfArray) (form.getField("choice_3")).getPdfObject().get(PdfName.Opt);
            PdfChoiceFormField choice2 = (PdfChoiceFormField) form.getField("choice_2");
            choice2.setListSelected(new String[]{"German", "Spanish"});
            String[] languages = getOptions(array);
            String[] exportValues = getExports(array);
            int n = languages.length;
            PdfArray newOptions = new PdfArray();
            PdfArray tempArr;

            for (int i = 0; i < n; i++) {
                tempArr = new PdfArray();
                tempArr.add(new PdfString(languages[i]));
                tempArr.add(new PdfString(exportValues[i]));
                newOptions.add(tempArr);
            }
            tempArr = new PdfArray();
            tempArr.add(new PdfString("CN"));
            tempArr.add(new PdfString("Chinese"));
            newOptions.add(tempArr);

            tempArr = new PdfArray();
            tempArr.add(new PdfString("JP"));
            tempArr.add(new PdfString("Japanese"));
            newOptions.add(tempArr);

            form.getField("choice_3").setOptions(newOptions);

            form.getField("choice_3").setValue("CN");
            form.getField("choice_4").setValue("Japanese");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    protected void manipulatePdf() {
        createPdf(DEST);
        fillPdf(DEST, RESULT[1]);
    }

    protected String[] getOptions(PdfArray array) {
        String[] result = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            result[i] = ((PdfArray) array.get(i)).get(0).toString();
        }

        return result;
    }

    protected String[] getExports(PdfArray array) {
        String[] result = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            result[i] = ((PdfArray) array.get(i)).get(1).toString();
        }

        return result;
    }

    private static class ChoiceCellRenderer extends CellRenderer {
        private final int cf;

        public ChoiceCellRenderer(Cell modelElement, int cf) {
            super(modelElement);
            this.cf = cf;
        }

        @Override
        public IRenderer getNextRenderer() {
            return new ChoiceCellRenderer((Cell) modelElement, cf);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            PdfChoiceFormField text = null;
            String[][] langAndExpArray;
            String[] langArray;
            PdfDocument document = drawContext.getDocument();

            switch (cf) {
                case 1 -> {
                    langAndExpArray = new String[LANGUAGES.length][];
                    for (int i = 0; i < LANGUAGES.length; i++) {
                        langAndExpArray[i] = new String[2];
                        langAndExpArray[i][0] = EXPORT_VALUES[i];
                        langAndExpArray[i][1] = LANGUAGES[i];
                    }
                    text = PdfFormField.createList(document, getOccupiedAreaBBox(), choiceFormat(cf), "", langAndExpArray);
                    text.setTopIndex(1);
                }
                case 2 -> {
                    langArray = new String[LANGUAGES.length];
                    System.arraycopy(LANGUAGES, 0, langArray, 0, LANGUAGES.length);
                    text = PdfFormField.createList(document, getOccupiedAreaBBox(), choiceFormat(cf), "", langArray);
                    text.setBorderColor(ColorConstants.GREEN);
                    PdfDictionary borderDict = new PdfDictionary();
                    borderDict.put(PdfName.S, PdfName.D);
                    text.getWidgets().get(0).setBorderStyle(borderDict);
                    text.setMultiSelect(true);
                    text.setListSelected(new int[]{0, 2});
                }
                case 3 -> {
                    langAndExpArray = new String[LANGUAGES.length][];
                    for (int i = 0; i < LANGUAGES.length; i++) {
                        langAndExpArray[i] = new String[2];
                        langAndExpArray[i][0] = EXPORT_VALUES[i];
                        langAndExpArray[i][1] = LANGUAGES[i];
                    }
                    text = PdfFormField.createComboBox(document, getOccupiedAreaBBox(), choiceFormat(cf), "", langAndExpArray);
                    text.setBorderColor(ColorConstants.RED);
                    text.setBackgroundColor(ColorConstants.GRAY);
                    text.setListSelected(new int[]{4});
                }
                case 4 -> {
                    langArray = new String[LANGUAGES.length];
                    System.arraycopy(LANGUAGES, 0, langArray, 0, LANGUAGES.length);
                    text = PdfFormField.createComboBox(document, getOccupiedAreaBBox(), choiceFormat(cf), "", langArray);
                    text.setFieldFlag(PdfChoiceFormField.FF_EDIT, true);
                }
                default -> { /** NOOP */}

            }

            PdfAcroForm.getAcroForm(document, true).addField(text);
        }

        private static String choiceFormat(int cf) {
            return String.format("choice_%s", cf);
        }
    }
}
