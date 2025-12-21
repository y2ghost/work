package study.ywork.doc.itext.page;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;


public class MyImageRenderListener implements IEventListener {
    private String path;

    public MyImageRenderListener(String path) {
        this.path = path;
    }

    public void eventOccurred(IEventData data, EventType type) {
        if (Objects.requireNonNull(type) == EventType.RENDER_IMAGE) {
            try {
                ImageRenderInfo renderInfo = (ImageRenderInfo) data;
                PdfImageXObject image = renderInfo.getImage();
                if (image == null) {
                    return;
                }

                byte[] imageByte = image.getImageBytes(true);
                String extension = image.identifyImageFileExtension();
                String filename = String.format(path, image.getPdfObject().getIndirectReference().getObjNumber(), extension);

                try (FileOutputStream os = new FileOutputStream(filename)) {
                    os.write(imageByte);
                    os.flush();
                }
            } catch (com.itextpdf.io.exceptions.IOException | IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public Set<EventType> getSupportedEvents() {
        return Collections.emptySet();
    }
}
