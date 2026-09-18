package edu.ku.book_api.controller;

import edu.ku.book_api.model.Course;
import edu.ku.book_api.model.CourseInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    // ✅ Mutable list now
    private final List<Course> courses = new ArrayList<>(List.of(
            new Course(1L, "CS101", "Introduction to Programming", 3, "Dr. Smith"),
            new Course(2L, "CS201", "Data Structures", 4, "Dr. Khan"),
            new Course(3L, "CS301", "Database Systems", 3, "Dr. Ahmed"),
            new Course(4L, "CS401", "Software Engineering", 3, "Dr. Lee"),
            new Course(5L, "CS501", "Distributed Systems", 4, "Dr. Martin")));

    // ✅ Auto-incrementing id (starts after the last seeded id)
    private final AtomicLong nextId = new AtomicLong(6);

    // ---------- GET all / filter ----------
    @GetMapping
    public List<Course> getAllCourses(
            @RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return courses;
        }
        String searchText = keyword.toLowerCase();
        return courses.stream()
                .filter(course -> course.code().toLowerCase().contains(searchText)
                        || course.title().toLowerCase().contains(searchText))
                .toList();
    }

    // ---------- GET by id ----------
    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(
            @PathVariable Long courseId) {
        return courses.stream()
                .filter(course -> course.id().equals(courseId))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ---------- POST create ----------
    @PostMapping
    public ResponseEntity<?> createCourse(
            @RequestBody CourseInput input) {
        // 1) Validation
        if (input.code() == null || input.code().isBlank()
                || input.title() == null || input.title().isBlank()
                || input.credits() <= 0) {
            return ResponseEntity.badRequest().body(
                    "Course code, title, and a positive credit value are required.");
        }

        // 2) Duplicate code check (case-insensitive)
        boolean duplicateCode = courses.stream()
                .anyMatch(course -> course.code().equalsIgnoreCase(input.code()));
        if (duplicateCode) {
            return ResponseEntity.status(409).body(
                    "A course with this code already exists.");
        }

        // 3) Build new course
        Course newCourse = new Course(
                nextId.getAndIncrement(),
                input.code().trim().toUpperCase(),
                input.title().trim(),
                input.credits(),
                "TBD" // instructor — added because your Course record has 5 fields
        );

        // 4) Add to in-memory store
        courses.add(newCourse);

        // 5) Return 201 Created with Location header
        URI location = URI.create("/api/v1/courses/" + newCourse.id());
        return ResponseEntity.created(location).body(newCourse);
    }
}