package study.ywork.doc.itext.page;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Set;

public class MyTextRenderListener implements IEventListener {
    private final PrintWriter out;

    public MyTextRenderListener(PrintWriter out) {
        this.out = out;
    }

    public void eventOccurred(IEventData data, EventType type) {
        switch (type) {
            case BEGIN_TEXT:
                out.print("<");
                break;
            case RENDER_TEXT:
                TextRenderInfo renderInfo = (TextRenderInfo) data;
                out.print("<");
                out.print(renderInfo.getText());
                out.print(">");
                break;
            case END_TEXT:
                out.println(">");
                break;
            default:
                break;
        }
    }

    public Set<EventType> getSupportedEvents() {
        return Collections.emptySet();
    }
}
