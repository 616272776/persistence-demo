package com.example.persistencedemo.domian;

import lombok.Data;

import java.util.List;

/**
 * @author: 苏敏
 * @date: 2020/6/24 15:36
 */
@Data
public class Student {
    private String studentId;
    private String studentName;
    private List<Course> courseList;
}
