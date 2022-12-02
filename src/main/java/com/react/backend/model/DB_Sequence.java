package com.react.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "DB_sequence")
@Data
public class DB_Sequence {
	@Id
	private String id;
	private int seq;
}
