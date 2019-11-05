package com.kodekonveyor.tidyup;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@NoArgsConstructor
public class WorkRequest {
	@Id
	@GeneratedValue
	private Long id;

	private String city;
	private String description;

	@JsonIgnore
	@ManyToOne
	private TidyUser user;
}
