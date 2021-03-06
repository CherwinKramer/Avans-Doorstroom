package nl.ckramer.doorstroombackend.entity.base;

import lombok.Getter;
import lombok.Setter;
import nl.ckramer.doorstroombackend.entity.User;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public abstract class Auditable extends BaseEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "user_id")
    @CreatedBy
    private User user;

}