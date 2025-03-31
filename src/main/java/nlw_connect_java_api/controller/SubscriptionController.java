package nlw_connect_java_api.controller;

import nlw_connect_java_api.dto.ErrorMessage;
import nlw_connect_java_api.dto.SubscriptionResponse;
import nlw_connect_java_api.exception.EventNotFoundException;
import nlw_connect_java_api.exception.SubscriptionConflictException;
import nlw_connect_java_api.exception.UserIndicadorNotFoundException;
import nlw_connect_java_api.model.User;
import nlw_connect_java_api.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SubscriptionController {

    @Autowired
    private SubscriptionService service;

    @PostMapping({"/subscription/{prettyName}", "/subscription/{prettyName}/{userId}"})
    public ResponseEntity<?> createSubscription(@PathVariable String prettyName, @RequestBody User subscriber, @PathVariable(required = false) Integer userId){
        try{
            SubscriptionResponse res = service.createNewSubscription(prettyName, subscriber, userId);
            if (res != null){
                return ResponseEntity.ok(res);
            }
            
        }catch(EventNotFoundException ex){
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
        catch(SubscriptionConflictException ex){
            return ResponseEntity.status(409).body(new ErrorMessage(ex.getMessage()));
        }
        catch(UserIndicadorNotFoundException ex){
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/subscription/{prettyName}/ranking")
    public ResponseEntity<?> generateRaningByEvent(@PathVariable String prettyName){
        try{
            return ResponseEntity.ok(service.getCompleteRanking(prettyName).subList(0, 3));
        }catch(EventNotFoundException e){
            return ResponseEntity.status(404).body(new ErrorMessage(e.getMessage()));
        }
    }

    @GetMapping("/subscription/{prettyName}/ranking/{userId}")
    public ResponseEntity<?> generateRaningByEventAndUser(@PathVariable String prettyName, @PathVariable Integer userId){
        try{
            return ResponseEntity.ok(service.getRankingByUser(prettyName, userId));
        }catch(Exception ex){
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
    } 
}
