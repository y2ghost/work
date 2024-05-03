package study.ywork.integration.jmx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.awt.Graphics;
import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.Timer;

@Component
public class MyPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    @Autowired
    private MyModelBean myModelBean;
    private int angle = 0;
    private Timer timer;

    public MyPanel(MyModelBean myModelBean) {
        this.myModelBean = myModelBean;
    }

    @PostConstruct
    public void postConstruct() {
        myModelBean.setLineLength(100);
        myModelBean.setDelay(200);
        myModelBean.addStopListener(stop -> {
            if (stop) {
                timer.stop();
            } else {
                timer.restart();
            }
        });
        initTimer();
    }

    private void initTimer() {
        timer = new Timer(myModelBean.getDelay(), e -> {
            angle += 5;
            if (angle >= 360) {
                angle = 0;
            }

            repaint();
            if (timer.getDelay() != myModelBean.getDelay()) {
                if (myModelBean.getDelay() < 0) {
                    myModelBean.setDelay(0);
                }

                timer.setDelay(myModelBean.getDelay());
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        int lineLength = myModelBean.getLineLength();
        int x1 = getWidth() / 2;
        int y1 = getHeight() / 2;
        double tempAngle = this.angle * Math.PI / 180;
        int x2 = (int) (x1 + lineLength * Math.sin(tempAngle));
        int y2 = (int) (y1 + lineLength * Math.cos(tempAngle));
        g.drawLine(x1, y1, x2, y2);
    }
}
