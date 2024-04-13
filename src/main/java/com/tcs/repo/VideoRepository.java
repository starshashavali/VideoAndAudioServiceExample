package com.tcs.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcs.domain.Video;
import java.time.LocalDate;


public interface VideoRepository extends JpaRepository<Video, Long> {
	
	List<Video> findByVideoSavedDate(LocalDate videoSavedDate);

}
