package com.theironyard.novauc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A ToDo.
 */
@Entity
@Table(name = "to_do")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ToDo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "todo_name")
    private String todoName;

    @Column(name = "todo_description")
    private String todoDescription;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTodoName() {
        return todoName;
    }

    public ToDo todoName(String todoName) {
        this.todoName = todoName;
        return this;
    }

    public void setTodoName(String todoName) {
        this.todoName = todoName;
    }

    public String getTodoDescription() {
        return todoDescription;
    }

    public ToDo todoDescription(String todoDescription) {
        this.todoDescription = todoDescription;
        return this;
    }

    public void setTodoDescription(String todoDescription) {
        this.todoDescription = todoDescription;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public ToDo creationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ToDo toDo = (ToDo) o;
        if (toDo.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, toDo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ToDo{" +
            "id=" + id +
            ", todoName='" + todoName + "'" +
            ", todoDescription='" + todoDescription + "'" +
            ", creationDate='" + creationDate + "'" +
            '}';
    }
}
