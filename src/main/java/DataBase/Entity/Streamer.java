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
@Table(name = "streamer", schema = "public")
public class Streamer {
    @Id
    @Size(max = 200)
    @Column(name = "twitch_id", nullable = false, length = 200)
    private String twitchId;

    @Size(max = 100)
    @NotNull
    @Column(name = "url", nullable = false, length = 100)
    private String url;

    @Size(max = 200)
    @NotNull
    @Column(name = "username", nullable = false, length = 200)
    private String username;

    @ManyToMany
    @JoinTable(name = "streamertousers",
            joinColumns = @JoinColumn(name = "streamer_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<DataBase.Entity.User> users = new LinkedHashSet<>();

    public Streamer() {
    }




}