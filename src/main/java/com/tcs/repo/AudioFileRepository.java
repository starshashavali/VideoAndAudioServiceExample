package com.tcs.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tcs.domain.AudioFile;

import jakarta.transaction.Transactional;

@Repository
public interface AudioFileRepository extends JpaRepository<AudioFile, Long> {
	
	
    List<AudioFile> findByAlbumIdOrderByIdAsc(Long albumId);

	
	 @Transactional
	    @Modifying
	    @Query("DELETE FROM AudioFile af WHERE af.createdAt <= :threshold")
	    void deleteAllOlderThan(LocalDateTime threshold);
}
