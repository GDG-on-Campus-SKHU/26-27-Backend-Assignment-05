package com.group.oauth.domain.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentController {

    @GetMapping("/students")
    public List<Student> getStudents() {

        List<Student> students = List.of(
                new Student(1L, "김석환", 24),
                new Student(2L, "김수정", 21),
                new Student(3L, "김서령", 20),
                new Student(4L, "김현우", 20),
                new Student(5L, "노강민", 25),
                new Student(6L, "박연지", 24),
                new Student(7L, "백민재", 24),
                new Student(8L, "안정민", 23),
                new Student(9L, "안령서", 22),
                new Student(10L, "이길수", 24),
                new Student(11L, "이삭", 24),
                new Student(12L, "이승호", 22),
                new Student(13L, "이우빈", 26)
        );

        return students.stream()
                .filter(student -> student.age() >= 23)
                .toList();
    }
}
