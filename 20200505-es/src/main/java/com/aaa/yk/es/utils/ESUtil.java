package com.aaa.yk.es.utils;

import com.aaa.yk.es.status.StatusEnum;
import com.mysql.jdbc.StringUtils;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @Param
 * @ClassName ESUtil
 * @Description es工具类
 * @Author yk
 * @Date 2020/5/5 0005 16:51
 * @Return
 **/
@Component
public class ESUtil {
    @Autowired
    private TransportClient transportClient;
    /**
     * @Description : 当spring容器进行初始化的时候就会调用init方法，然后把@Autowired所标识的对象赋值给静态对象
     * @param null
     * @return :
     * @author : yk
     * @date : 2020/05/05 16:57
     */
    private static TransportClient client;

    @PostConstruct
    public  void init(){
        client = this.transportClient;
    }
    /**
     * @Description : 定义map对象，统一返回
     * @param null
     * @return :
     * @author : yk
     * @date : 2020/05/05 17:13
     */
    public static Map<String,Object> resultMap = new HashMap<String, Object>();

    /**
     * @Description :
     *         因为该工具类中所有的方法都必须要是静态方法，静态方法只能使用静态变量，
     *         所以当使用@Autowired所注入的对象就不能在静态方法中使用
     *         可以通过@PostContruct注解来解决以上的问题
     * @param index
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     * @author : yk
     * @date : 2020/05/05 17:11
     * 创建es的index
     */
    public static Map<String,Object> createIndex(String index){
        /*
        * 1.先判断es中是否已经存在这个index
        * 规定：如果为true说明不存在，如果为false说明存在
        * */
        if (!isIndexExist(index)){
            resultMap.put("code", StatusEnum.EXIST.getCode());
            resultMap.put("msg", StatusEnum.EXIST.getMsg());
        }
            /*2. 在ES中创建Index*/
            CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(index).execute().actionGet();

            /*
             * 3.再次判断Index是否创建成功
             * createIndexResponse.isAcknowledged():返回值true/false true:成功 false:失败
             */
            if (createIndexResponse.isAcknowledged()){
                resultMap.put("code", StatusEnum.OPRATION_SUCCESS.getCode());
                resultMap.put("msg", StatusEnum.OPRATION_SUCCESS.getMsg());
            }else {
                resultMap.put("code", StatusEnum.OPRATION_FAILED.getCode());
                resultMap.put("msg", StatusEnum.OPRATION_FAILED.getMsg());
            }

        return resultMap;
    }


    /**
     * @Description :删除index
     * @param index
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     * @author : yk
     * @date : 2020/05/05 17:27
     */
    public static Map<String,Object> deleteIndex(String index){
        /*
         * 1.先判断es中是否已经存在这个index
         * 规定：如果为true说明不存在，如果为false说明存在
         * */
        if (!isIndexExist(index)){
            resultMap.put("code", StatusEnum.EXIST.getCode());
            resultMap.put("msg", StatusEnum.EXIST.getMsg());
        }else if (isIndexExist(index)){
            /*2. 在ES中删除Index*/
            DeleteIndexResponse deleteIndexResponse = client.admin().indices().prepareDelete(index).execute().actionGet();
            /*
             * 3.再次判断Index是否创建成功
             * deleteIndexResponse.isAcknowledged():返回值true/false true:成功 false:失败
             */
            if (deleteIndexResponse.isAcknowledged()) {
                resultMap.put("code", StatusEnum.OPRATION_SUCCESS.getCode());
                resultMap.put("msg", StatusEnum.OPRATION_SUCCESS.getMsg());
            }else {
                resultMap.put("code", StatusEnum.OPRATION_FAILED.getCode());
                resultMap.put("msg", StatusEnum.OPRATION_FAILED.getMsg());
            }
        }
        return resultMap;
    }

    /**
     * @Description :判断index是否存在
     * @param index
     * @return : boolean
     * @author : yk
     * @date : 2020/05/05 17:03
     */
    public static boolean isIndexExist(String index){
        IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet();
        return indicesExistsResponse.isExists();
    }
    /**
     * @Description :判断指定index下的type是否存在
     * @param index
     * @param type
     * @return : boolean
     * @author : yk
     * @date : 2020/05/05 17:30
     */
    public static boolean isTypeExist(String index,String type){
        return isIndexExist(index)?client.admin().indices().prepareTypesExists(index).setTypes(type).execute().actionGet().isExists() : false;
    }
    /**
     * @Description :添加数据
     * @param mapObj ：所需要添加的数据
     * @param index  ： 数据所在的index
     * @param type  : 数据所在的type
     * @param id  ： 数据的id
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     * @author : yk
     * @date : 2020/05/05 17:35
     */
    public static Map<String,Object> addData(Map<String,Object> mapObj,String index,String type,String id){
        IndexResponse indexResponse = client.prepareIndex(index, type, id).setSource(mapObj).get();
        String responseStatus = indexResponse.status().toString();
        /*
        * indexResponse.status().toString():如果添加成功，就是ok
        * 因为使用的是equals，所以ok和OK是返回为false，需要强转
        * */
        if ("CREATED".equals(responseStatus.toUpperCase())){
            /*说明操作成功*/
            resultMap.put("code", StatusEnum.OPRATION_SUCCESS.getCode());
            resultMap.put("msg", StatusEnum.OPRATION_SUCCESS.getMsg());
        }else {
            resultMap.put("code", StatusEnum.OPRATION_FAILED.getCode());
            resultMap.put("msg", StatusEnum.OPRATION_FAILED.getMsg());
        }
        return resultMap;
    }
    /**
     * @Description :添加数据（不指定id，使用uuid进行添加）
     * @param mapObj
     * @param index
     * @param type
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     * @author : yk
     * @date : 2020/05/05 17:42
     */
    public static Map<String , Object>addData(Map<String,Object> mapObj,String index,String type){
        /*UUID:123455-de4646-dvry64---->123455de4646dvry64------>再把小写字母转换成大写，比较严谨*/
        return addData(mapObj, index, type, UUID.randomUUID().toString().replaceAll("-","").toUpperCase());
    }
    /**
     * @Description : 通过id删除数据
     * @param index
     * @param type
     * @param id
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     * @author : yk
     * @date : 2020/05/05 17:46
     */
    public static Map<String,Object> deleteDataById(String index,String type,String id){
        DeleteResponse deleteResponse = client.prepareDelete(index, type, id).execute().actionGet();
        if ("OK".equals(deleteResponse.status().toString().toUpperCase())){
            /*说明操作成功*/
            resultMap.put("code", StatusEnum.OPRATION_SUCCESS.getCode());
            resultMap.put("msg", StatusEnum.OPRATION_SUCCESS.getMsg());
        }else {
            resultMap.put("code", StatusEnum.OPRATION_FAILED.getCode());
            resultMap.put("msg", StatusEnum.OPRATION_FAILED.getMsg());
        }
        return resultMap;
    }
    /**
     * @Description : 通过id修改数据
     * @param mapObj
     * @param index
     * @param type
     * @param id
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     * @author : yk
     * @date : 2020/05/05 18:33
     */
    public static Map<String,Object> updateDataById(Map<String,Object> mapObj,String index,String type,String id){
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(index).type(type).id(id).doc(mapObj);
        ActionFuture<UpdateResponse> update = client.update(updateRequest);
        if ("OK".equals(update.actionGet().status().toString().toUpperCase())){
            /*说明操作成功*/
            resultMap.put("code", StatusEnum.OPRATION_SUCCESS.getCode());
            resultMap.put("msg", StatusEnum.OPRATION_SUCCESS.getMsg());
        }else {
            resultMap.put("code", StatusEnum.OPRATION_FAILED.getCode());
            resultMap.put("msg", StatusEnum.OPRATION_FAILED.getMsg());
        }
        return resultMap;
    }
    /**
     * @Description : 通过id查询数据
     * @param index
     * @param type
     * @param id
     * @param fields ：所需要显示的字段，如果有多个字段需要显示则使用逗号隔开，如果所有的字段都需要显示，直接设置为null
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     * @author : yk
     * @date : 2020/05/05 18:38
     */
    public static Map<String,Object> selectDataById(String index,String type,String id,String fields){
        GetRequestBuilder getRequestBuilder = client.prepareGet(index, type, id);
        if (null != fields && !"".equals(fields)){
            getRequestBuilder.setFetchSource(fields.split(","),null);
        }
        GetResponse documentFields = getRequestBuilder.execute().actionGet();
        return documentFields.getSource();
    }
    /**
     * @Description :分词全文检索
     * @param index
     * @param type
     * @param query ：最终使用这个对象进行查询数据
     * @param size  ： 显示多少条
     * @param fields ：所需要显示的字段，如果需要显示多个则使用逗号隔开，如果需要全部显示，则设置为空
     * @param sourField ：所要被排序的字段
     * @param highlightField ：所要被高亮显示的字段
     * @return : java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @author : yk
     * @date : 2020/05/05 18:51
     */
    public static List<Map<String,Object>> selectAllData(String index, String type, QueryBuilder query,
                                                         Integer size,String fields,String sourField,
                                                         String highlightField){
        /*查询index*/
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        /*判断type不为空*/
        if (null != type && !"".equals(type)){
            searchRequestBuilder.setTypes(type.split(","));
        }

        /*判断高亮显示字段不为空*/
        if (null != highlightField && !"".equals(highlightField)){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            /*设置需要高亮显示的字段*/
            highlightBuilder.field(highlightField);
            searchRequestBuilder.highlighter(highlightBuilder);
        }

        searchRequestBuilder.setQuery(query);
        /*判断需要显示的字段不为null处理*/
        if (null != fields && !"".equals(fields)){
            searchRequestBuilder.setFetchSource(fields.split(","),null);
        }
        /*判断排序字段不为null处理*/
        if (null != sourField && !"".equals(sourField)){
            searchRequestBuilder.addSort(sourField, SortOrder.DESC);
        }
        /*判断所要显示的条数不为null处理*/
        if (null != size && !"".equals(size)){
            searchRequestBuilder.setSize(size);
        }
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

       /* if ("OK".equals(searchResponse.status().toString().toUpperCase())){

        }*/
        if (searchResponse.status().getStatus() == 200){
           /*
           * 解析对象(需要处理高亮显示的结果)
           * searchResponse:查询返回的结果集
           * */
           return setSelectResponse(searchResponse,highlightField);

        }
        return null;
    }
    /**
     * @Description :  处理高亮显示
     * @param searchResponse
     * @param highlightField
     * @return : java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @author : yk
     * @date : 2020/05/05 19:19
     */
    private static List<Map<String,Object>> setSelectResponse(SearchResponse searchResponse,String highlightField){
        List<Map<String,Object>> sourceList = new ArrayList<Map<String, Object>>();
        StringBuffer stringBuffer = new StringBuffer();
        for (SearchHit searchHit :  searchResponse.getHits().getHits()){
            searchHit.getSourceAsMap().put("id",searchHit.getId());
            if (null != highlightField && ""!= highlightField){
                HighlightField highlightField1 = searchHit.getHighlightFields().get(highlightField);
                Text[] text = highlightField1.getFragments();
                if (text != null){
                    for (Text str:text
                         ) {
                        stringBuffer.append(str.toString());
                    }
                    /*遍历高亮显示的结果集*/
                    searchHit.getSourceAsMap().put(highlightField,stringBuffer.toString());
                }
            }
            sourceList.add(searchHit.getSourceAsMap());
        }
        return sourceList;
    }
}
