package DataBase.Hibernate;

import java.io.Serializable;
import java.util.Objects;

public class StreamertouserId implements Serializable {
    private Long userId;
    private String streamerId;

    public StreamertouserId() {
    }

    public StreamertouserId(Long userId, String streamerId) {
        this.userId = userId;
        this.streamerId = streamerId;
    }

    // equals() и hashCode() обязательно!
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamertouserId)) return false;
        StreamertouserId that = (StreamertouserId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(streamerId, that.streamerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, streamerId);
    }
}
