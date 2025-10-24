package com.github.gomestkd.eventmanagement.repositories;

import com.github.gomestkd.eventmanagement.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Item> findItemsByName(@Param("name") String description, Pageable pageable);

    //TODO: add find by description
    @Query("SELECT i FROM Item i WHERE LOWER(i.description) LIKE LOWER(CONCAT('%',:description,'%'))")
    Page<Item> findItemsByDescription(@Param("description") String description, Pageable pageable);

    //TODO:add find by price range
    @Query("SELECT i FROM Item i WHERE i.price BETWEEN :minPrice AND :maxPrice")
    Page<Item> findItemsByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);

    //TODO: add find by name and description
    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(i.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    Page<Item> findItemsByNameAndDescription(@Param("name") String name, @Param("description") String description, Pageable pageable);
}
