package com.example.persistencedemo.domian;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author: 苏敏
 * @date: 2020/6/27 21:00
 */
@Data
@Table
@Entity
public class Domain {
    /**
     * 领域对象Id
     */
    @Id
    @Column(length = 32)
    private String domainId;

    private String clazz;

    private String fieldName;

    @Column(length = 32)
    private String persistenceId;


}
