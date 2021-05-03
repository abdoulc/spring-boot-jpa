package com.abdel.todo.repository;

import com.abdel.todo.domain.ToDo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ToDoRepository extends CrudRepository<ToDo,String> {
	
	
}
