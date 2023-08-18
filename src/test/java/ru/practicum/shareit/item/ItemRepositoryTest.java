package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.DataTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void findAllBySearch() {
        User user = DataTest.testUser1();
        Item item1 = DataTest.testItem1();
        Item item2 = DataTest.testItem2();
        Item item3 = DataTest.testItem3();
        Item item4 = DataTest.testItem4();
        Item item5 = DataTest.testItem5();
        userRepository.save(user);
        itemRepository.saveAll(List.of(item1, item2, item3, item4, item5));

        Pageable pageable = PageRequest.of(0, 10);

        assertThat(itemRepository.findAllBySearch("ДОСпех", pageable)).hasSize(2);
        assertThat(itemRepository.findAllBySearch("ГРОССМЕЙСТЕРСКИЙ", pageable)).hasSize(1);
        assertThat(itemRepository.findAllBySearch("штаНы", pageable)).hasSize(2);
        assertThat(itemRepository.findAllBySearch("махакамские штаНы", pageable)).hasSize(1);
        assertThat(itemRepository.findAllBySearch("шКолы ВоЛка", pageable)).hasSize(3);
        assertThat(itemRepository.findAllBySearch("меч", pageable)).hasSize(1);
    }
}