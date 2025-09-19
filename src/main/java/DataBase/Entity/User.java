package DataBase.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "users", schema = "public")
public class User {
    @Id
    @NotNull
    @Column(name = "tg_chat_id", nullable = false)
    private Long id;

    @Size(max = 200)
    @NotNull
    @Column(name = "user_name", nullable = false, length = 200)
    private String userName;

    @ManyToMany(mappedBy = "users")
    private Set<Streamer> streamers = new LinkedHashSet<>();

    public User() {
    }


}