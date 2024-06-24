package com.arya.SpringSecurityApp.controller;


import com.arya.SpringSecurityApp.response.GenericResponse;
import org.springframework.web.bind.annotation.*;
import com.arya.SpringSecurityApp.entity.Student;

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


    @DeleteMapping("student/{id}")
    public GenericResponse<String> deleteStudent(@PathVariable Long id) {
        Optional<Student> studentOptional = students.stream()
                .filter(student -> student.getId().equals(id))
                .findFirst();

        GenericResponse<String> response = new GenericResponse<>();

        if (studentOptional.isPresent()) {
            students.remove(studentOptional.get());
            response.setMessage("Student with ID " + id + " deleted successfully");
            response.setStatus("SUCCESS");
            response.setData("Deleted ID: " + id);
        }
        else {
            response.setMessage("Student with ID " + id + " not found");
            response.setStatus("FAILURE");
            response.setData("Deletion failed for ID: " + id);
        }
        return response;
    }


}
