package study.ywork.cook.object;

import java.util.Objects;

// Objects.requireNonNull示例
public class Flower {
    private final String name;
    private final String color;

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Flower(String name, String color) {
        this.name = Objects.requireNonNull(name, "花名不能为null");
        this.color = Objects.requireNonNull(color, "花的颜色不能为null");
    }
}
