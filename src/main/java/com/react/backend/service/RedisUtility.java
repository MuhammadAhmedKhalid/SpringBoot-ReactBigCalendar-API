package com.react.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.react.backend.model.Event;

@Service
public class RedisUtility {
	
	public static final String HASH_KEY = "Event";
	public static final String HASH_KEY_LISTS = "Events";
	
	@SuppressWarnings("rawtypes")
	@Qualifier("redisTemplate")
	@Autowired
	private RedisTemplate template;
	
	@SuppressWarnings("unchecked")
	public Event save(Event event)
	{
		template.opsForHash().put(HASH_KEY+event.getId(), event.getId(), event);
		template.expire(HASH_KEY+event.getId(), 60, TimeUnit.MINUTES);
		return event;
	}
	
	@SuppressWarnings("unchecked")
	public void saveAll(List<Event> events)
	{
		template.opsForHash().put(HASH_KEY_LISTS, HASH_KEY_LISTS, events);
		template.expire(HASH_KEY_LISTS, 60, TimeUnit.MINUTES);
	}
	
	@SuppressWarnings({ "unchecked" })
	public List<Event> findAll()
	{
		var list = template.opsForHash().get(HASH_KEY_LISTS, HASH_KEY_LISTS);
		if(list != null) {
			return (List<Event>) list;
		}
		return new ArrayList<Event>(); 
	}

	@SuppressWarnings("unchecked")
	public void deleteEvents() 
	{
		template.opsForHash().delete(HASH_KEY_LISTS, HASH_KEY_LISTS);
	}
}
