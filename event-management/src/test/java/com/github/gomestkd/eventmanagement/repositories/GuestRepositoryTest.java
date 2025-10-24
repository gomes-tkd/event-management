package com.github.gomestkd.eventmanagement.repositories;


import com.github.gomestkd.eventmanagement.integrationstests.testcontainers.AbstractIntegrationTest;
import com.github.gomestkd.eventmanagement.model.Guest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class GuestRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private GuestRepository guestRepository;

    private Guest guest;
    private Guest guest2;

    @BeforeEach
    void setUp() {
        guestRepository.deleteAll();
        guestRepository.flush();

        guest = new Guest();
        guest.setName("John Doe");
        guest.setEmail("email.john.doe@email");
        guest.setPhone("(99) 9 9999-9999");

        guest2 = new Guest();
        guest2.setName("Alice Johnson");
        guest2.setEmail("email.alice.johnson@email");
        guest2.setPhone("(77) 7 7777-7777");

        guestRepository.saveAll(List.of(guest, guest2));
        guestRepository.flush();
    }

    @Test()
    @Order(1)
    @DisplayName("GuestRepository: Should find guests by partial name match (case-insensitive)")
    void findGuestsByName() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "name")
        );

        List<Guest>foundGuests = guestRepository.findGuestsByName("hn", pageable).getContent();

        Assertions.assertNotNull(foundGuests);
        Assertions.assertEquals(2, foundGuests.size());

        Assertions.assertTrue(foundGuests.contains(guest));
        Assertions.assertTrue(foundGuests.contains(guest2));

        Guest guestOne = foundGuests.getFirst();
        Assertions.assertEquals("Alice Johnson", guestOne.getName());
        Assertions.assertEquals("email.alice.johnson@email", guestOne.getEmail());
        Assertions.assertEquals("(77) 7 7777-7777", guestOne.getPhone());

        Guest guestTwo = foundGuests.get(1);
        Assertions.assertEquals("John Doe", guestTwo.getName());
        Assertions.assertEquals("email.john.doe@email", guestTwo.getEmail());
        Assertions.assertEquals("(99) 9 9999-9999", guestTwo.getPhone());
    }
}
