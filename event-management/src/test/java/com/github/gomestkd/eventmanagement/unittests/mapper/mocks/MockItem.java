package com.github.gomestkd.eventmanagement.unittests.mapper.mocks;

import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.model.Item;
import java.util.ArrayList;
import java.util.List;

public class MockItem {

    public Item mockEntity(Integer i) {
        Item item = new Item();
        item.setId(i.longValue());
        item.setName("Name: " + i);
        item.setDescription("Description: " + i);
        item.setPrice(Double.valueOf(i));
        return item;
    }

    public ItemDTO mockDTO(Integer i) {
        ItemDTO item = new ItemDTO();
        item.setId(i.longValue());
        item.setName("Name: " + i);
        item.setDescription("Description: " + i);
        item.setPrice(Double.valueOf(i));
        return item;
    }

    public Item mockEntity() {
        return mockEntity(0);
    }

    public ItemDTO mockDTO() {
        return mockDTO(0);
    }

    public List<Item> mockEntityList() {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            items.add(mockEntity(i));
        }
        return items;
    }

    public List<ItemDTO> mockDTOList() {
        List<ItemDTO> items = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            items.add(mockDTO(i));
        }
        return items;
    }
}