package com.example.k8s_web.websocket;

import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@ServerEndpoint("/log/{podName}")
@Component
public class WebSocketController {
    @Autowired
    public  void setCoreV1Api(CoreV1Api coreV1Api) {
        WebSocketController.coreV1Api = coreV1Api;
    }


    private static CoreV1Api coreV1Api;
    private Process process;
    private InputStream inputStream;

    /**
     * 新的WebSocket请求开启
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("podName") String podName) {
        try {
            String fieldSelector = "metadata.name="+podName;
            PodLogs logs = new PodLogs();

            V1PodList list = coreV1Api.listPodForAllNamespaces(true,null, fieldSelector, null, null, null, null, null, null, null);
            List<V1Pod> items = list.getItems();
            if (items.size()>0) {
                V1Pod v1Pod = items.get(0);
                InputStream is = logs.streamNamespacedPodLog(v1Pod);
                this.inputStream = is;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String line = null;
                        try {
                            while((line = reader.readLine()) != null) {
                                // 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
                                session.getBasicRemote().sendText(line+"<br/>" );
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * WebSocket请求关闭
     */
    @OnClose
    public void onClose() {
        try {
            if(inputStream != null)
                inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(process != null)
            process.destroy();
    }

    @OnError
    public void onError(Throwable thr) {
        thr.printStackTrace();
    }
}