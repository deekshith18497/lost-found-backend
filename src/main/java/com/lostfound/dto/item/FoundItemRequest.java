
package com.lostfound.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class FoundItemRequest {

    @NotBlank(message = "Item title is required")
    @Size(min = 3, max = 150,
            message = "Title must contain 3 to 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000,
            message = "Description must contain 10 to 2000 characters")
    private String description;

    @NotBlank(message = "Found location is required")
    @Size(min = 3, max = 200,
            message = "Location must contain 3 to 200 characters")
    private String location;

    @NotNull(message = "Found date is required")
    @PastOrPresent(message = "Found date cannot be in the future")
    private LocalDateTime foundDate;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getFoundDate() { return foundDate; }
    public void setFoundDate(LocalDateTime foundDate) {
        this.foundDate = foundDate;
    }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
