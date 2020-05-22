package com.weison.sbr.service;

import com.weison.sbr.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository TaskRepository;

    @Autowired
    private TaskService taskService;


}
