package com.react.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "Event")
@Data
public class Event {

	@Transient
	public static final String SEQUENCE_NAME="events_sequence";
	
	@Id
	Integer id;
	String title;
	String startDate;
	String endDate;
	String startTime;
	String endTime;
	String weekday;
	boolean allDay;
	boolean weekly;
	boolean everyday;
}
