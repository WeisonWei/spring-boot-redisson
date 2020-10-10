package com.weison.sbr.repository;

import com.weison.sbr.entity.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

/**
 * @author WeisonWei
 * @date 2020/10/10
 */
@Slf4j
@Component
public class TaskAuditListener {

    @PostPersist
    private void postPersist(Task entity) {
        log.info("Task [ " + entity.getId() + " ]被更新");
    }

    @PostRemove
    private void PostRemove(Task entity) {
        log.info("Task [ " + entity.getId() + " ]被删除");
    }

    @PostUpdate
    private void PostUpdate(Task entity) {
        log.info("Task [ " + entity.getId() + " ]被更新");
    }
}
