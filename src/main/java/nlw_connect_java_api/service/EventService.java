package nlw_connect_java_api.service;

import nlw_connect_java_api.model.Event;
import nlw_connect_java_api.repo.EventRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    
    @Autowired
    private EventRepo eventRepo;

    public Event addNewEvent(Event event){

        event.setPrettyName(event.getTitle().toLowerCase().replaceAll(" ", "-"));
        return eventRepo.save(event);
    }

    public List<Event> getAllEvents(){
        return (List<Event>) eventRepo.findAll();
    }

    public Event getByPrettyName(String prettyName){
        return eventRepo.findByPrettyName(prettyName);
    }
}
