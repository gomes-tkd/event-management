package com.github.gomestkd.eventmanagement.unittests.mapper;

import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.model.Item;
import com.github.gomestkd.eventmanagement.unittests.mapper.mocks.MockItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.gomestkd.eventmanagement.mapper.ObjectMapper.parseObject;
import static com.github.gomestkd.eventmanagement.mapper.ObjectMapper.parseSetObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ObjectMapperItemTests {
    MockItem inputObject;

    @BeforeEach
    public void setUp() {
        inputObject = new MockItem();
    }

    @Test
    @DisplayName("parseObject: Should correctly map an Item entity to an ItemDTO")
    public void parserEntityToDTOTest() {
        ItemDTO output = parseObject(inputObject.mockEntity(), ItemDTO.class);

        assertNotNull(output);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Name: 0", output.getName());
        assertEquals(Double.valueOf(0D), output.getPrice());
        assertEquals("Description: 0", output.getDescription());
    }

    @Test
    @DisplayName("parseSetObject: Should correctly map a list of Item entities to a set of ItemDTOs")
    public void parserEntityListToDTOListTest() {
        Set<ItemDTO> outputList = parseSetObject(
                new HashSet<>(inputObject.mockEntityList()),
                ItemDTO.class
        );

        List<ItemDTO> outputAsList = new ArrayList<>(outputList);

        ItemDTO outputFirstTest = outputAsList.getFirst();
        assertNotNull(outputFirstTest);
    }

    @Test
    @DisplayName("parseObject: Should correctly map an ItemDTO to an Item entity")
    public void parserDTOToEntityTest() {
        Item output = parseObject(inputObject.mockDTO(), Item.class);

        assertNotNull(output);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Name: 0", output.getName());
        assertEquals(Double.valueOf(0D), output.getPrice());
        assertEquals("Description: 0", output.getDescription());
    }

    @Test
    @DisplayName("parseSetObject: Should correctly map a list of ItemDTOs to a set of Item entities")
    public void parserDTOListToEntityListTest() {
        Set<Item> outputList = parseSetObject(
                new HashSet<>(inputObject.mockDTOList()),
                Item.class
        );

        List<Item> outputAsList = new ArrayList<>(outputList);

        Item outputFirstTest = outputAsList.get(13);
        assertNotNull(outputFirstTest);
    }
}