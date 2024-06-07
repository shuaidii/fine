/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-11-07
 * Time: 22:01
 */

public class Pair {
    private int id;
    private char direction; // {’e’, ’w’, ’n’, ’s’}

    public Pair(int i, char d) {
        this.id = i;
        this.direction = d;
    }

    public char getDirection() {
        return direction;
    }

    public int getId() {
        return id;
    }

    public void setDirection(char d) {
        this.direction = d;
    }

    public void setId(int i) {
        this.id = i;
    }
}
