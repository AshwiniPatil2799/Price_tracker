package com.example.myapplication.activity;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class PriceTrackerEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "price")
    private double price;
    @ColumnInfo(name = "change")
    private double change;

    // Constructor
    public PriceTrackerEntity(int id, String name, double price, double change) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.change = change;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getChange() {
        return change;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setChange(double change) {
        this.change = change;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", change=" + change +
                '}';
    }
}