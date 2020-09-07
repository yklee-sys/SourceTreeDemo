package com.aaa.yk.es.controller;

import com.aaa.yk.es.utils.ESUtil;
import org.elasticsearch.index.query.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Param
 * @ClassName SearchController
 * @Description 创建索引
 * @Author yk
 * @Date 2020/5/5 0005 19:23
 * @Return
 **/
@RestController
public class SearchController {
    @RequestMapping("/createIndex")
    public Map<String, Object> createIndex() {
        return ESUtil.createIndex("test_index20");
    }
    /**
     * @Description :
        添加一条数据
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     * @author : yk
     * @date : 2020/05/05 19:49
     */
    @RequestMapping("/addData")
    public Map<String, Object> addData() {
        Map<String, Object> mapObj = new HashMap<String, Object>();
        mapObj.put("username","zhang-san-3");
        mapObj.put("password",123456);
        mapObj.put("age",11);
        return ESUtil.addData(mapObj,"test_index3","test_type1","42");
    }
    /**
     * @Description :
            查询所有数据
            当查询所有数据时，高亮显示会报错空指针
            当查询所有数据时，这个时候时没有关键字匹配的，相当于select * from user 没有 where username = xxx
            只能高亮显示关键字，如果没有关键字，则高亮显示直接报错
     * @return : java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @author : yk
     * @date : 2020/05/05 20:02
     */

    @RequestMapping("/selectAll")
    public List<Map<String,Object>> selectAll(){
        /*1.创建QueryBuilder对象*/
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        /*2.创建所要搜索的条件（查询所有的数据）*/
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        /*3.把搜索条件放入到BoolQueryBuilder对象中*/
        BoolQueryBuilder must = boolQueryBuilder.must(matchAllQueryBuilder);
        /*4.返回*/
        return ESUtil.selectAllData("test_index3","test_type1",must,1,null,null,"username");
    }
    /**
     * @Description :
        模糊查询
        在es中会默认如果单词之间没有连接符就会把这个词默认成一个词
        如果需要模糊匹配分词查询，必须要使用链接符（_,-, ,=,...）
     * @return : java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @author : yk
     * @date : 2020/05/05 20:06
     */
    @RequestMapping("/selectAllLike")
    public List<Map<String,Object>> selectAllLike(){
        /*1.创建QueryBuilder对象*/
        BoolQueryBuilder boolQueryBuilder =QueryBuilders.boolQuery();
        /*
        2.创建条件查询
        matchAllQuery():查询所有
        matchPhraseQuery():实现模糊查询
                            第一个参数代表了条件是哪一个字段 select * from user where username like "%zhangsan%";
                            第二个参数表示的是模糊查询的值
        * */
        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("username", "zhang");
        /*3.把条件放入BoolQueryBuilder对象中*/
        BoolQueryBuilder must = boolQueryBuilder.must(matchPhraseQueryBuilder);
        /*4.返回*/
        return ESUtil.selectAllData("test_index3","test_type1",must,50,null,null,"username");
    }
}