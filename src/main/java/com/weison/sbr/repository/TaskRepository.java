package com.weison.sbr.repository;

import com.weison.sbr.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author WeisonWei
 * @date 2020/10/10
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, CrudRepository<Task, Long> {

}
