package com.github.gomestkd.eventmanagement.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Relation(collectionRelation = "events")
@JsonPropertyOrder({
    "id", "name", "description", "start_time", "end_time", "location", "created_at", "updated_at"
})
public class EventDTO extends RepresentationModel<ItemDTO> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private Date startTime;
    private Date endTime;
    private String location;
    private Date createdAt;
    private Date updatedAt;

    public EventDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EventDTO eventDTO)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getId(), eventDTO.getId()) && Objects.equals(getName(), eventDTO.getName()) && Objects.equals(getDescription(), eventDTO.getDescription()) && Objects.equals(getStartTime(), eventDTO.getStartTime()) && Objects.equals(getEndTime(), eventDTO.getEndTime()) && Objects.equals(getLocation(), eventDTO.getLocation()) && Objects.equals(getCreatedAt(), eventDTO.getCreatedAt()) && Objects.equals(getUpdatedAt(), eventDTO.getUpdatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getName(), getDescription(), getStartTime(), getEndTime(), getLocation(), getCreatedAt(), getUpdatedAt());
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location='" + location + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
