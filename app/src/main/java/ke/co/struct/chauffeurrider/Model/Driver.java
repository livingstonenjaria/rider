package ke.co.struct.chauffeurrider.Model;

public class Driver {
    String name, phone, model, plate,  pic, car;

    public Driver() {
    }

    public Driver(String name, String phone, String model, String plate, String pic, String car) {
        this.name = name;
        this.phone = phone;
        this.model = model;
        this.plate = plate;
        this.pic = pic;
        this.car = car;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getModel() {
        return model;
    }

    public String getPlate() {
        return plate;
    }

    public String getPic() {
        return pic;
    }

    public String getCar() {
        return car;
    }
}
