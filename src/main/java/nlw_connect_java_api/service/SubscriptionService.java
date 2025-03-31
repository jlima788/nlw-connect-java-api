package nlw_connect_java_api.service;

import nlw_connect_java_api.dto.SubscriptionRankingByUser;
import nlw_connect_java_api.dto.SubscriptionRankingItem;
import nlw_connect_java_api.dto.SubscriptionResponse;
import nlw_connect_java_api.exception.EventNotFoundException;
import nlw_connect_java_api.exception.SubscriptionConflictException;
import nlw_connect_java_api.exception.UserIndicadorNotFoundException;
import nlw_connect_java_api.model.Event;
import nlw_connect_java_api.model.Subscription;
import nlw_connect_java_api.model.User;
import nlw_connect_java_api.repo.EventRepo;
import nlw_connect_java_api.repo.SubscriptionRepo;
import nlw_connect_java_api.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class SubscriptionService {
    
    @Autowired
    private EventRepo evtRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubscriptionRepo subRepo;

    public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId){
        Event evt = evtRepo.findByPrettyName(eventName);
        if (evt == null){
            throw new EventNotFoundException("Evento " + eventName+ " não existe");
        }
        User userRec = userRepo.findByEmail(user.getEmail());
        if(userRec == null){
            userRec = userRepo.save(user);
        }

        User indicador = null;
        if(userId != null){
        indicador = userRepo.findById(userId).orElse(null);
            if (indicador == null) {
                throw new UserIndicadorNotFoundException("Usuário " +userId+ " indicador não existe");
            }
        }

        Subscription subs = new Subscription();
        subs.setEvent(evt);
        subs.setSubscriber(userRec);
        subs.setIndication(indicador);

        Subscription tmpSub = subRepo.findByEventAndSubscriber(evt, userRec);
        if(tmpSub != null){
            throw new SubscriptionConflictException("Já existe incrição para o usuário " + userRec.getName() + " no evento " + evt.getTitle());
        }

        Subscription res = subRepo.save(subs);
        return new SubscriptionResponse(res.getSubscriptionNumber(), "http://codecraft.com/subscription/"+res.getEvent().getPrettyName()+"/"+res.getSubscriber().getId());
    }

    public List<SubscriptionRankingItem> getCompleteRanking(String prettyName){
        Event evt = evtRepo.findByPrettyName(prettyName);
        if (evt ==null){
            throw new EventNotFoundException("Ranking do evento " + prettyName + " não existe");
        }
        return subRepo.generateRanking(evt.getEventId());
    }

    public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId){
        List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);
        SubscriptionRankingItem item = ranking.stream().filter(i->i.userId().equals(userId)).findFirst().orElse(null);
        if(item == null){
            throw new UserIndicadorNotFoundException("Não há inscrições com indicação do usuário "+userId);
        }
        Integer posicao = IntStream.range(0, ranking.size())
                          .filter(pos -> ranking.get(pos).userId().equals(userId))
                          .findFirst().getAsInt();
        return new SubscriptionRankingByUser(item, posicao+1);
    }
}
