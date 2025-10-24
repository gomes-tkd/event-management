package com.github.gomestkd.eventmanagement.repositories;

import com.github.gomestkd.eventmanagement.model.Guest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    @Query("SELECT g FROM Guest g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Guest> findGuestsByName(@Param("name") String description, Pageable pageable);

    @Query("SELECT g FROM Guest g WHERE LOWER(g.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    Page<Guest> findGuestsByEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT g FROM Guest g WHERE LOWER(g.phone) LIKE LOWER(CONCAT('%', :phone, '%'))")
    Page<Guest> findGuestByPhone(@Param("phone") String phone, Pageable pageable);
}
