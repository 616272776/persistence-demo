package com.example.persistencedemo.domian;

import com.example.persistencedemo.enums.PersistenceEnum;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author: 苏敏
 * @date: 2020/6/27 20:19
 */
@Data
@Table
@Entity
public class Persistence {

    /**
     * 持久对象Id
     */
    @Id

    @Column(length = 32)
    private String persistenceId;
    /**
     * 所属层级
     */
    private Long level;

    /**
     * 持久值类型
     */
    private PersistenceEnum persistenceEnum;

    /**
     * 持久字段名
     */
    private String persistenceName;

    /**
     * 值id地址
     */
    @Column(length = 32)
    private String valueAddress;
    /**
     * 子持久集合
     */
    private String childrenPersistence;
}
