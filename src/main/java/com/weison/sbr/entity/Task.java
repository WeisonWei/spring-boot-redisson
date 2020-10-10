package com.weison.sbr.entity;

import com.weison.sbr.repository.TaskAuditListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * @author WeisonWei
 * @date 2020/10/10
 */
@Table(name = "t_task",
        indexes = {
                @Index(name = "idx_type", columnList = "type"),
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uniq_tag", columnNames = {"tag"}),
        })
@Data
@ToString
@Entity
@DynamicInsert(true)
@DynamicUpdate(true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class, TaskAuditListener.class})
public class Task extends BaseEntity {

    @Column(name = "describe", nullable = true, columnDefinition = BaseEntity.VARCHAR_DEFAULT_0 + " comment '描述'")
    private String describe = null;

    @Column(name = "name", nullable = true, columnDefinition = BaseEntity.VARCHAR_DEFAULT_0 + " comment '姓名'")
    private String name = null;

    @Column(name = "remark", nullable = true, columnDefinition = BaseEntity.VARCHAR_DEFAULT_0 + " comment '备注'")
    private String remark = null;

    @Column(name = "tag", nullable = true, columnDefinition = BaseEntity.INT_DEFAULT_0 + " comment '标记'")
    private Tag tag = null;

    @Column(name = "type", nullable = true, columnDefinition = BaseEntity.INT_DEFAULT_0 + " comment '类型'")
    private Type type = null;

    @Column(name = "version", nullable = true, columnDefinition = BaseEntity.INT_DEFAULT_0 + " comment '版本'")
    private Integer version = null;

    public enum Tag {
        /**
         * 默认
         */
        DEFAULT,
        /**
         * 签到
         */
        SIGN,
        /**
         * 购买
         */
        BUY
    }

    public enum Type {
        /**
         * 默认
         */
        DEFAULT,
        /**
         * 购买
         */
        BUY,
        /**
         * 签到
         */
        SIGN
    }
}
