package com.github.gomestkd.eventmanagement.integrationstests.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ItemDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private Double price;

    public ItemDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemDTO itemDTO)) return false;
        return Objects.equals(getId(), itemDTO.getId()) && Objects.equals(getName(), itemDTO.getName()) && Objects.equals(getDescription(), itemDTO.getDescription()) && Objects.equals(getPrice(), itemDTO.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getPrice());
    }
}
