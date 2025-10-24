package com.github.gomestkd.eventmanagement.unittests.services;

import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.exception.BadRequestException;
import com.github.gomestkd.eventmanagement.exception.ResourceNotFoundException;
import com.github.gomestkd.eventmanagement.file.exporter.contract.ItemExporter;
import com.github.gomestkd.eventmanagement.file.exporter.factory.FileExporterFactory;
import com.github.gomestkd.eventmanagement.file.importer.contract.ItemImporter;
import com.github.gomestkd.eventmanagement.file.importer.factory.FileImporterFactory;
import com.github.gomestkd.eventmanagement.model.Item;
import com.github.gomestkd.eventmanagement.repositories.ItemRepository;
import com.github.gomestkd.eventmanagement.services.ItemService;
import com.github.gomestkd.eventmanagement.unittests.mapper.mocks.MockItem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    MockItem input;

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PagedResourcesAssembler<ItemDTO> assembler;

    @Mock
    private FileImporterFactory fileImporterFactory;

    @Mock
    private FileExporterFactory fileExporterFactory;

    @BeforeEach
    void setUp() {
        input = new MockItem();
    }

    @Test
    @DisplayName("findAll: Should return a paginated list of items")
    void testFindAll() {
        try {
            // Setup
            List<Item> entityList = input.mockEntityList();
            Page<Item> page = new PageImpl<>(entityList, PageRequest.of(0, 10), entityList.size());
            when(itemRepository.findAll(any(Pageable.class))).thenReturn(page);

            // CORRECTED MOCK: The assembler now expects only one argument (a Page)
            when(assembler.toModel(any(Page.class))).thenAnswer(invocation -> {
                Page<ItemDTO> dtoPage = invocation.getArgument(0);
                return PagedModel.of(dtoPage.getContent().stream().map(EntityModel::of).toList(), new PagedModel.PageMetadata(dtoPage.getSize(), dtoPage.getNumber(), dtoPage.getTotalElements()));
            });

            // Execution
            PagedModel<EntityModel<ItemDTO>> result = itemService.findAll(PageRequest.of(0, 10));

            // Assertions
            assertNotNull(result, "Result should not be null");
            assertEquals(14, result.getContent().size(), "Should return 14 items");
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during findAll test", e);
        }
    }

    @Test
    @DisplayName("findById: Should return an item when ID exists")
    void testFindById_whenIdExists() {
        try {
            // Setup
            Item entity = input.mockEntity(1);
            when(itemRepository.findById(1L)).thenReturn(Optional.of(entity));

            // Execution
            ItemDTO result = itemService.findById(1L);

            // Assertions
            assertNotNull(result, "Result should not be null");
            assertEquals(1L, result.getId(), "ID should match");
            assertEquals("Name: 1", result.getName(), "Name should match");
            assertTrue(result.getLinks().hasLink("self"), "Should have a self link");
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during findById test", e);
        }
    }

    @Test
    @DisplayName("findByName: Should return a paginated list of items matching the name")
    void testFindByName() {
        try {
            // Setup
            List<Item> entityList = List.of(input.mockEntity(1));
            Page<Item> page = new PageImpl<>(entityList, PageRequest.of(0, 1), 1);
            when(itemRepository.findItemsByName(anyString(), any(Pageable.class))).thenReturn(page);
            when(assembler.toModel(any(Page.class))).thenAnswer(invocation -> PagedModel.of(List.of(EntityModel.of(input.mockDTO(1))), new PagedModel.PageMetadata(1, 0, 1)));

            // Execution
            PagedModel<EntityModel<ItemDTO>> result = itemService.findByName("some name", PageRequest.of(0, 1));

            // Assertions
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during findByName test", e);
        }
    }

    @Test
    @DisplayName("findByDescription: Should return a paginated list of items matching the description")
    void testFindByDescription() {
        try {
            // Setup
            Page<Item> page = new PageImpl<>(List.of(input.mockEntity(1)));
            when(itemRepository.findItemsByDescription(anyString(), any(Pageable.class))).thenReturn(page);
            when(assembler.toModel(any(Page.class))).thenAnswer(invocation -> PagedModel.of(List.of(EntityModel.of(input.mockDTO(1))), new PagedModel.PageMetadata(1, 0, 1)));

            // Execution
            PagedModel<EntityModel<ItemDTO>> result = itemService.findByDescription("some desc", PageRequest.of(0, 1));

            // Assertions
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during findByDescription test", e);
        }
    }

    @Test
    @DisplayName("findByPriceRange: Should return a paginated list of items in the price range")
    void testFindByPriceRange() {
        try {
            // Setup
            Page<Item> page = new PageImpl<>(List.of(input.mockEntity(1)));
            when(itemRepository.findItemsByPriceRange(anyDouble(), anyDouble(), any(Pageable.class))).thenReturn(page);
            when(assembler.toModel(any(Page.class))).thenAnswer(invocation -> PagedModel.of(List.of(EntityModel.of(input.mockDTO(1))), new PagedModel.PageMetadata(1, 0, 1)));

            // Execution
            PagedModel<EntityModel<ItemDTO>> result = itemService.findByPriceRange(1.0, 10.0, PageRequest.of(0, 1));

            // Assertions
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during findByPriceRange test", e);
        }
    }

    @Test
    @Disabled("PDF export not implemented yet")
    @DisplayName("exportPage: Should return a resource file")
    void testExportPage() {
        try {
            // Setup
            Page<Item> page = new PageImpl<>(input.mockEntityList());
            ItemExporter mockedExporter = mock(ItemExporter.class);
            Resource mockedResource = mock(Resource.class);

            when(itemRepository.findAll(any(Pageable.class))).thenReturn(page);
            when(fileExporterFactory.getItemExporter(anyString())).thenReturn(mockedExporter);
            when(mockedExporter.exportItems(anySet())).thenReturn(mockedResource);

            // Execution
            Resource result = itemService.exportPage(PageRequest.of(0, 10), "application/pdf");

            // Assertions
            assertNotNull(result);
            verify(fileExporterFactory, times(1)).getItemExporter("application/pdf");
            verify(mockedExporter, times(1)).exportItems(anySet());
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during exportPage test", e);
        }
    }

    @Test
    @Disabled("PDF export not implemented yet")
    @DisplayName("exportItem: Should return a resource file for a single item")
    void testExportItem() {
        try {
            // Setup
            Item entity = input.mockEntity(1);
            ItemExporter mockedExporter = mock(ItemExporter.class);
            Resource mockedResource = mock(Resource.class);

            when(itemRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(fileExporterFactory.getItemExporter(anyString())).thenReturn(mockedExporter);
            when(mockedExporter.exportItem(any(ItemDTO.class))).thenReturn(mockedResource);

            // Execution
            Resource result = itemService.exportItem(1L, "application/pdf");

            // Assertions
            assertNotNull(result);
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during exportItem test", e);
        }
    }

    @Test
    @DisplayName("massCreation: Should create items from file")
    void testMassCreation() {
        try {
            // Setup
            MockMultipartFile file = new MockMultipartFile("file", "items.csv", "text/csv", "name,description,price\nItemA,DescA,10.0".getBytes());
            ItemImporter mockedImporter = mock(ItemImporter.class);

            when(fileImporterFactory.getItemImporter("items.csv")).thenReturn(mockedImporter);
            when(mockedImporter.importItems(any())).thenReturn(Set.of(input.mockDTO(1)));
            when(itemRepository.save(any(Item.class))).thenReturn(input.mockEntity(1));

            // Execution
            Set<ItemDTO> result = itemService.massCreation(file);

            // Assertions
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(itemRepository, times(1)).save(any(Item.class));
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during massCreation test", e);
        }
    }

    @Test
    @DisplayName("massCreation: Should throw BadRequestException for empty file")
    void testMassCreation_whenFileIsEmpty() {
        // Setup
        MockMultipartFile file = new MockMultipartFile("file", "items.csv", "text/csv", new byte[0]);

        // Execution & Assertion
        assertThrows(BadRequestException.class, () -> itemService.massCreation(file));
    }

    @Test
    @DisplayName("findById: Should throw ResourceNotFoundException when ID does not exist")
    void testFindById_whenIdDoesNotExist() {
        // Setup
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Execution & Assertion
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            itemService.findById(99L);
        });

        assertEquals("No records found for this ID!", exception.getMessage());
    }

    @Test
    @DisplayName("create: Should successfully create and return a new item")
    void testCreate() {
        try {
            // Setup
            Item entity = input.mockEntity(1);
            Item persisted = entity;
            persisted.setId(1L);
            ItemDTO dto = input.mockDTO(1);

            when(itemRepository.save(any(Item.class))).thenReturn(persisted);

            // Execution
            ItemDTO result = itemService.create(dto);

            // Assertions
            assertNotNull(result, "Result should not be null");
            assertEquals(1L, result.getId(), "ID should be assigned");
            assertEquals("Name: 1", result.getName(), "Name should match");
            assertTrue(result.getLinks().hasLink("self"), "Should have a self link");
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during create test", e);
        }
    }

    @Test
    @DisplayName("create: Should throw BadRequestException when DTO is null")
    void testCreate_whenDtoIsNull() {
        // CORRECTED EXCEPTION: Expect BadRequestException, not IllegalArgumentException
        Exception exception = assertThrows(BadRequestException.class, () -> {
            itemService.create(null);
        });
        assertEquals("ItemDTO cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("update: Should successfully update and return the item")
    void testUpdate_whenDataIsValid() {
        try {
            // Setup
            Item entity = input.mockEntity(1);
            entity.setId(1L);
            Item updatedEntity = entity;
            updatedEntity.setName("Updated Name");
            ItemDTO dto = input.mockDTO(1);
            dto.setName("Updated Name");

            when(itemRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(itemRepository.save(any(Item.class))).thenReturn(updatedEntity);

            // Execution
            ItemDTO result = itemService.update(dto);

            // Assertions
            assertNotNull(result, "Result should not be null");
            assertEquals(1L, result.getId(), "ID should be the same");
            assertEquals("Updated Name", result.getName(), "Name should be updated");
            assertTrue(result.getLinks().hasLink("self"), "Should have a self link");
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during update test", e);
        }
    }

    @Test
    @DisplayName("update: Should throw BadRequestException when DTO is null")
    void testUpdate_whenDtoIsNull() {
        // CORRECTED EXCEPTION: Expect BadRequestException, not IllegalArgumentException
        Exception exception = assertThrows(BadRequestException.class, () -> {
            itemService.update(null);
        });
        assertEquals("ItemDTO cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("update: Should throw ResourceNotFoundException when ID does not exist")
    void testUpdate_whenIdDoesNotExist() {
        // Setup
        ItemDTO dto = input.mockDTO(1);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Execution & Assertion
        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.update(dto);
        });
    }

    @Test
    @DisplayName("delete: Should successfully delete an item when ID exists")
    void testDelete_whenIdExists() {
        // Setup
        Item entity = input.mockEntity(1);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(itemRepository).delete(entity);

        // Execution
        itemService.delete(1L);

        // Assertion
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).delete(entity);
    }

    @Test
    @DisplayName("delete: Should throw ResourceNotFoundException when ID does not exist")
    void testDelete_whenIdDoesNotExist() {
        // Setup
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Execution & Assertion
        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.delete(99L);
        });
    }
}

