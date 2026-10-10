
package com.lostfound.service;

import com.lostfound.dto.item.FoundItemRequest;
import com.lostfound.dto.item.FoundItemResponse;
import com.lostfound.entity.Category;
import com.lostfound.entity.FoundItem;
import com.lostfound.entity.User;
import com.lostfound.repository.CategoryRepository;
import com.lostfound.repository.FoundItemRepository;
import com.lostfound.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FoundItemService {

    private final FoundItemRepository foundItemRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public FoundItemService(
            FoundItemRepository foundItemRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        this.foundItemRepository = foundItemRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FoundItemResponse createFoundItem(
            String email, FoundItemRequest request) {

        User user = getUser(email);
        Category category = getActiveCategory(request.getCategoryId());

        FoundItem item = new FoundItem();
        item.setTitle(request.getTitle().trim());
        item.setDescription(request.getDescription().trim());
        item.setLocation(request.getLocation().trim());
        item.setFoundDate(request.getFoundDate());
        item.setUser(user);
        item.setCategory(category);
        item.setStatus("ACTIVE");

        return toResponse(foundItemRepository.save(item));
    }

    public List<FoundItemResponse> getActiveFoundItems() {
        return foundItemRepository
                .findByStatusIgnoreCaseOrderByCreatedAtDesc("FOUND")
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<FoundItemResponse> getMyFoundItems(String email) {
        User user = getUser(email);

        return foundItemRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FoundItemResponse getFoundItemById(Long id) {
        return toResponse(getFoundItem(id));
    }

    @Transactional
    public FoundItemResponse updateFoundItem(
            String email, Long id, FoundItemRequest request) {

        User user = getUser(email);
        FoundItem item = getFoundItem(id);

        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You can update only your own found items");
        }

        if (!"FOUND".equalsIgnoreCase(item.getStatus())) {
            throw new IllegalArgumentException(
                    "Only active found items can be updated");
        }

        Category category = getActiveCategory(request.getCategoryId());

        item.setTitle(request.getTitle().trim());
        item.setDescription(request.getDescription().trim());
        item.setLocation(request.getLocation().trim());
        item.setFoundDate(request.getFoundDate());
        item.setCategory(category);

        return toResponse(foundItemRepository.save(item));
    }

    @Transactional
    public void closeFoundItem(String email, Long id) {
        User user = getUser(email);
        FoundItem item = getFoundItem(id);

        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You can close only your own found items");
        }

        item.setStatus("CLOSED");
        foundItemRepository.save(item);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Category getActiveCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found"));

        if (!category.isActive()) {
            throw new IllegalArgumentException("Category is inactive");
        }

        return category;
    }

    private FoundItem getFoundItem(Long id) {
        return foundItemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Found item not found"));
    }

    private FoundItemResponse toResponse(FoundItem item) {
        FoundItemResponse response = new FoundItemResponse();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setDescription(item.getDescription());
        response.setLocation(item.getLocation());
        response.setFoundDate(item.getFoundDate());
        response.setStatus(item.getStatus());
        response.setUserId(item.getUser().getId());
        response.setUserName(item.getUser().getName());
        response.setCategoryId(item.getCategory().getId());
        response.setCategoryName(item.getCategory().getName());
        response.setCreatedAt(item.getCreatedAt());
        return response;
    }
}
