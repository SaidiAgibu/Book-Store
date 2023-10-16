package com.saidi.book_store.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDto {
    private String title;
    private String author;
    private byte[] fileContent;
    private String description;
    private String category;
    private Double price;
    private Integer quantityAvailable;
}
