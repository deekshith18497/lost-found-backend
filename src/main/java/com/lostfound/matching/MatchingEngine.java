
package com.lostfound.matching;

import com.lostfound.entity.FoundItem;
import com.lostfound.entity.LostItem;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MatchingEngine {

    private final DescriptionSimilarity descriptionSimilarity;

    public MatchingEngine(DescriptionSimilarity descriptionSimilarity) {
        this.descriptionSimilarity = descriptionSimilarity;
    }

    public double calculateSimilarity(
            LostItem lostItem, FoundItem foundItem) {

        double score = 0.0;

        String lostTitle = normalize(lostItem.getTitle());
        String foundTitle = normalize(foundItem.getTitle());

        String lostDescription = normalize(lostItem.getDescription());
        String foundDescription = normalize(foundItem.getDescription());

        String lostLocation = normalize(lostItem.getLocation());
        String foundLocation = normalize(foundItem.getLocation());

        // Title similarity: 40%
        score += descriptionSimilarity.calculateSimilarity(
                lostTitle, foundTitle) * 40;

        // Description similarity: 30%
        score += descriptionSimilarity.calculateSimilarity(
                lostDescription, foundDescription) * 30;

        // Category match: 20%
        if (lostItem.getCategory() != null
                && foundItem.getCategory() != null
                && lostItem.getCategory().getId()
                        .equals(foundItem.getCategory().getId())) {
            score += 20;
        }

        // Location similarity: 10%
        score += descriptionSimilarity.calculateSimilarity(
                lostLocation, foundLocation) * 10;

        return Math.round(Math.min(score, 100.0) * 100.0) / 100.0;
    }

    public String explainMatch(
            LostItem lostItem, FoundItem foundItem) {

        List<String> reasons = new ArrayList<>();

        if (descriptionSimilarity.calculateSimilarity(
                normalize(lostItem.getTitle()),
                normalize(foundItem.getTitle())) >= 0.5) {
            reasons.add("Similar item titles");
        }

        if (descriptionSimilarity.calculateSimilarity(
                normalize(lostItem.getDescription()),
                normalize(foundItem.getDescription())) >= 0.4) {
            reasons.add("Similar item descriptions");
        }

        if (lostItem.getCategory() != null
                && foundItem.getCategory() != null
                && lostItem.getCategory().getId()
                        .equals(foundItem.getCategory().getId())) {
            reasons.add("Same item category");
        }

        if (descriptionSimilarity.calculateSimilarity(
                normalize(lostItem.getLocation()),
                normalize(foundItem.getLocation())) >= 0.5) {
            reasons.add("Similar locations");
        }

        if (reasons.isEmpty()) {
            return "Some item details may be similar";
        }

        return String.join(", ", reasons);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
