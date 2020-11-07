package nl.ckramer.doorstroombackend.repository;

import nl.ckramer.doorstroombackend.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.time.Instant;

@NoRepositoryBean
public interface BaseRepository<T, L extends Serializable> extends JpaRepository<T, L> {

    @Override
    default T save(Object o){
        T clazz = (T) o;
        if(o instanceof BaseEntity) {
            if(((BaseEntity) clazz).getCreatedAt() == null) {
                ((BaseEntity) clazz).setCreatedAt(Instant.now());
            }
            ((BaseEntity) clazz).setUpdatedAt(Instant.now());
        }
        return this.save(o);
    }
}
