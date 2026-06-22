package study.ywork.doc.pdfbox.printing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.contentstream.operator.graphics.GraphicsOperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.MissingResourceException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

import javax.print.PrintServiceLookup;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * 透明的PDF文档有时打印速度慢，质量差
 * 本类给出示例解决方案
 */
public class OpaquePDFRenderer extends PDFRenderer {
    public static void main(String[] args) throws IOException, PrinterException, URISyntaxException {
        try (PDDocument doc = Loader.loadPDF(RandomAccessReadBuffer.createBufferFromStream(
                new URI("https://github.com/qzind/tray/files/1749977/test.pdf")
                        .toURL().openStream()))) {
            PDFRenderer renderer = new OpaquePDFRenderer(doc);
            Printable printable = new PDFPrintable(doc, Scaling.SCALE_TO_FIT, false, 0, true, renderer);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(PrintServiceLookup.lookupDefaultPrintService());
            job.setPrintable(printable);

            if (job.printDialog()) {
                job.print();
            }
        }
    }

    public OpaquePDFRenderer(PDDocument document) {
        super(document);
    }

    @Override
    protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
        return new OpaquePageDrawer(parameters);
    }

    private static class OpaquePageDrawer extends PageDrawer {
        OpaquePageDrawer(PageDrawerParameters parameters) throws IOException {
            super(parameters);
            addOperator(new OpaqueDrawObject(this));
            addOperator(new OpaqueSetGraphicsStateParameters(this));
        }
    }

    private static class OpaqueDrawObject extends GraphicsOperatorProcessor {
        OpaqueDrawObject(PDFGraphicsStreamEngine context) {
            super(context);
        }

        private static final Log LOG = LogFactory.getLog(OpaqueDrawObject.class);

        @Override
        public void process(Operator operator, List<COSBase> operands) throws IOException {
            if (operands.isEmpty()) {
                throw new MissingOperandException(operator, operands);
            }

            COSBase base0 = operands.get(0);
            if (!(base0 instanceof COSName)) {
                return;
            }

            COSName objectName = (COSName) base0;
            PDFGraphicsStreamEngine context = getGraphicsContext();
            PDXObject xobject = context.getResources().getXObject(objectName);

            if (xobject == null) {
                throw new MissingResourceException("Missing XObject: " + objectName.getName());
            } else if (xobject instanceof PDImageXObject image) {
                context.drawImage(image);
            } else if (xobject instanceof PDFormXObject formXObject) {
                try {
                    context.increaseLevel();
                    if (context.getLevel() > 50) {
                        LOG.error("recursion is too deep, skipping form XObject");
                        return;
                    }
                    context.showForm(formXObject);
                } finally {
                    context.decreaseLevel();
                }
            }
        }

        @Override
        public String getName() {
            return OperatorName.DRAW_OBJECT;
        }
    }

    private static class OpaqueSetGraphicsStateParameters extends OperatorProcessor {
        private static final Log LOG = LogFactory.getLog(OpaqueSetGraphicsStateParameters.class);

        OpaqueSetGraphicsStateParameters(PDFStreamEngine context) {
            super(context);
        }

        @Override
        public void process(Operator operator, List<COSBase> arguments) throws IOException {
            if (arguments.isEmpty()) {
                throw new MissingOperandException(operator, arguments);
            }
            COSBase base0 = arguments.get(0);
            if (!(base0 instanceof COSName)) {
                return;
            }

            COSName graphicsName = (COSName) base0;
            PDFStreamEngine context = getContext();
            PDExtendedGraphicsState gs = context.getResources().getExtGState(graphicsName);

            if (gs == null) {
                LOG.error("name for 'gs' operator not found in resources: /" + graphicsName.getName());
                return;
            }

            gs.setNonStrokingAlphaConstant(1f);
            gs.setStrokingAlphaConstant(1f);
            gs.copyIntoGraphicsState(context.getGraphicsState());
        }

        @Override
        public String getName() {
            return OperatorName.SET_GRAPHICS_STATE_PARAMS;
        }
    }
}
