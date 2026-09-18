package edu.ku.book_api.model;

public record CourseInput(
        String code,
        String title,
        int credits
) {
}