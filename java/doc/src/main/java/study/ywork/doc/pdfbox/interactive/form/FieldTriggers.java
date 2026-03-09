package study.ywork.doc.pdfbox.interactive.form;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDAnnotationAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;

public final class FieldTriggers {
    private FieldTriggers() {
    }

    public static void main(String[] args) throws IOException {
        try (PDDocument document = Loader.loadPDF(new File("target/SimpleForm.pdf"))) {
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            PDField field = acroForm.getField("SampleField");
            PDAnnotationWidget widget = field.getWidgets().get(0);
            PDAnnotationAdditionalActions annotationActions = new PDAnnotationAdditionalActions();
            PDActionJavaScript jsEnterAction = new PDActionJavaScript();
            jsEnterAction.setAction("app.alert(\"On 'enter' action\")");
            annotationActions.setE(jsEnterAction);

            PDActionJavaScript jsExitAction = new PDActionJavaScript();
            jsExitAction.setAction("app.alert(\"On 'exit' action\")");
            annotationActions.setX(jsExitAction);

            PDActionJavaScript jsMouseDownAction = new PDActionJavaScript();
            jsMouseDownAction.setAction("app.alert(\"On 'mouse down' action\")");
            annotationActions.setD(jsMouseDownAction);

            PDActionJavaScript jsMouseUpAction = new PDActionJavaScript();
            jsMouseUpAction.setAction("app.alert(\"On 'mouse up' action\")");
            annotationActions.setU(jsMouseUpAction);

            PDActionJavaScript jsFocusAction = new PDActionJavaScript();
            jsFocusAction.setAction("app.alert(\"On 'focus' action\")");
            annotationActions.setFo(jsFocusAction);

            PDActionJavaScript jsBlurredAction = new PDActionJavaScript();
            jsBlurredAction.setAction("app.alert(\"On 'blurred' action\")");
            annotationActions.setBl(jsBlurredAction);
            widget.setActions(annotationActions);

            PDFormFieldAdditionalActions fieldActions = new PDFormFieldAdditionalActions();
            PDActionJavaScript jsKeystrokeAction = new PDActionJavaScript();
            jsKeystrokeAction.setAction("app.alert(\"On 'keystroke' action\")");
            fieldActions.setK(jsKeystrokeAction);

            PDActionJavaScript jsFormattedAction = new PDActionJavaScript();
            jsFormattedAction.setAction("app.alert(\"On 'formatted' action\")");
            fieldActions.setF(jsFormattedAction);

            PDActionJavaScript jsChangedAction = new PDActionJavaScript();
            jsChangedAction.setAction("app.alert(\"On 'change' action\")");

            PDActionJavaScript jsRecalculateAction = new PDActionJavaScript();
            jsRecalculateAction.setAction("app.alert(\"On 'recalculate' action\")");
            fieldActions.setC(jsRecalculateAction);
            field.getActions().getCOSObject().addAll(fieldActions.getCOSObject());
            document.save("target/FieldTriggers.pdf");
        }
    }
}
