package com.example.persistencedemo.service;

import com.example.persistencedemo.dao.DomainDao;
import com.example.persistencedemo.dao.PersistenceDao;
import com.example.persistencedemo.dao.ValueDao;
import com.example.persistencedemo.domian.Domain;
import com.example.persistencedemo.domian.Persistence;
import com.example.persistencedemo.domian.Value;
import com.example.persistencedemo.enums.PersistenceEnum;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * @author: 苏敏
 * @date: 2020/6/28 11:24
 */
@Service
public class DomainService {
    @Autowired
    private DomainDao domainDao;

    @Autowired
    private PersistenceDao persistenceDao;

    @Autowired
    private ValueDao valueDao;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private String domainId = "";

    private String json = "{\"ClassId\":\"123\",\"ClassName\":\"一班\",\"studentList\":[{\"studentId\":\"s1\",\"studentName\":\"张三\",\"courseList\":[{\"courseId\":\"c1\",\"courseName\":\"语文\",\"scoreList\":[{\"scoreId\":\"s1\",\"scoreNumber\":3},{\"scoreId\":\"s2\",\"scoreNumber\":5}]},{\"courseId\":\"c2\",\"courseName\":\"数学\",\"scoreList\":[{\"scoreId\":\"s1\",\"scoreNumber\":5},{\"scoreId\":\"s2\",\"scoreNumber\":6}]}]},{\"studentId\":\"s1\",\"studentName\":\"李四\",\"courseList\":[{\"courseId\":\"c1\",\"courseName\":\"语文\",\"scoreList\":[{\"scoreId\":\"s1\",\"scoreNumber\":55},{\"scoreId\":\"s2\",\"scoreNumber\":77}]},{\"courseId\":\"c2\",\"courseName\":\"数学\",\"scoreList\":[{\"scoreId\":\"s1\",\"scoreNumber\":44},{\"scoreId\":\"s2\",\"scoreNumber\":33}]}]}]}";

    /**
     * 保持领域对象
     */
    void save(Domain domain, JsonNode node) {
        // 1.以领域对象名作为当前持久对象
        Persistence persistence = saveDomainRoot(domain);
        // 2.更新领域对象的持久化信息
        Domain result = domainDao.findById(domain.getDomainId()).get();
        result.setPersistenceId(persistence.getPersistenceId());
        domainDao.save(result);

        // 2.开始对树状结构进行分析并保存数据，关系包含在持久对象表中
        ArrayNode jsonNodes = saveDomain(persistence, node);
        persistence.setChildrenPersistence(jsonNodes.toString());
        persistenceDao.save(persistence);
    }

    /**
     * 保存以domain的字段名作为字段值进行保存
     *
     * @param domain
     */
    private Persistence saveDomainRoot(Domain domain) {
        Persistence persistence = new Persistence();
        persistence.setPersistenceId(genUUID());
        persistence.setLevel(0L);
        persistence.setPersistenceEnum(PersistenceEnum.OBJECT);
        persistence.setPersistenceName(domain.getFieldName());
        persistence.setValueAddress(null);
        persistence.setChildrenPersistence(null);
        return persistence;
    }

    /**
     * 将领域对象的结构存储完成
     */
    public ArrayNode saveDomain(Persistence persistence, JsonNode node) {
        // todo 完成领域对象和持久对象的关系存储
        // 遍历当前的节点的子对象，查看类型是值、对象还是集合。


        // 3.对返回的节点进行初始化
        ArrayNode arrayNode = objectMapper.createArrayNode();

        if(node.isValueNode()){
            Persistence childenPersistence = new Persistence();
            childenPersistence.setPersistenceId(genUUID());
            childenPersistence.setLevel(persistence.getLevel() + 1);
            childenPersistence.setPersistenceEnum(PersistenceEnum.VALUE);

            Value value = new Value();
            value.setValueId(genUUID());
            value.setValue(node.toString());
            valueDao.save(value);
            childenPersistence.setValueAddress(value.getValueId());
            persistenceDao.save(childenPersistence);
            arrayNode.add(childenPersistence.getPersistenceId());
            return arrayNode;
        }
        // todo 中断没有做合并




        Iterator<Map.Entry<String, JsonNode>> it = node.fields();
        while (it.hasNext()) {
            // 1.新建持久化对象
            Persistence childenPersistence = new Persistence();

            // 2.对持久化对象进行初始化赋值
            childenPersistence.setPersistenceId(genUUID());
            childenPersistence.setLevel(persistence.getLevel() + 1);

            Map.Entry<String, JsonNode> entry = it.next();
            // 判断是值对象
            if (entry.getValue().isValueNode()) {
                childenPersistence.setPersistenceEnum(PersistenceEnum.VALUE);
                childenPersistence.setPersistenceName(entry.getKey());

                // 对值对象进行保存
                Value value = new Value();
                value.setValueId(genUUID());
                value.setValue(entry.getValue().toString());
                valueDao.save(value);
                childenPersistence.setValueAddress(value.getValueId());

            }
            // 判断是对象
            if (entry.getValue().isObject()) {
                childenPersistence.setPersistenceEnum(PersistenceEnum.OBJECT);
                childenPersistence.setPersistenceName(entry.getKey());

                Iterator<JsonNode> arrayIt = entry.getValue().iterator();
                ArrayNode objectChildrenNode = objectMapper.createArrayNode();
                while (arrayIt.hasNext()) {
                    ArrayNode jsonNodes = saveDomain(childenPersistence, arrayIt.next());
                    objectChildrenNode.add(jsonNodes.toString());
                }
                childenPersistence.setChildrenPersistence(objectChildrenNode.toString());

            }
            // 判断是数组对象
            if (entry.getValue().isArray()) {
                childenPersistence.setPersistenceEnum(PersistenceEnum.ARRAY);
                childenPersistence.setPersistenceName(entry.getKey());
                Iterator<JsonNode> arrayIt = entry.getValue().iterator();
                // todo
                ArrayNode arrayChildrenNode = objectMapper.createArrayNode();
                while (arrayIt.hasNext()) {
                    ArrayNode jsonNodes = saveDomain(childenPersistence, arrayIt.next());
                    arrayChildrenNode.add(jsonNodes.toString());
                }
                childenPersistence.setChildrenPersistence(arrayChildrenNode.toString());
            }

            //todo 保持持久化类
            persistenceDao.save(childenPersistence);
            arrayNode.add(childenPersistence.getPersistenceId());
        }


        return arrayNode;

    }


    private static String genUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return str.replace("-", "");
    }


}
