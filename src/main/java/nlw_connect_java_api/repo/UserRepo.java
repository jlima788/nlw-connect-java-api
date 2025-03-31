package nlw_connect_java_api.repo;

import nlw_connect_java_api.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Integer> {
    public User findByEmail(String email);  
}
