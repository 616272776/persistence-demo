package com.example.persistencedemo;

import com.example.persistencedemo.dao.DomainDao;
import com.example.persistencedemo.dao.PersistenceDao;
import com.example.persistencedemo.domian.Clazz;
import com.example.persistencedemo.domian.Domain;
import com.example.persistencedemo.domian.Persistence;
import com.example.persistencedemo.domian.Value;
import com.example.persistencedemo.enums.PersistenceEnum;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
class PersistenceDemoApplicationTests {

    @Autowired
    private DomainDao domainDao;

    @Autowired
    private PersistenceDao persistenceDao;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private String domainId = "";

    private String json = "{\"ClassId\":\"123\",\"ClassName\":\"一班\",\"studentList\":[{\"studentId\":\"s1\",\"studentName\":\"张三\",\"courseList\":[{\"courseId\":\"c1\",\"courseName\":\"语文\",\"scoreList\":[{\"scoreId\":\"s1\",\"scoreNumber\":3},{\"scoreId\":\"s2\",\"scoreNumber\":5}]},{\"courseId\":\"c2\",\"courseName\":\"数学\",\"scoreList\":[{\"scoreId\":\"s1\",\"scoreNumber\":5},{\"scoreId\":\"s2\",\"scoreNumber\":6}]}]},{\"studentId\":\"s1\",\"studentName\":\"李四\",\"courseList\":[{\"courseId\":\"c1\",\"courseName\":\"语文\",\"scoreList\":[{\"scoreId\":\"s1\",\"scoreNumber\":55},{\"scoreId\":\"s2\",\"scoreNumber\":77}]},{\"courseId\":\"c2\",\"courseName\":\"数学\",\"scoreList\":[{\"scoreId\":\"s1\",\"scoreNumber\":44},{\"scoreId\":\"s2\",\"scoreNumber\":33}]}]}]}";

    /**
     * 保持领域对象
     */
    @Test
    void save(Domain domain, JsonNode node) {
        // 1.以领域对象名作为当前持久对象
        Persistence persistence = saveDomainRoot(domain);
        // 2.更新领域对象的持久化信息
        Domain result = domainDao.findById(domain.getDomainId()).get();
        result.setPersistenceId(persistence.getPersistenceId());
        domainDao.save(result);

        // 2.开始对树状结构进行分析并保存数据，关系包含在持久对象表中
        saveDomain(persistence, node);
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
     * @param node
     * @param list
     * @param level
     */
    public void jsonLeaf(JsonNode node, List<ObjectNode> list, int level) {
        // 如果是值类型直接返回
        if (node.isValueNode()) {
            return;
        }
        // 是数组类型遍历数组进行递归
        if (node.isArray()) {
            Iterator<JsonNode> it = node.iterator();
            while (it.hasNext()) {
                jsonLeaf(it.next(), list, level);
            }
        }
        // 是对象类型进行抽取
        if (node.isObject()) {

            ObjectNode objectNode = objectMapper.createObjectNode();

            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                // 是值对象就放到节点中,不是就进行遍历
                if (entry.getValue().isValueNode()) {
                    objectNode.put(entry.getKey(), entry.getValue());
                } else {
                    jsonLeaf(entry.getValue(), list, level + 1);
                }

            }
            objectNode.put("level", level);
            // 添加到集合中
            list.add(objectNode);
        }
    }

    /**
     * 将领域对象的结构存储完成
     */
    public ArrayNode saveDomain(Persistence persistence, JsonNode node) {
        // todo 完成领域对象和持久对象的关系存储
        // 遍历当前的节点的子对象，查看类型是值、对象还是集合。

        // 1.新建持久化对象
        Persistence childenPersistence = new Persistence();

        // 2.对持久化对象进行初始化赋值
        childenPersistence.setPersistenceId(genUUID());
        childenPersistence.setLevel(persistence.getLevel()+1);

        // 3.对返回的节点进行初始化
        ArrayNode arrayNode = objectMapper.createArrayNode();

        Iterator<Map.Entry<String, JsonNode>> it = node.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            // 判断是值对象
            if (entry.getValue().isValueNode()) {
                childenPersistence.setPersistenceEnum(PersistenceEnum.VALUE);
                childenPersistence.setPersistenceName(entry.getKey());

                //todo 这里还没有对值对象进行保存
                Value value = new Value();
                value.setValueId(genUUID());
                childenPersistence.setValueAddress(value.getValueId());
                arrayNode.add(value.getValueId());
            }
            // 判断是对象
            if (entry.getValue().isObject()) {
                childenPersistence.setPersistenceEnum(PersistenceEnum.OBJECT);
                childenPersistence.setPersistenceName(entry.getKey());

                Iterator<JsonNode> arrayIt = entry.getValue().iterator();
                while (arrayIt.hasNext()) {
                    saveDomain(childenPersistence,entry.getValue());
                }

            }
            // 判断是数组对象
            if (entry.getValue().isArray()) {
                childenPersistence.setPersistenceEnum(PersistenceEnum.ARRAY);
                childenPersistence.setPersistenceName(entry.getKey());


                Iterator<JsonNode> arrayIt = entry.getValue().iterator();
                while (arrayIt.hasNext()) {
                    saveDomain(childenPersistence,entry.getValue());
                }

//                childenPersistence.setChildrenPersistence();
            }
        }


        //todo 保持持久化类
        persistenceDao.save(childenPersistence);

        arrayNode.add(childenPersistence.getPersistenceId());
        return arrayNode;

    }


    public String genUUID() {
        return UUID.randomUUID().toString();
    }



    @Test
    void test(){
        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.add("awdrfaefwqa");

        ArrayNode arrayNode2 = objectMapper.createArrayNode();
        arrayNode2.add("342gsdgaesge");
        ArrayNode arrayNode4 = objectMapper.createArrayNode();
        arrayNode4.add("44141weawtawet");

        ArrayNode arrayNode3 = objectMapper.createArrayNode();
        arrayNode3.add(arrayNode);
        arrayNode3.add(arrayNode2);
        arrayNode3.add(arrayNode4);
        System.out.println(arrayNode3);



    }
}
