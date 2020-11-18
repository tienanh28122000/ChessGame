package chess;

import java.io.Serializable;

/**
 * Player class, với name và color
 */
public class Player implements Serializable {

    private String name;
    private Color color;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

}
