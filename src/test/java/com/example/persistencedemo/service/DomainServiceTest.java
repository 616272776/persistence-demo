package com.example.persistencedemo.service;

import com.example.persistencedemo.dao.DomainDao;
import com.example.persistencedemo.dao.PersistenceDao;
import com.example.persistencedemo.dao.ValueDao;
import com.example.persistencedemo.domian.Clazz;
import com.example.persistencedemo.domian.Domain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

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
    String deepJson = "{\"ClassId\":\"123\",\"ClassName\":\"一班\",\"studentList\":[{\"studentId\":\"s1\",\"studentName\":\"张三\",\"courseList\":[{\"courseId\":\"c1\",\"courseName\":\"语文\",\"scoreList\":[{\"scoreId\":\"s1\",\"scoreNumber\":3},{\"scoreId\":\"s2\",\"scoreNumber\":5}]},{\"courseId\":\"c2\",\"courseName\":\"数学\",\"scoreList\":[{\"scoreId\":\"s1\",\"scoreNumber\":5},{\"scoreId\":\"s2\",\"scoreNumber\":6}]}]}]}";

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

    @Test
    void savedeepJson() throws JsonProcessingException {
        before();
        Domain domain = domainDao.findById("1").get();
        domainService.save(domain,objectMapper.readTree(deepJson));
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


    @Test
    void test() throws JsonProcessingException {
        String str = "[\"4dd4ae6ab9dd49929a8b3060301bcb45\",\"d8545ab71d874fe2b3ef010648a04250\",\"123\",\"123\",\"123\",\"123\"]";
        JsonNode jsonNode = objectMapper.readTree(str);



    }
}