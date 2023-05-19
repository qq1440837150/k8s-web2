package com.example.k8s_web.task;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.json.JSONObject;
import com.example.k8s_web.entiry.RunRecord;
import com.example.k8s_web.entiry.Task;
import com.example.k8s_web.repository.RunRecordRepository;
import com.example.k8s_web.repository.TaskRepository;
import com.example.k8s_web.util.RestTemplateUtils;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.ModelMapper;
import io.kubernetes.client.util.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class RunTask implements Runnable{
    static {
//        ModelMapper.addModelMap("","v1","Pod","",);
//        Class.forName("")
        Yaml.addModelMap("v1","Pod",V1Pod.class);
    }
    public static String httpPrefix = "http://app5.gzucmrepair.top/api/v1/query_range";

    private Long taskId;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private TaskRepository taskRepository;

    public RunRecordRepository getRunRecordRepository() {
        return runRecordRepository;
    }

    public void setRunRecordRepository(RunRecordRepository runRecordRepository) {
        this.runRecordRepository = runRecordRepository;
    }

    private RunRecordRepository runRecordRepository;

    @Override
    public void run() {
        Optional<Task> byId = taskRepository.findById(taskId);
        if (byId.isPresent()) {

            Task task = byId.get();
            ApiClient client = null;
            try {
                client = Config.defaultClient().setVerifyingSsl(false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Configuration.setDefaultApiClient(client);
            CoreV1Api api = new CoreV1Api();

            V1Pod load = null;
            if(task.getType()==null && task.getType()==0){
                try {
                    ClassPathResource classPathResource = new ClassPathResource("cputest_pod.yaml");
//                    File file = classPathResource.getFile();
                    String content = classPathResource.readUtf8Str();
//                    String content = classPathResource.readStr(Charset.forName());

//                    File file = new File("E:\\java\\k8s-web\\k8s-web\\k8s-web\\src\\main\\resources\\cputest_pod.yaml");

                    load = (V1Pod) Yaml.load(content);
                    load.getMetadata().putLabelsItem("app",task.getPodName());
                    // 设置调度器
                    if (task.getScheduler()==1) {
                        load.getSpec().setSchedulerName("crane-scheduler");
                    }else {
//                    load.getSpec().setSchedulerName("default");
                        load.getSpec().setSchedulerName(null);
                    }
                    List<V1Container> containers = load.getSpec().getContainers();
                    containers.get(0).setImage(task.getContainer());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                try {

//                    V1ConfigMapBuilder builder = new V1ConfigMapBuilder();
                    HashMap<String, String> data = new HashMap<>();
                    data.put("TestMain.java",task.getCodeContent());
//                    V1ConfigMap build = builder.withApiVersion("v1")
//                            .withKind("ConfigMap")
//                            .withNewMetadata().withName(task.getName() + "data").endMetadata()
//                            .withData(data)
//                            .build();
                    V1ConfigMap build = new V1ConfigMap();
                    build.setApiVersion("v1");
                    build.setKind("ConfigMap");
                    build.setMetadata(new V1ObjectMeta().name(task.getName()+"data"));
                    build.setData(data);
                    api.createNamespacedConfigMap("default",build,null,null,null ,null );
                    ClassPathResource classPathResource = new ClassPathResource("general_pod.yaml");
                    String content = classPathResource.readUtf8Str();

//                    String content = classPathResource.readStr(Charset.defaultCharset());
//                    File file = classPathResource.getFile();
//                    File file = new File("E:\\java\\k8s-web\\k8s-web\\k8s-web\\src\\main\\resources\\general_pod.yaml");
                    load = (V1Pod) Yaml.load(content);
                    load.getMetadata().putLabelsItem("app",task.getPodName());
                    List<V1Volume> volumes = load.getSpec().getVolumes();
                    V1Volume v1Volume = volumes.get(0);
                    V1ConfigMapVolumeSource v1ConfigMapVolumeSource = new V1ConfigMapVolumeSource();
                    v1ConfigMapVolumeSource.setName(task.getName()+"data");
                    v1Volume.setConfigMap(v1ConfigMapVolumeSource);

                    // 设置调度器
                    if (task.getScheduler()==1) {
                        load.getSpec().setSchedulerName("crane-scheduler");
                    }else {
//                    load.getSpec().setSchedulerName("default");
                        load.getSpec().setSchedulerName(null);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("开始时间："+ new Date());
            Date startTime = new Date();

            int count = 1;
            List<String> podName = new ArrayList<>();

            while (count<=task.getPodNums()){
                try {

                    String name = task.getPodName()+ "-"+count;
                    podName.add(name);
                    V1ObjectMeta metadata = load.getMetadata();
                    load.getMetadata().setName(name);
                    api.createNamespacedPod("default",
                            load, null, null, null,null);
                    Thread.sleep(1000*60*task.getInterval());

                    count++;
                } catch (InterruptedException e) {
                    System.out.println("睡眠中断了");
                    // 查询状态
                    Optional<Task> taskOptional = taskRepository.findById(taskId);
                    if (taskOptional.isPresent()) {
                        if (taskOptional.get().getStatus()==0) {
                            break;
                        }
                    }

                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                Thread.sleep(200);
                //some cleaning up code...
                System.out.println("结束时间："+ new Date());
                if(task.getType()==null && task.getType()==0){

                }else{// 删除configmap
                    String configMapName = task.getName() + "data";
                    api.deleteNamespacedConfigMap(configMapName,"default",null,null,null,null,null,null );

                }
                Date endTime = new Date();
                for (String name : podName) {
                    api.deleteNamespacedPod(name,"default",null,null,
                            null,null,null,null);
                }
                // 保存记录 更新任务状态
                RunRecord runRecord = new RunRecord();
                runRecord.setStartDate(startTime);
                runRecord.setEndDate(endTime);
                runRecord.setTaskId(taskId);

                // 这里查询一次指标
                String cpu_usage_active = getMetric("cpu_usage_active",runRecord);
                runRecord.setCpuRecord(cpu_usage_active);
                String mem_usage_active = getMetric("mem_usage_active",runRecord);
                runRecord.setMemRecord(mem_usage_active);


                runRecordRepository.save(runRecord);
                task.setStatus((short) 0);

                taskRepository.save(task);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getMetric(String metricName, RunRecord runRecord) {
        long current = runRecord.getEndDate().getTime();
        long lasteOneHour = runRecord.getStartDate().getTime();
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("query",metricName);
        jsonObject.putOpt("end",String.format("%.3f", ((double) current)/1000));
        jsonObject.putOpt("start",String.format("%.3f",((double) lasteOneHour)/1000));
        jsonObject.putOpt("step",14);
        String http = RestTemplateUtils.getHttp(httpPrefix, jsonObject);
        return http;
    }

    public static void main(String[] args) throws IOException {
        File file = new File("E:\\java\\k8s-web\\k8s-web\\k8s-web\\src\\main\\resources\\general_pod.yaml");
        Object load = Yaml.load(file);
        System.out.println(load);
    }
}

