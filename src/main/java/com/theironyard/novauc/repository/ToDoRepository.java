package com.theironyard.novauc.repository;

import com.theironyard.novauc.domain.ToDo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ToDo entity.
 */
@SuppressWarnings("unused")
public interface ToDoRepository extends JpaRepository<ToDo,Long> {

}
