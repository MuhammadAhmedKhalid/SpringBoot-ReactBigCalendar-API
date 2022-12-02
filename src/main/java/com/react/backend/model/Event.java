package com.react.backend.model;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document(collection = "Event")
@Data
public class Event {

	String title;
	String startDate;
	String endDate;
	String startTime;
	String endTime;
	boolean allDay;
	boolean weekly;
	String weekday;
}
