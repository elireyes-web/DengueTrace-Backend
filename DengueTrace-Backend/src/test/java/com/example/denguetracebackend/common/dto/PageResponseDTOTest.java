package com.example.denguetracebackend.common.dto;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResponseDTOTest {

    @Test
    void mapsRealPaginationMetadata() {
        var page = new PageImpl<>(List.of("Lima", "Miraflores"), PageRequest.of(1, 2), 7);

        PageResponseDTO<String> result = PageResponseDTO.from(page);

        assertEquals(List.of("Lima", "Miraflores"), result.content());
        assertEquals(1, result.page());
        assertEquals(2, result.size());
        assertEquals(7, result.totalElements());
        assertEquals(4, result.totalPages());
        assertFalse(result.last());
    }

    @Test
    void mapsEmptyFirstPage() {
        var page = new PageImpl<>(List.<String>of(), PageRequest.of(0, 10), 0);
        var result = PageResponseDTO.from(page);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
        assertTrue(result.last());
    }
}
