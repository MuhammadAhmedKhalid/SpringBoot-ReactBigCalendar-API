package com.react.backend.resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.react.backend.dao.EventRepo;
import com.react.backend.model.Event;
import com.react.backend.service.RedisUtility;

@RestController
public class EventController {
	
	@Autowired
	RedisUtility redisUtility;
	
	@Autowired
	EventRepo eventRepo;
	
	@Autowired
	SequenceGeneratorService service;
	
	@SuppressWarnings({ "static-access", "rawtypes" })
	@CrossOrigin(origins = "http://localhost:3000/")
	@PostMapping("addEvent")
	public ResponseEntity addEvent(@RequestBody Event event)
	{
		event.setId(service.getSequenceNumber(event.SEQUENCE_NAME));
		eventRepo.save(event);
		redisUtility.deleteEvents();
		return ResponseEntity.ok(null);
	}
	
	@SuppressWarnings({ "rawtypes", "static-access" })
	@CrossOrigin(origins = "http://localhost:3000/")
	@PostMapping("addEventWeekly")
	public ResponseEntity addEventWeekly(@RequestBody Event event) throws ParseException 
	{
		ArrayList<String> dates = datesList(event);
		
		HashMap<Integer, String> days = new HashMap<Integer, String>();
		days.put(1, "Monday");
		days.put(2, "Tuesday");
		days.put(3, "Wednesday");
		days.put(4, "Thursday");
		days.put(5, "Friday");
		days.put(6, "Saturday");
		days.put(7, "Sunday");
		
		int weekdayNum = 0;
		 for (Map.Entry<Integer, String> set :days.entrySet()) 
		 {
			 if(set.getValue().equals(event.getWeekday())) 
			 {
				 weekdayNum = set.getKey();
			 }
		 }
		 
		 ArrayList<String> dateList = new ArrayList<>();
		 int numOfDates = 0;
		for(int i=0; i<dates.size(); i++)
		{		  
			if(stringToDate(dates.get(i)).getDayOfWeek().getValue() == weekdayNum)
			{
				numOfDates=numOfDates+1;
				dateList.add(dates.get(i));
			}
		}
		
		for(int i=0; i<numOfDates; i++)
		{
			event.setId(service.getSequenceNumber(event.SEQUENCE_NAME));
			event.setStartDate(dateList.get(i));
			event.setEndDate(dateList.get(i));
			eventRepo.save(event);
			redisUtility.deleteEvents();
		}
		
		return ResponseEntity.ok(null);
	}
	
	@SuppressWarnings({ "rawtypes", "static-access" })
	@CrossOrigin(origins = "http://localhost:3000/")
	@PostMapping("addEventEveryday")
	public ResponseEntity addEventEveryday(@RequestBody Event event) throws ParseException
	{
		ArrayList<String> dates = datesList(event);
		for(int i=0; i<dates.size(); i++) 
		{
			event.setId(service.getSequenceNumber(event.SEQUENCE_NAME));
			event.setStartDate(dates.get(i));
			event.setEndDate(dates.get(i));
			eventRepo.save(event);
			redisUtility.deleteEvents();
		}
		return ResponseEntity.ok(null);
	}
	
	@CrossOrigin(origins = "http://localhost:3000/")
	@GetMapping("getEvents")
	public List<Event> getEvents()
	{
		var events = redisUtility.findAll();
		
		if (events.isEmpty()) {				
			events= eventRepo.findAll();
			redisUtility.saveAll(events);
		}
		return events;
	}
	
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "http://localhost:3000/")
	@DeleteMapping("deleteEvent/{id}")
	public ResponseEntity deleteEvent(@PathVariable("id") int id) 
	{
		redisUtility.deleteEvents();
		eventRepo.deleteById(id);
		return ResponseEntity.ok(null);
	}
	
	public ArrayList<String> datesList(Event event) throws ParseException 
	{
		ArrayList<String> dates_list = new ArrayList<>();
		Date dFrom = new SimpleDateFormat("yyyy-MM-dd").parse(event.getStartDate());
		Date dTo = new SimpleDateFormat("yyyy-MM-dd").parse(event.getEndDate());
		long t1 = dFrom.getTime();
		long t2 = dTo.getTime();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

		if(t1<t2) {
			for(long i=t1; i<=t2;i+=86400000)
			{
				dates_list.add(f.format(i));
			}
		}else {
			dates_list.add(f.format(dFrom));
		}
		return dates_list;
	}
	
	public LocalDate stringToDate(String str_date)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
		LocalDate date = LocalDate.parse(str_date, formatter);
		return date;
	}
}
