package com.example.persistencedemo.dao;

import com.example.persistencedemo.domian.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: 苏敏
 * @date: 2020/6/27 21:08
 */
public interface DomainDao extends JpaRepository<Domain,String> {
}
