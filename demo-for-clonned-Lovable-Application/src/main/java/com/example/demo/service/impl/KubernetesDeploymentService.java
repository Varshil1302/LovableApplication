package com.example.demo.service.impl;

import com.example.demo.config.KubernetesConfig;
import com.example.demo.dto.deploye.DeployementResponse;
import com.example.demo.service.DeployementService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KubernetesDeploymentService implements DeployementService {

    private final KubernetesClient kubernetesConfig;
    private final StringRedisTemplate redisTemplate;

    private final String NAMESPACE= "lovable-app";
    private final String POOL_LABEL= "status";
    private final String PROJECT_LABEL= "projectId";
    private final String IDLE= "idle";
    private final String BUSY= "busy";
    private final String SYNCER_CONTAINER="syncer";
    private final String RUNNER_CONTAINER = "runner";
    private final String REVERSE_PROXY_PORT= "8090";


    @Override
    public DeployementResponse deployeProject(Long projectId) {
        String domain= "Project-"+projectId+".app.domain.com";
        Pod pod = findActivePod(projectId);
        if(pod!=null)
        {
            registerRoute(domain,pod);
            return new DeployementResponse("http://"+domain+":"+REVERSE_PROXY_PORT);
        }
        return claimAndStartNewPod(projectId,domain);
    }

    private DeployementResponse claimAndStartNewPod(Long projectId, String domain)
    {
        Pod pod = kubernetesConfig.pods().inNamespace(NAMESPACE)
                .withLabel(POOL_LABEL,IDLE)
                .list().getItems().stream()
                .findFirst()
                .orElseThrow(()->new RuntimeException("No Idle Runner Availble.Please Scale up a runner-pods."));

        String podName = pod.getMetadata().getName();
        log.info("Claiming Pod {} for project {}",podName,projectId.toString());

        kubernetesConfig.pods().inNamespace(NAMESPACE).withName(podName).edit(p->{
            p.getMetadata().getLabels().put(PROJECT_LABEL,projectId.toString());
            p.getMetadata().getLabels().put(POOL_LABEL,BUSY);
            return p;
        });

        try{
            // SYNCER CONTAINER EXECUTION
            String initialSyncCommand = String.format("mc mirror --overwrite myminio/lovable/%d/ /app/",projectId);

            log.info("Starting initial sync for project {} in pod {}", projectId, podName);

            execCommand(podName,SYNCER_CONTAINER,"sh","-c",initialSyncCommand);

            String watchCmd = String.format(
                    "nohup mc mirror --overwrite --watch myminio/lovable/%d/ /app/ > /app/sync.log 2>&1 &",
                    projectId);

            execCommand(podName,SYNCER_CONTAINER,"sh","-c",watchCmd);

            // RUNNER CONTAINER EXECUTION

            String startCmd = "npm install && nohup npm run dev -- --host 0.0.0.0 --port 5173 > /app/dev.log 2>&1 &";

            log.info("Starting dev server for project {}...", projectId);
            execCommand(podName,RUNNER_CONTAINER,"sh","-c",startCmd);
            registerRoute(domain,pod);

            log.info("Deployment successful: http://{}:{}",domain,REVERSE_PROXY_PORT);
            return new DeployementResponse("http://"+domain+":"+REVERSE_PROXY_PORT);
        }
        catch (Exception e)
        {
            log.error("Deployment failed for project {}. Releasing Pod {}.",projectId,podName,e);
            kubernetesConfig.pods().inNamespace(NAMESPACE).withName(podName).delete();
            throw new RuntimeException("Failed to deploy the project with id: "+projectId);
        }



    }

    private void registerRoute(String domain,Pod pod)
    {
        String podIP = pod.getStatus().getPodIP();
        if(podIP==null) throw new RuntimeException("Pod is not available...");
        redisTemplate.opsForValue().set("route:"+domain,podIP+":5173",6,TimeUnit.HOURS);
    }


    private void execCommand(String podName, String container, String... command) {
        log.debug("Exec in {}:{} -> {}", podName, container, String.join(" ", command));

        CompletableFuture<String> data = new CompletableFuture<>();
        try (ExecWatch ignored = kubernetesConfig.pods().inNamespace(NAMESPACE).withName(podName)
                .inContainer(container)
                .writingOutput(new ByteArrayOutputStream())
                .writingError(new ByteArrayOutputStream())
                .usingListener(new ExecListener() {
                    @Override
                    public void onClose(int code, String reason) {
                        data.complete("Done");
                    }
                })
                .exec(command)) {

            // Wait briefly to ensure command fired (Fabric8 exec is async)
            // For long running background jobs (nohup), we don't wait for "Done"
            if (command[command.length - 1].trim().endsWith("&")) {
                Thread.sleep(500);
            } else {
                data.get(30, TimeUnit.SECONDS); // Block for synchronous setup commands (npm install)
            }

        } catch (Exception e) {
            log.error("Exec failed", e);
            throw new RuntimeException("Pod Execution Failed", e);
        }
    }

    private Pod findActivePod(Long projectId) {

        return kubernetesConfig.pods().inNamespace(NAMESPACE)
                .withLabel(PROJECT_LABEL, projectId.toString())
                .withLabel(POOL_LABEL, BUSY) // Only find active/busy ones
                .list().getItems().stream()
                .filter(pod -> pod.getStatus().getPhase().equals("Running"))
                .findFirst()
                .orElse(null);
    }
}
