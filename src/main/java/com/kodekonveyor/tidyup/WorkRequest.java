package com.kodekonveyor.tidyup;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

@Generated
@Data
@Entity
@NoArgsConstructor
public class WorkRequest {
	@Id
	@GeneratedValue
	private Long identifier;

	private String city;
	private String description;

	@JsonIgnore
	@ManyToOne
	private TidyUser user;
}
