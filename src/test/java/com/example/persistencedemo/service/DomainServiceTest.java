package com.example.persistencedemo.service;

import com.example.persistencedemo.dao.DomainDao;
import com.example.persistencedemo.dao.PersistenceDao;
import com.example.persistencedemo.dao.ValueDao;
import com.example.persistencedemo.domian.Clazz;
import com.example.persistencedemo.domian.Domain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: 苏敏
 * @date: 2020/6/28 11:28
 */
@SpringBootTest
@RunWith(SpringRunner.class)
class DomainServiceTest {

    @Autowired
    private DomainService domainService;

    @Autowired
    private DomainDao domainDao;

    @Autowired
    private PersistenceDao persistenceDao;

    @Autowired
    private ValueDao valueDao;

    private final static ObjectMapper objectMapper = new ObjectMapper();
    String simpleJson = "{\"ClassId\":\"123\",\"ClassName\":\"一班\"}";
    String json = "{\"ClassId\":\"123\",\"ClassName\":\"一班\",\"studentList\":[1,2,3],\"object\":{\"name\":\"张三\",\"age\":15}}";
    String deepJson = "";

    @Test
    void saveSimpleJson() throws JsonProcessingException {
        before();
        Domain domain = domainDao.findById("1").get();
        domainService.save(domain,objectMapper.readTree(simpleJson));
        after();
    }

    @Test
    void saveJson() throws JsonProcessingException {
        before();
        Domain domain = domainDao.findById("1").get();
        domainService.save(domain,objectMapper.readTree(json));
        after();
    }


    @Before
    void before(){
        Domain domain = new Domain();
        domain.setDomainId("1");
        domain.setClazz(Clazz.class.toString());
        domain.setFieldName("clazz");

        domainDao.save(domain);
    }
    @After
    void after(){
        domainDao.deleteAll();
        persistenceDao.deleteAll();
        valueDao.deleteAll();
    }
}