package com.github.gomestkd.eventmanagement.repositories;

import com.github.gomestkd.eventmanagement.model.Item;
import com.github.gomestkd.eventmanagement.integrationstests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private Item item2;
    private Item item3;
    private Item item4;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        itemRepository.flush();

        item = new Item();
        item.setName("Coca-Cola");
        item.setPrice(10.00);
        item.setDescription("600ml - Original");

        item2 = new Item();
        item2.setName("Pepsi");
        item2.setPrice(9.00);
        item2.setDescription("600ml - Original");

        item3 = new Item();
        item3.setName("Sprite");
        item3.setPrice(8.00);
        item3.setDescription("600ml - Lemon Lime");

        item4 = new Item();
        item4.setName("Cachorro-quente");
        item4.setPrice(7.00);
        item4.setDescription("Simple - complete with ketchup and mustard");

        itemRepository.saveAll(List.of(item, item2, item3, item4));
        itemRepository.flush();

        item = itemRepository.findAll().stream()
                .filter(i -> i.getName().equals("Coca-Cola"))
                .findFirst().orElseThrow();
        item2 = itemRepository.findAll().stream()
                .filter(i -> i.getName().equals("Pepsi"))
                .findFirst().orElseThrow();
        item3 = itemRepository.findAll().stream()
                .filter(i -> i.getName().equals("Sprite"))
                .findFirst().orElseThrow();
        item4 = itemRepository.findAll().stream()
                .filter(i -> i.getName().equals("Cachorro-quente"))
                .findFirst().orElseThrow();
    }

    @Test
    @Order(1)
    @DisplayName("findItemsByName: Should return the item 'Coca-Cola' when searched by name")
    void findItemsByName() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "name")
        );

        Item foundItem = itemRepository.findItemsByName("Coca-Cola", pageable).getContent().getFirst();

        Assertions.assertNotNull(foundItem);

        Assertions.assertEquals(item.getId(), foundItem.getId());

        Assertions.assertEquals("Coca-Cola", foundItem.getName());
        Assertions.assertEquals(10.00, foundItem.getPrice());
        Assertions.assertEquals("600ml - Original", foundItem.getDescription());
    }

    @Test
    @Order(2)
    @DisplayName("findItemsByDescription: Should return 'Coca-Cola' when searched by partial, case-insensitive description 'OriGIna'")
    void findItemsByDescription() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "description")
        );

        Item foundItem = itemRepository
                .findItemsByDescription("OriGIna", pageable)
                .getContent()
                .getFirst();

        Assertions.assertNotNull(foundItem);
        Assertions.assertEquals(item.getId(), foundItem.getId());
        Assertions.assertEquals("Coca-Cola", foundItem.getName());
        Assertions.assertEquals(10.00, foundItem.getPrice());
        Assertions.assertEquals("600ml - Original", foundItem.getDescription());
    }

    @Test
    @Order(3)
    @DisplayName("findItemsByPriceRange: Should return all items priced between $7.00 and $10.00")
    void findItemsByPriceRange() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "price")
        );

        Set<Item> foundItems = Set.copyOf(itemRepository.findItemsByPriceRange(7.00, 10.00, pageable).getContent());
        Assertions.assertNotNull(foundItems);
        Assertions.assertEquals(4, foundItems.size());
        Assertions.assertTrue(foundItems.contains(item));
        Assertions.assertTrue(foundItems.contains(item2));
        Assertions.assertTrue(foundItems.contains(item3));
        Assertions.assertTrue(foundItems.contains(item4));
    }

    @Disabled("Method not implemented yet")
    @Test
    @Order(4)
    @DisplayName("findItemsByNameAndDescription: Should search items by both name and description (test pending implementation)")
    void findItemsByNameAndDescription() {
    }
}