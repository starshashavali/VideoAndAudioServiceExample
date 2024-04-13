package com.tcs.service;



import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.domain.Video;
import com.tcs.repo.VideoRepository;

@Service
public class VideoFileServiceImpl {

	@Autowired
	private VideoRepository videoRepository;



	public void saveVideoFile(String name, byte[] data) {
		Video video = new Video();
		video.setName(name);
		video.setVideoData(data);
		video.setVideoSavedDate(LocalDate.now());
		videoRepository.save(video);
	}
	public List<byte[]> getVideosByDate(LocalDate date) {
	    // Fetch the list of Video objects for the given date
	    List<Video> videos = videoRepository.findByVideoSavedDate(date);

	    // Map each Video object to its video data (byte array)
	    return videos.stream()
	                 .map(Video::getVideoData) // Assuming there's a getVideoData method in Video class
	                 .collect(Collectors.toList());
	}


	    public List<byte[]> getTodayVideos() {
	        return getVideosByDate(LocalDate.now());
	    }

	    public List<byte[]> getYesterdayVideos() {
	        return getVideosByDate(LocalDate.now().minusDays(1));
	    }
	

	public byte[] getVideoFileDataById(Long id) {
		Video video = videoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Video not found with id: " + id));

		if (video.getVideoData() == null) {
			throw new RuntimeException("Video data is null for video id: " + id);
		}

		return video.getVideoData();
	}
	
	 public List<byte[]> getAllVideoData() {
	        List<Video> videos = videoRepository.findAll();

	        if (videos.isEmpty()) {
	            throw new RuntimeException("No videos found");
	        }

	        return videos.stream()
	                     .map(video -> {
	                         if (video.getVideoData() == null) {
	                             throw new RuntimeException("Video data is null for video id: " + video.getId());
	                         }
	                         return video.getVideoData();
	                     })
	                     .collect(Collectors.toList());
	    }
	}