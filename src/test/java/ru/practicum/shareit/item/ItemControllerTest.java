package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.constants.HeaderConstants;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.DataTest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createItem() throws Exception {
        ItemDto itemDto = DataTest.testItemDto1();

        when(itemService.createItem(any(ItemDto.class), eq(DataTest.userId)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        verify(itemService, times(1)).createItem(any(ItemDto.class), eq(DataTest.userId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = DataTest.testCommentDto();

        when(itemService.createComment(eq(DataTest.userId), any(CommentDto.class), eq(DataTest.itemId)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/" + DataTest.itemId + "/comment")
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));

        verify(itemService, times(1))
                .createComment(eq(DataTest.userId), any(CommentDto.class), eq(DataTest.itemId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItem() throws Exception {
        ItemDto itemDto = DataTest.testItemDto1();
        itemDto.setName("Перчатки Школы Медведя");
        itemDto.setDescription("Мастерские перчатки");

        when(itemService.updateItem(any(ItemDto.class), eq(DataTest.itemId), eq(DataTest.userId)))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/" + DataTest.itemId)
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        verify(itemService, times(1))
                .updateItem(any(ItemDto.class), eq(DataTest.itemId), eq(DataTest.userId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getById() throws Exception {
        ItemDto itemDto = DataTest.testItemDto1();

        when(itemService.getById(eq(itemDto.getId()), eq(DataTest.userId)))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/" + itemDto.getId())
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        verify(itemService, times(1)).getById(eq(itemDto.getId()), eq(DataTest.userId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getAllByOwner() throws Exception {
        ItemDto itemDto1 = DataTest.testItemDto1();
        ItemDto itemDto2 = DataTest.testItemDto2();
        List<ItemDto> itemDtoList = List.of(itemDto1, itemDto2);

        when(itemService.getAllByOwner(eq(DataTest.userId), any(Pageable.class)))
                .thenReturn(itemDtoList);

        mockMvc.perform(get("/items")
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto1.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$[0].available")
                        .value(itemDto1.getAvailable()))
                .andExpect(jsonPath("$[1].id").value(itemDto2.getId()))
                .andExpect(jsonPath("$[1].name").value(itemDto2.getName()))
                .andExpect(jsonPath("$[1].description").value(itemDto2.getDescription()))
                .andExpect(jsonPath("$[1].available")
                        .value(itemDto2.getAvailable()));

        verify(itemService, times(1)).getAllByOwner(eq(DataTest.userId), any(Pageable.class));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getBySearch() throws Exception {
        ItemDto itemDto1 = DataTest.testItemDto1();
        ItemDto itemDto2 = DataTest.testItemDto2();
        List<ItemDto> itemDtoList = List.of(itemDto1, itemDto2);

        when(itemService.getBySearch(anyString(), any(Pageable.class)))
                .thenReturn(itemDtoList);

        mockMvc.perform(get("/items/search")
                        .param("text", "меч"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto1.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$[0].available")
                        .value(itemDto1.getAvailable()))
                .andExpect(jsonPath("$[1].id").value(itemDto2.getId()))
                .andExpect(jsonPath("$[1].name").value(itemDto2.getName()))
                .andExpect(jsonPath("$[1].description").value(itemDto2.getDescription()))
                .andExpect(jsonPath("$[1].available")
                        .value(itemDto2.getAvailable()));

        verify(itemService, times(1)).getBySearch(anyString(),any(Pageable.class));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete("/items/" + DataTest.itemId))
                .andExpect(status().isOk());
        verify(itemService, times(1)).deleteById(DataTest.itemId);
        verifyNoMoreInteractions(itemService);
    }
}