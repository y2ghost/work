package study.ywork.activitiweb.controller;

import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import study.ywork.activitiweb.mapper.ActivitiMapper;
import study.ywork.activitiweb.security.SecurityUtil;
import study.ywork.activitiweb.util.ResponseVo;
import study.ywork.activitiweb.util.ResponseCode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/definitions")
public class ProcessDefinitionController {
    private RepositoryService repositoryService;
    private ProcessRuntime processRuntime;
    private ActivitiMapper mapper;
    private static final String UPLOAD_FILE_DIR = "/tmp/";

    public ProcessDefinitionController(RepositoryService repositoryService, ProcessRuntime processRuntime,
        SecurityUtil securityUtil, ActivitiMapper mapper) {
        this.repositoryService = repositoryService;
        this.processRuntime = processRuntime;
        this.mapper = mapper;
    }

    @PostMapping("/upload-deploy")
    public ResponseVo uploadStreamAndDeployment(@RequestParam MultipartFile processFile) {
        String fileName = processFile.getOriginalFilename();
        try {
            InputStream fileInputStream = processFile.getInputStream();
            String extension = "";
            int lastDotIndex = fileName.lastIndexOf(".");

            if (lastDotIndex > 0) {
                extension = fileName.substring(lastDotIndex + 1);
            }

            DeploymentBuilder builder = repositoryService.createDeployment();
            if (extension.equals("zip")) {
                ZipInputStream zip = new ZipInputStream(fileInputStream);
                builder.addZipInputStream(zip);
            } else {
                builder.addInputStream(fileName, fileInputStream);
            }

            Deployment deployment = builder.name(fileName).deploy();
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, deployment.getId() + ";" + fileName);
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "部署流程失败", e.toString());
        }
    }

    @PostMapping("/upload")
    public ResponseVo upload(HttpServletRequest request, @RequestParam("processFile") MultipartFile processFile) {
        String fileName = processFile.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + suffixName;
        File file = new File(UPLOAD_FILE_DIR + newFileName);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            processFile.transferTo(file);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return ResponseVo.newResponseVo(ResponseCode.SUCCESS, fileName);
    }

    @PostMapping("/deploy-uuid")
    public ResponseVo deployByFileUUID(@RequestParam String fileUUID,
        @RequestParam("deploymentName") String deploymentName) {
        try {
            String filename = "resources/bpmn/" + fileUUID;
            Deployment deployment = repositoryService.createDeployment().addClasspathResource(filename)
                .name(deploymentName).deploy();
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, deployment.getId());
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "BPMN部署流程失败", e.toString());
        }

    }

    @PostMapping("/deploy-bpmn")
    public ResponseVo addDeploymentByString(@RequestParam("stringBPMN") String stringBPMN) {
        try {
            Deployment deployment = repositoryService.createDeployment().addString("CreateWithBPMNJS.bpmn", stringBPMN)
                .name("deploy-bpmn").deploy();
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, deployment.getId());
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "string部署流程失败", e.toString());
        }
    }

    @GetMapping("/")
    public ResponseVo getDefinitions() {
        try {
            Page<ProcessDefinition> processDefinitions = processRuntime.processDefinitions(Pageable.of(0, 50));
            System.out.println("流程定义数量： " + processDefinitions.getTotalItems());
            for (ProcessDefinition pd : processDefinitions.getContent()) {
                System.out.println("getId：" + pd.getId());
                System.out.println("getName：" + pd.getName());
                System.out.println("getStatus：" + pd.getKey());
                System.out.println("getStatus：" + pd.getFormKey());
            }
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, processDefinitions.getContent());
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "获取流程定义失败", e.toString());
        }
    }

    @GetMapping("/xml")
    public void getProcessDefineXML(HttpServletResponse response, @RequestParam String deploymentId,
        @RequestParam String resourceName) {
        try {
            InputStream inputStream = repositoryService.getResourceAsStream(deploymentId, resourceName);
            int count = inputStream.available();
            byte[] bytes = new byte[count];
            response.setContentType("text/xml");
            OutputStream outputStream = response.getOutputStream();

            while (inputStream.read(bytes) != -1) {
                outputStream.write(bytes);
            }

            inputStream.close();
        } catch (Exception e) {
            e.toString();
        }
    }

    @GetMapping("/deployments")
    public ResponseVo getDeployments() {
        try {
            List<HashMap<String, Object>> listMap = new ArrayList<>();
            List<Deployment> list = repositoryService.createDeploymentQuery().list();
            for (Deployment dep : list) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", dep.getId());
                hashMap.put("name", dep.getName());
                hashMap.put("deploymentTime", dep.getDeploymentTime());
                listMap.add(hashMap);
            }

            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, listMap);
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "查询失败", e.toString());
        }
    }

    // 删除流程定义
    @PostMapping("/delete")
    public ResponseVo delDefinition(@RequestParam String deployId, @RequestParam String processId) {
        try {
            mapper.deleteFormData(processId);
            repositoryService.deleteDeployment(deployId, true);
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, "删除成功", null);
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "删除失败", e.toString());
        }
    }
}
