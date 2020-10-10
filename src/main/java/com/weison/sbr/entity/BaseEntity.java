package com.weison.sbr.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author WeisonWei
 * @date 2020/10/10
 */
@Slf4j
@MappedSuperclass
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EntityListeners({AuditingEntityListener.class})
public abstract class BaseEntity implements Serializable {

    public static final String INT_DEFAULT_0 = "int default 0";
    public static final String TINY_INT_DEFAULT_0 = "tinyint default 0";
    public static final String BIGINT_DEFAULT_0 = "bigint default 0";
    public static final String DECIMAL_DEFAULT_0 = "decimal default 0";
    public static final String VARCHAR_DEFAULT_0 = "varchar(255) default ''";
    public static final String VARCHAR_DEFAULT_20 = "varchar(20) default ''";
    public static final String VARCHAR_DEFAULT_50 = "varchar(50) default ''";
    public static final String VARCHAR_DEFAULT_1024 = "varchar(1024) default ''";
    public static final String DATETIME_DEFAULT_0 = "datetime default null";
    public static final String TEXT_DEFAULT = "text";

    @Id
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @CreatedBy
    @Column(name = "cid", nullable = true, columnDefinition = BaseEntity.BIGINT_DEFAULT_0)
    private Long cid;

    @LastModifiedBy
    @Column(name = "mid", nullable = true, columnDefinition = BaseEntity.BIGINT_DEFAULT_0)
    private Long mid;

    @CreatedDate
    @Column(name = "create_time", nullable = true, columnDefinition = BaseEntity.BIGINT_DEFAULT_0)
    private Long createTime;

    @LastModifiedDate
    @Column(name = "update_time", nullable = true, columnDefinition = BaseEntity.BIGINT_DEFAULT_0)
    private Long updateTime;

    @Deprecated
    @Column(name = "del", nullable = true, columnDefinition = BaseEntity.BIGINT_DEFAULT_0)
    private Long del;

    public BaseEntity delete() {
        setDel(System.currentTimeMillis());
        return this;
    }

    public BaseEntity insert() {
        setCreateTime(System.currentTimeMillis());
        return this;
    }

    public BaseEntity update() {
        setUpdateTime(System.currentTimeMillis());
        return this;
    }

}
