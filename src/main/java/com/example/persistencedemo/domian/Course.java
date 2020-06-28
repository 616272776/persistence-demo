package com.example.persistencedemo.domian;

import lombok.Data;

import java.util.List;

/**
 * @author: 苏敏
 * @date: 2020/6/24 15:37
 */
@Data
public class Course {
    String courseId;
    String courseName;
    List<Score> scoreList;
}
