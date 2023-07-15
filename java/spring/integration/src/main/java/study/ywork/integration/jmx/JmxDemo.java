package study.ywork.integration.jmx;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import java.awt.Dimension;
import javax.swing.JFrame;

public class JmxDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JmxConfig.class);
        MyPanel panel = context.getBean(MyPanel.class);
        JFrame frame = createFrame();
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        if (!context.isRunning()) {
            context.close();
        }
    }

    private static JFrame createFrame() {
        JFrame frame = new JFrame("JMX示例");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(new Dimension(500, 400));
        return frame;
    }
}
