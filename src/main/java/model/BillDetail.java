package model;

public class BillDetail {
    private int id;
    private User user;
    private Dish dish;
    private int quantity;

    public BillDetail(int id, User user, Dish dish, int quantity) {
        this.id = id;
        this.user = user;
        this.dish = dish;
        this.quantity = quantity;
    }

    public BillDetail(User user, Dish dish, int quantity) {
        this.user = user;
        this.dish = dish;
        this.quantity = quantity;
    }

    public BillDetail() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "BillDetail{" +
                "id=" + id +
                ", user=" + user +
                ", dish=" + dish +
                ", quantity=" + quantity +
                '}';
    }
}