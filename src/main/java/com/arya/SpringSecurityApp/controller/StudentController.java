package com.arya.SpringSecurityApp.controller;


import com.arya.SpringSecurityApp.response.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.arya.SpringSecurityApp.model.Student;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
public class StudentController {
    List<Student> students=new ArrayList<>(List.of(
            new Student(1L,"Arya","Python"),
            new Student(2L,"Yadvendra","Java")
    ));

    private long nextId = 3L;

    @GetMapping("csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }


    @GetMapping("students")
    public GenericResponse<List<Student>> getStudents(){
        GenericResponse<List<Student>> response = new GenericResponse<>();
        response.setMessage("Student Details Retrieved successfully");
        response.setStatus("SUCCESS");
        response.setData(students);
        return response;
    }


    @PostMapping("add-student")
    public GenericResponse<Student> addStudent(@RequestBody Student student) {
        Student newStudent = new Student();
        newStudent.setId(nextId++);
        newStudent.setName(student.getName());
        newStudent.setTech(student.getTech());
        students.add(newStudent);

        GenericResponse<Student> response = new GenericResponse<>();
        response.setMessage("Student added successfully");
        response.setStatus("SUCCESS");
        response.setData(student);
        return response;
    }


    @DeleteMapping("students/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        // Finding the student with the given ID
        Optional<Student> studentOptional = students.stream()
                .filter(student -> student.getId().equals(id))
                .findFirst();
        if (studentOptional.isPresent()) {
            // Removing the student from the list
            students.remove(studentOptional.get());
            return ResponseEntity.ok("Student with ID " + id + " deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
