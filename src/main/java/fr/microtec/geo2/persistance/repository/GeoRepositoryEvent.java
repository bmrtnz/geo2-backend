package fr.microtec.geo2.persistance.repository;

import fr.microtec.geo2.persistance.repository.event.Geo2LoadEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class GeoRepositoryEvent {

    private final List<Geo2LoadEventListener<?>> loadEventListeners;

    public GeoRepositoryEvent(List<Geo2LoadEventListener<?>> loadEventListeners) {
        this.loadEventListeners = loadEventListeners;
    }

    /**
     * Fire load event.
     */
    public <T> void fireLoad(T entity) {
        loadEventListeners.stream()
            .filter(e -> e.acceptVisitor(entity.getClass()))
            .forEach(e -> ((Geo2LoadEventListener<T>) e).onLoad(entity));
    }

}
