package com.example.k8s_web.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.k8s_web.entiry.RunRecord;
import com.example.k8s_web.repository.RunRecordRepository;
import com.example.k8s_web.util.RestTemplateUtils;
import com.example.k8s_web.vo.ApiResult;
import com.example.k8s_web.vo.CompareForm;
import com.example.k8s_web.vo.CompareRecordForm;
import com.example.k8s_web.vo.TimeRangeForm;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/metrics")
public class MetricController {
    @Autowired
    private CoreV1Api coreV1Api;
    String httpPrefix = "http://app5.gzucmrepair.top/api/v1/query_range";

    @Autowired
    private RunRecordRepository runRecordRepository;
    @GetMapping("/name")
    public ApiResult obtainMetrics(String name){
//        String params = "query="+name+"&start=1679807362.703&end=1679810962.703&step=14";
        long current = System.currentTimeMillis();
        long lasteOneHour = current-1000*60*60;
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("query",name);
        jsonObject.putOpt("end",String.format("%.3f", ((double) current)/1000));
        jsonObject.putOpt("start",String.format("%.3f",((double) lasteOneHour)/1000));
        jsonObject.putOpt("step",14);

        String http = RestTemplateUtils.getHttp(httpPrefix, jsonObject);
        JSONObject res = JSONUtil.parseObj(http);
        JSONObject data = res.getJSONObject("data");
        JSONArray result = data.getJSONArray("result");
        JSONArray series = new JSONArray();
        JSONArray legend = new JSONArray();
        JSONArray xAxisData = new JSONArray();
        for (int i = 0; i < result.size(); i++) {
            JSONObject oneSeriey = new JSONObject();
            oneSeriey.putOpt("type","line");
            List<Object> finalValue  = new ArrayList<>();

            JSONObject one = result.getJSONObject(i);
            JSONObject metric = one.getJSONObject("metric");
            String instance = metric.getStr("instance");
//            System.out.println(instance);
            legend.add(instance);
            JSONArray values = one.getJSONArray("values");
            for (int j = 0; j < values.size(); j++) {
                JSONArray temp = values.getJSONArray(j);
                finalValue.add(temp.get(1));
                if(i==0){
                    xAxisData.add(temp.get(0));
                }
//                for (int k = 0; k < temp.size(); k++) {
//                    JSONArray jsonArray = temp.getJSONArray(k);
//                    JSONObject oneValue = temp.getJSONArray(k).getJSONObject(1);
//                    finalValue.add(oneValue);
//                }
//                finalValue.addAll(values.getJSONArray(j));
            }
//            System.out.println("sss");
            oneSeriey.putOpt("data",finalValue);
            oneSeriey.putOpt("name",instance);
            series.add(oneSeriey);
        }
        JSONObject re = new JSONObject();
        re.putOpt("series",series);
        re.putOpt("xAxis",new JSONObject().putOnce("data",xAxisData));

        re.putOpt("legend",new JSONObject().putOnce("data",legend));
        return ApiResult.success(re);
    }
    @PostMapping("/name/range")
    public ApiResult obtainMetricsTimeRange(@RequestBody TimeRangeForm timeRangeForm){
//        String params = "query="+name+"&start=1679807362.703&end=1679810962.703&step=14";
        long current =timeRangeForm.getEnd().getTime();
        long lasteOneHour = timeRangeForm.getStart().getTime();
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("query",timeRangeForm.getName());
        double end= ((double) current)/1000;
        double start = ((double) lasteOneHour)/1000;
        jsonObject.putOpt("end",String.format("%.3f", end));
        jsonObject.putOpt("start",String.format("%.3f",start));
        // 大概需要50个点)
        Long step = 0L;
        if (end-start>=50.0) {
            step = Math.round ((end-start)/50);
        }
        if(step<=0){
            step= 1L;
        }
        jsonObject.putOpt("step",step);

        String http = RestTemplateUtils.getHttp(httpPrefix, jsonObject);
        JSONObject res = JSONUtil.parseObj(http);
        JSONObject data = res.getJSONObject("data");
        JSONArray result = data.getJSONArray("result");
        JSONArray series = new JSONArray();
        JSONArray legend = new JSONArray();
        JSONArray xAxisData = new JSONArray();
        for (int i = 0; i < result.size(); i++) {
            JSONObject oneSeriey = new JSONObject();
            oneSeriey.putOpt("type","line");
            List<Object> finalValue  = new ArrayList<>();

            JSONObject one = result.getJSONObject(i);
            JSONObject metric = one.getJSONObject("metric");
            String instance = metric.getStr("instance");
//            System.out.println(instance);
            legend.add(instance);
            JSONArray values = one.getJSONArray("values");
            for (int j = 0; j < values.size(); j++) {
                JSONArray temp = values.getJSONArray(j);
                finalValue.add(temp.get(1));
                if(i==0){
                    xAxisData.add(temp.get(0));
                }
//                for (int k = 0; k < temp.size(); k++) {
//                    JSONArray jsonArray = temp.getJSONArray(k);
//                    JSONObject oneValue = temp.getJSONArray(k).getJSONObject(1);
//                    finalValue.add(oneValue);
//                }
//                finalValue.addAll(values.getJSONArray(j));
            }
//            System.out.println("sss");
            oneSeriey.putOpt("data",finalValue);
            oneSeriey.putOpt("name",instance);
            series.add(oneSeriey);
        }
        JSONObject re = new JSONObject();
        re.putOpt("series",series);
        re.putOpt("xAxis",new JSONObject().putOnce("data",xAxisData));

        re.putOpt("legend",new JSONObject().putOnce("data",legend));
        return ApiResult.success(re);
    }
    @PostMapping("/name/campare")
    public ApiResult obtainMetricsCompare(@RequestBody CompareForm compareForm){
//        String params = "query="+name+"&start=1679807362.703&end=1679810962.703&step=14";
        long timespan1 = compareForm.getEnd1().getTime()-compareForm.getStart1().getTime();
        long timespan2 = compareForm.getEnd2().getTime()-compareForm.getStart2().getTime();
        if(timespan2!=timespan1) return ApiResult.error();// 如果时间间隔不一样，报错
        long current = compareForm.getEnd1().getTime();
        long lasteOneHour = compareForm.getStart1().getTime();
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("query",compareForm.getName());
        jsonObject.putOpt("end",String.format("%.3f", ((double) current)/1000));
        jsonObject.putOpt("start",String.format("%.3f",((double) lasteOneHour)/1000));
        jsonObject.putOpt("step",14);

        String http = RestTemplateUtils.getHttp(httpPrefix, jsonObject);
        JSONObject res = JSONUtil.parseObj(http);
        JSONObject data = res.getJSONObject("data");
        JSONArray result = data.getJSONArray("result");
        JSONArray series = new JSONArray();
        JSONArray legend = new JSONArray();
        JSONArray xAxisData = new JSONArray();
        for (int i = 0; i < result.size(); i++) {
            JSONObject oneSeriey = new JSONObject();
            oneSeriey.putOpt("type","line");
            List<Object> finalValue  = new ArrayList<>();

            JSONObject one = result.getJSONObject(i);
            JSONObject metric = one.getJSONObject("metric");
            String instance = metric.getStr("instance");
//            System.out.println(instance);
            legend.add(instance+"_task1");
            JSONArray values = one.getJSONArray("values");
            for (int j = 0; j < values.size(); j++) {
                JSONArray temp = values.getJSONArray(j);
                finalValue.add(temp.get(1));
                if(i==0){
//                    xAxisData.add(temp.get(0));
                    xAxisData.add(j);// 改用下标
                }
//                for (int k = 0; k < temp.size(); k++) {
//                    JSONArray jsonArray = temp.getJSONArray(k);
//                    JSONObject oneValue = temp.getJSONArray(k).getJSONObject(1);
//                    finalValue.add(oneValue);
//                }
//                finalValue.addAll(values.getJSONArray(j));
            }
//            System.out.println("sss");
            oneSeriey.putOpt("data",finalValue);
            oneSeriey.putOpt("name",instance+"_task1");
            series.add(oneSeriey);
        }

        // 第二个任务

         current = compareForm.getEnd2().getTime();
        lasteOneHour = compareForm.getStart2().getTime();
        jsonObject = new JSONObject();
        jsonObject.putOpt("query",compareForm.getName());
        jsonObject.putOpt("end",String.format("%.3f", ((double) current)/1000));
        jsonObject.putOpt("start",String.format("%.3f",((double) lasteOneHour)/1000));
        jsonObject.putOpt("step",14);

        http = RestTemplateUtils.getHttp(httpPrefix, jsonObject);
        res = JSONUtil.parseObj(http);
        data = res.getJSONObject("data");
        result = data.getJSONArray("result");
        for (int i = 0; i < result.size(); i++) {
            JSONObject oneSeriey = new JSONObject();
            oneSeriey.putOpt("type","line");
            List<Object> finalValue  = new ArrayList<>();

            JSONObject one = result.getJSONObject(i);
            JSONObject metric = one.getJSONObject("metric");
            String instance = metric.getStr("instance");
//            System.out.println(instance);
            legend.add(instance+"_task2");
            JSONArray values = one.getJSONArray("values");
            for (int j = 0; j < values.size(); j++) {
                JSONArray temp = values.getJSONArray(j);
                finalValue.add(temp.get(1));
            }
//            System.out.println("sss");
            oneSeriey.putOpt("data",finalValue);
            oneSeriey.putOpt("name",instance+"_task2");
            series.add(oneSeriey);
        }

        JSONObject re = new JSONObject();
        re.putOpt("series",series);
        re.putOpt("xAxis",new JSONObject().putOnce("data",xAxisData));

        re.putOpt("legend",new JSONObject().putOnce("data",legend));
        return ApiResult.success(re);
    }
    @PostMapping("/name/campareRecord")
    public ApiResult obtainMetricsCompareRecord(@RequestBody CompareRecordForm compareRecordForm){
//        String params = "query="+name+"&start=1679807362.703&end=1679810962.703&step=14";

        RunRecord runRecord = runRecordRepository.findById(compareRecordForm.getRecordId1()).get();
        if (runRecord == null|| StringUtils.isEmpty(runRecord.getMemRecord())) {
            return ApiResult.error();
        }
        RunRecord runRecord2 = runRecordRepository.findById(compareRecordForm.getRecordId2()).get();
        if (runRecord2 == null|| StringUtils.isEmpty(runRecord2.getMemRecord())) {
            return ApiResult.error();
        }
        String text1 = null;
        if (compareRecordForm.getType()==0) {
            text1 = runRecord.getCpuRecord();
        }else{
            text1 = runRecord.getMemRecord();
        }
        String text2 = null;
        if (compareRecordForm.getType()==0) {
            text2 = runRecord2.getCpuRecord();
        }else{
            text2 = runRecord2.getMemRecord();
        }
        String http = text1;
        JSONObject res = JSONUtil.parseObj(http);
        JSONObject data = res.getJSONObject("data");
        JSONArray result = data.getJSONArray("result");
        JSONArray series = new JSONArray();
        JSONArray legend = new JSONArray();
        JSONArray xAxisData = new JSONArray();
        for (int i = 0; i < result.size(); i++) {
            JSONObject oneSeriey = new JSONObject();
            oneSeriey.putOpt("type","line");
            List<Object> finalValue  = new ArrayList<>();

            JSONObject one = result.getJSONObject(i);
            JSONObject metric = one.getJSONObject("metric");
            String instance = metric.getStr("instance");
//            System.out.println(instance);
            legend.add(instance+"_task1");
            JSONArray values = one.getJSONArray("values");
            for (int j = 0; j < values.size(); j++) {
                JSONArray temp = values.getJSONArray(j);
                finalValue.add(temp.get(1));
                if(i==0){
//                    xAxisData.add(temp.get(0));
                    xAxisData.add(j);// 改用下标
                }
//                for (int k = 0; k < temp.size(); k++) {
//                    JSONArray jsonArray = temp.getJSONArray(k);
//                    JSONObject oneValue = temp.getJSONArray(k).getJSONObject(1);
//                    finalValue.add(oneValue);
//                }
//                finalValue.addAll(values.getJSONArray(j));
            }
//            System.out.println("sss");
            oneSeriey.putOpt("data",finalValue);
            oneSeriey.putOpt("name",instance+"_task1");
            // 不显示数据点
            oneSeriey.putOpt("symbol","none");
            series.add(oneSeriey);
        }

        // 第二个任务
        http = text2;
        res = JSONUtil.parseObj(http);
        data = res.getJSONObject("data");
        result = data.getJSONArray("result");
        for (int i = 0; i < result.size(); i++) {
            JSONObject oneSeriey = new JSONObject();
            oneSeriey.putOpt("type","line");
            List<Object> finalValue  = new ArrayList<>();

            JSONObject one = result.getJSONObject(i);
            JSONObject metric = one.getJSONObject("metric");
            String instance = metric.getStr("instance");
//            System.out.println(instance);
            legend.add(instance+"_task2");
            JSONArray values = one.getJSONArray("values");
            for (int j = 0; j < values.size(); j++) {
                JSONArray temp = values.getJSONArray(j);
                finalValue.add(temp.get(1));
            }
//            System.out.println("sss");
            oneSeriey.putOpt("data",finalValue);
            oneSeriey.putOpt("name",instance+"_task2");
            // 设置虚线
            oneSeriey.putOpt("itemStyle",new JSONObject()
                    .putOnce("normal",new JSONObject().putOnce("lineStyle",
                            new JSONObject().putOnce("type","dotted"))));
            oneSeriey.putOpt("symbol","none");
            series.add(oneSeriey);
        }

        JSONObject re = new JSONObject();
        re.putOpt("series",series);
        re.putOpt("xAxis",new JSONObject().putOnce("data",xAxisData));

        re.putOpt("legend",new JSONObject().putOnce("data",legend));
//        re.putOpt("tooltips",new JSONObject().putOnce("trigger","axis"));
        return ApiResult.success(re);
    }
    @PostMapping("/name/campare2")
    public ApiResult obtainMetricsCompare2(@RequestBody CompareForm compareForm){
//        String params = "query="+name+"&start=1679807362.703&end=1679810962.703&step=14";
        long timespan1 = compareForm.getEnd1().getTime()-compareForm.getStart1().getTime();
        long timespan2 = compareForm.getEnd2().getTime()-compareForm.getStart2().getTime();
        if(timespan2!=timespan1) return ApiResult.error();// 如果时间间隔不一样，报错
        long current = compareForm.getEnd1().getTime();
        long lasteOneHour = compareForm.getStart1().getTime();
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("query",compareForm.getName());
        jsonObject.putOpt("end",String.format("%.3f", ((double) current)/1000));
        jsonObject.putOpt("start",String.format("%.3f",((double) lasteOneHour)/1000));
        jsonObject.putOpt("step",14);

        String http = RestTemplateUtils.getHttp(httpPrefix, jsonObject);
        JSONObject res = JSONUtil.parseObj(http);
        JSONObject data = res.getJSONObject("data");
        JSONArray result = data.getJSONArray("result");
        JSONArray series = new JSONArray();
        JSONArray legend = new JSONArray();
        JSONArray xAxisData = new JSONArray();
        List<String> dimensions = new ArrayList<>();
        dimensions.add("product");
        Map<String,Map<String,Object>> sources = new HashMap();
        for (int i = 0; i < result.size(); i++) {
            JSONObject oneSeriey = new JSONObject();
            oneSeriey.putOpt("type","line");
            List<Object> finalValue  = new ArrayList<>();
            JSONObject one = result.getJSONObject(i);
            JSONObject metric = one.getJSONObject("metric");
            String instance = metric.getStr("instance");
            legend.add(instance+"_task1");
            dimensions.add(instance+"_task1");
            series.add(new JSONObject().putOnce("type","line"));
            JSONArray values = one.getJSONArray("values");
            for (int j = 0; j < values.size(); j++) {
                JSONArray temp = values.getJSONArray(j);
                finalValue.add(temp.get(1));
                if(i==0){
                    xAxisData.add(j);// 改用下标
                }
                Map<String, Object> source = sources.getOrDefault(String.valueOf(temp.get(0)), new HashMap<>());
                if (source.size()==0) {
                    source.put("product",String.valueOf(temp.get(0)));
                }
                source.put(instance+"_task1",temp.get(1));
                sources.put(String.valueOf(temp.get(0)),source);
            }
//            System.out.println("sss");
            oneSeriey.putOpt("data",finalValue);
            oneSeriey.putOpt("name",instance+"_task1");
//            series.add(oneSeriey);
        }

        // 第二个任务

        current = compareForm.getEnd2().getTime();
        lasteOneHour = compareForm.getStart2().getTime();
        jsonObject = new JSONObject();
        jsonObject.putOpt("query",compareForm.getName());
        jsonObject.putOpt("end",String.format("%.3f", ((double) current)/1000));
        jsonObject.putOpt("start",String.format("%.3f",((double) lasteOneHour)/1000));
        jsonObject.putOpt("step",14);

        http = RestTemplateUtils.getHttp(httpPrefix, jsonObject);
        res = JSONUtil.parseObj(http);
        data = res.getJSONObject("data");
        result = data.getJSONArray("result");
        series.add(new JSONObject().putOnce("type","line"));

        for (int i = 0; i < result.size(); i++) {
            JSONObject oneSeriey = new JSONObject();
            oneSeriey.putOpt("type","line");
            List<Object> finalValue  = new ArrayList<>();
            JSONObject one = result.getJSONObject(i);
            JSONObject metric = one.getJSONObject("metric");
            String instance = metric.getStr("instance");
//            System.out.println(instance);
            legend.add(instance+"_task2");
            dimensions.add(instance+"_task2");

            JSONArray values = one.getJSONArray("values");
            for (int j = 0; j < values.size(); j++) {
                JSONArray temp = values.getJSONArray(j);
                if(i==0){
                    String xvalue = String.valueOf(temp.get(0));
                }
                finalValue.add(temp.get(1));
                Map<String, Object> source = sources.getOrDefault(String.valueOf(temp.get(0)), new HashMap<>());
                if (source.size()==0) {
                    source.put("product",String.valueOf(temp.get(0)));
                }
                source.put(instance+"_task2",temp.get(1));
                sources.put(String.valueOf(temp.get(0)+"_task2"),source);
            }
//            System.out.println("sss");
//            oneSeriey.putOpt("data",finalValue);
//            oneSeriey.putOpt("name",instance+"_task2");
            series.add(oneSeriey);
        }

        JSONObject re = new JSONObject();
        re.putOpt("series",series);
        re.putOpt("xAxis",new JSONObject().putOnce("type","category"));
        re.putOpt("dataset",new JSONObject().putOnce("dimensions",dimensions).putOnce("source",
                sources.values().stream().sorted((obj1,obj2)->{
                    Integer product1 = Integer.valueOf((String) obj1.get("product"));
                    Integer product2 = Integer.valueOf((String) obj2.get("product"));
                    return product1-product2;
                }).collect(Collectors.toList())));
        re.putOpt("legend",new JSONObject());
        re.putOpt("tooltip",new JSONObject());
        re.putOpt("yAxis",new JSONObject());
        return ApiResult.success(re);
    }
    @GetMapping("/clearAll")
    public ApiResult clearAll() throws ApiException {


       HttpUtil.post("http://app5.gzucmrepair.top/api/v1/admin/tsdb/delete_series?match[]=cpu_usage_active",new HashMap<>());
        HttpUtil.post("http://app5.gzucmrepair.top/api/v1/admin/tsdb/delete_series?match[]=mem_usage_active",new HashMap<>());

        String post = HttpUtil.post("http://app5.gzucmrepair.top/api/v1/admin/tsdb/delete_series?match[]=cpu_usage_avg_5m", new HashMap<>());
        System.out.println(post);

        HttpUtil.post("http://app5.gzucmrepair.top/api/v1/admin/tsdb/delete_series?match[]=cpu_usage_max_avg_1h",new HashMap<>());
        HttpUtil.post("http://app5.gzucmrepair.top/api/v1/admin/tsdb/delete_series?match[]=cpu_usage_max_avg_1d",new HashMap<>());
        HttpUtil.post("http://app5.gzucmrepair.top/api/v1/admin/tsdb/delete_series?match[]=mem_usage_avg_5m",new HashMap<>());
        HttpUtil.post("http://app5.gzucmrepair.top/api/v1/admin/tsdb/delete_series?match[]=mem_usage_max_avg_1h",new HashMap<>());
        HttpUtil.post("http://app5.gzucmrepair.top/api/v1/admin/tsdb/delete_series?match[]=mem_usage_max_avg_1d",new HashMap<>());
        V1PodList list = coreV1Api.listPodForAllNamespaces(true,null, null, "app=crane-scheduler-controller", null, null, null, null, null, null);
        List<V1Pod> items = list.getItems();
        if (items.size()>0) {
            V1Pod v1Pod = items.get(0);
            coreV1Api.deleteNamespacedPod(v1Pod.getMetadata().getName(),v1Pod.getMetadata().getNamespace(),null
                    ,null,null,null,null,null);
        }
        return ApiResult.success();
    }
}
