package com.abdel.todo.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.Optional;
import com.abdel.todo.domain.ToDo;
import com.abdel.todo.domain.ToDoBuilder;
import com.abdel.todo.repository.ToDoRepository;
import com.abdel.todo.validation.ToDoValidationError;
import com.abdel.todo.validation.ToDoValidationErrorBuilder;

@RestController
@RequestMapping("/api")
public class ToDoController {
	
	
	private ToDoRepository repository;
	@Autowired
	public ToDoController(ToDoRepository toDoRepository) {
	this.repository = toDoRepository;
	}
	@GetMapping("/todos")
	public ResponseEntity<Iterable<ToDo>> getTodos(){
		return ResponseEntity.ok(repository.findAll());
	}
	
	@GetMapping("/todo/{id}")
	public ResponseEntity<ToDo> getTodo(@PathVariable String id) {
		Optional<ToDo> toDo = repository.findById(id);
		if(toDo.isPresent())
				return ResponseEntity.ok(toDo.get());
		return ResponseEntity.notFound().build();
	}
	
	@PatchMapping("/todo/{id}")
	public ResponseEntity<ToDo> setCompleted(@PathVariable String id){
		Optional<ToDo> toDo = repository.findById(id);
		if(!toDo.isPresent())
			return ResponseEntity.notFound().build();
		ToDo result = toDo.get();
		result.setCompleted(true);
		repository.save(result);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.buildAndExpand(result.getId()).toUri();
		return ResponseEntity.ok().header("Location",location.toString()).
				build();
	}
	
	@RequestMapping(value="/todo", method= {RequestMethod.POST, RequestMethod.PUT})
	public ResponseEntity<?> createToDo(@Valid @RequestBody ToDo toDo, Errors errors){
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().
					body(ToDoValidationErrorBuilder.fromBindingErrors(errors));
		}
		ToDo result = repository.save(toDo);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().
				path("/{id}")
				.buildAndExpand(result.getId()).toUri();
				return ResponseEntity.created(location).build();
	}
	
	@DeleteMapping("/todo/{id}")
	public ResponseEntity<ToDo> deleteToDo(@PathVariable String id){
		repository.delete(ToDoBuilder.create().withId(id).build());
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/todo")
	public ResponseEntity<ToDo> deleteToDo(@RequestBody ToDo toDo){
	repository.delete(toDo);
	return ResponseEntity.noContent().build();
	}
	
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ToDoValidationError handleException(Exception exception) {
	return new ToDoValidationError(exception.getMessage());
	}
}
