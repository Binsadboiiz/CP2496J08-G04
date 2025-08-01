package controller.staff;

public class Customer {
    private String name;
    private String phone;
    private String type;

    public Customer(String name, String phone, String type) {
        this.name = name;
        this.phone = phone;
        this.type = type;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getType() { return type; }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setType(String type) { this.type = type; }
}
