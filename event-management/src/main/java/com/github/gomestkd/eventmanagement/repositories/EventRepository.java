package com.github.gomestkd.eventmanagement.repositories;

import com.github.gomestkd.eventmanagement.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(
            "SELECT e FROM Event e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))"
    )
    Page<Event> findEventsByName(@Param("name") String name, Pageable pageable);

    @Query(
        "SELECT e FROM Event e WHERE LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))"
    )
    Page<Event> findEventsByLocation(@Param("location") String location, Pageable pageable);

    @Query(
        "SELECT e FROM Event e WHERE LOWER(e.description) LIKE LOWER(CONCAT('%', :description, '%'))"
    )
    Page<Event> findEventsByDescription(@Param("description") String description, Pageable pageable);

    @Query(
        "SELECT e FROM Event e WHERE (e.startTime >= :startTime) AND (e.endTime <= :endTime)"
    )
    Page<Event> findEventsByTimeRange(
        @Param("startTime") java.time.LocalDateTime startTime,
        @Param("endTime") java.time.LocalDateTime endTime,
        Pageable pageable
    );
}
