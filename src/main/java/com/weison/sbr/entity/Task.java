package com.weison.sbr.entity;

import com.weison.sbr.repository.TaskAuditListener;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;


@Table(name = "t_task", indexes = @Index(name = "idx_tag", columnList = "tag"))
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate(true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class, TaskAuditListener.class})
public class Task extends BaseEntity {

    @Column(name = "name", nullable = true, columnDefinition = BaseEntity.VARCHAR_DEFAULT_0)
    private String name = null;

    @Column(name = "describe", nullable = true, columnDefinition = BaseEntity.VARCHAR_DEFAULT_0)
    private String describe = null;

    @Column(name = "type", nullable = true, columnDefinition = BaseEntity.INT_DEFAULT_0)
    private Type type = null;

    @Column(name = "version", nullable = true, columnDefinition = BaseEntity.INT_DEFAULT_0)
    private Integer version = null;

    @Column(name = "tag", nullable = true, columnDefinition = BaseEntity.INT_DEFAULT_0)
    private Tag tag = null;

    @Column(name = "status", nullable = true, columnDefinition = BaseEntity.INT_DEFAULT_0)
    private STATUS status = null;

    @Column(name = "remark", nullable = true, columnDefinition = BaseEntity.VARCHAR_DEFAULT_0)
    private String remark = null;

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
         * 签到打卡
         */
        SIGN
    }

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
}
