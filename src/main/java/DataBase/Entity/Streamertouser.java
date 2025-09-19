package DataBase.Entity;

import DataBase.Hibernate.StreamertouserId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.IdClass;
import jakarta.persistence.UniqueConstraint;



import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@IdClass(StreamertouserId.class)
@Table(name = "streamertousers", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "streamertousers_user_id_streamer_id_key", columnNames = {"user_id", "streamer_id"})
})
@Getter
@Setter
@Accessors(chain = true)
public class Streamertouser {
    @Id
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @NotNull
    @Column(name = "streamer_id", nullable = false)
    private String streamerId;

    public Streamertouser() {
    }
}