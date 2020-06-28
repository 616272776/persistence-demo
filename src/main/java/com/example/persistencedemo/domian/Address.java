package com.example.persistencedemo.domian;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author: 苏敏
 * @date: 2020/6/28 9:38
 * 地址
 */
@Data
@Table
@Entity
public class Address {
    /**
     * 地址Id
     */
    @Id
    @Column(length = 32)
    private String addressId;



}
