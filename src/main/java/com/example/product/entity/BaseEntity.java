package com.example.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {

    private static final long serialVersoinUID = 1L;

    @JsonIgnore
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @JsonIgnore
    @Column(name = "updated_at")
    private Date updatedAt;

    @JsonIgnore
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @PrePersist
    private void onCreate(){
        createdAt = new Date();
        isDeleted = false;
    }

    @PreUpdate
    private void onUpdate(){
        updatedAt = new Date();
    }
}
