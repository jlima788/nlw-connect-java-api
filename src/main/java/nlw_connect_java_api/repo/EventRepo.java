package nlw_connect_java_api.repo;

import nlw_connect_java_api.model.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepo extends CrudRepository<Event, Integer> {
    public Event findByPrettyName(String prettyName);
    
}
