package study.ywork.web.domain;

import java.io.Serializable;

/*
 * 演示全局Application对象共享
 */
public class GlobalStyle implements Serializable {
    private static final long serialVersionUID = 1L;
    private String background = "#fff";
    private String fontSize = "14px";

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }
}
