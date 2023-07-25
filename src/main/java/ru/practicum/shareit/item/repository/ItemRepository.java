package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findALLByOwnerIdOrderByIdAsc(long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "AND i.available = true")
    List<Item> findAllBySearch(String search);
}
