package common;

public class Order {
    private final int id;
    private final String menuName;

    public Order(int id, String menuName) {
        this.id = id;
        this.menuName = menuName;
    }

    @Override
    public String toString() {
        return "🍚#" + id + "(" + menuName + ")";
    }
}
