package com.weison.sbr.controller;

import com.weison.sbr.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author WeisonWei
 * @date 2020/10/10
 */
@RestController
@Slf4j
public class TaskController {

    @Resource
    private TaskService taskService;

    @GetMapping("/hello")
    public String hello(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String requestURL = request.getRequestURL().toString();
        log.info("requestURI:{},requestURL:{},", requestURI, requestURL);
        return "hello";
    }

    @GetMapping("/dir/{key}")
    public String dir(@PathVariable("key") String key) {
        return taskService.dir(key);
    }

    @GetMapping("/locks/no")
    public String noLock() {
        return taskService.noLock();
    }

    @GetMapping("/locks/synchronized")
    public synchronized String synchronizedLock() {
        return taskService.synchronizedLock();
    }

    @GetMapping("/locks/re-entrant-lock-f")
    public String fairReEntrantLock() {
        return taskService.fairReEntrantLock();
    }

    @GetMapping("/locks/e-entrant-lock")
    public String reEntrantLock() throws InterruptedException {
        return taskService.reEntrantLock();
    }

    @GetMapping("/locks/r-lock")
    public String rLock() throws InterruptedException {
        return taskService.rLock();
    }

    @GetMapping("/locks/rtf-lock")
    public String trfLock() throws InterruptedException {
        return taskService.trfLock();
    }

    @GetMapping("/locks/rt-lock")
    public String trLock() throws InterruptedException {
        return taskService.trLock();
    }

    @GetMapping("/locks/r-lock-e")
    public String rLockExec() {
        return taskService.rLockExec();
    }

    @GetMapping("/amounts")
    public Float getAmount() {
        return taskService.getAmount();
    }

    @PostMapping("/amounts")
    public Float useAmount() {
        return taskService.useAmount();
    }
}
