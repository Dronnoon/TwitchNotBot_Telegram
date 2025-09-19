package DataBase.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity(name = "Statusnotification")
@Table(name = "statusnotification", schema = "public")
@Getter
@Setter
@Accessors(chain = true)
public class Statusnotification {
    @Id
    @Column(name = "tg_chat_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = false;

    public Statusnotification() {
    }
}