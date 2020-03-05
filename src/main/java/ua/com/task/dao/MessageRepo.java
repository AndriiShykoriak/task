package ua.com.task.dao;

import org.springframework.data.repository.CrudRepository;
import ua.com.task.entity.Message;

import java.util.List;

public interface MessageRepo extends CrudRepository<Message, Long> {

    List<Message> findByTag(String tag);


}
