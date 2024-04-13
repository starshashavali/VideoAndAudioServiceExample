package com.tcs.rest;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.tcs.domain.Video;
import com.tcs.repo.VideoRepository;
import com.tcs.service.VideoFileServiceImpl;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/video")
@CrossOrigin("*")
public class VideoFileController {

    @Autowired
    private VideoFileServiceImpl videoFileService;
    
    @Autowired
    private VideoRepository videoRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("name") String name,
                                              @RequestParam("file") MultipartFile file) {
        try {
            byte[] videoData = file.getBytes();
            byte[] processedData = processVideoData(videoData);

            videoFileService.saveVideoFile(name, processedData);

            return ResponseEntity.ok().body("Video uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload video");
        }
    }

    private byte[] processVideoData(byte[] videoData) {
        return videoData;
    }
 
    @GetMapping("/videos/date")
    public ResponseEntity<List<byte[]>> getVideosByDate(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<byte[]> videoData = videoRepository.findByVideoSavedDate(date).stream()
                                                    .map(Video::getVideoData)
                                                    .collect(Collectors.toList());
            if (videoData.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(videoData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
//http://localhost:9001/api/video/today
    @GetMapping("/today")
    public ResponseEntity<List<byte[]>> getTodayVideos() {
        return getVideosByDateResponse(LocalDate.now());
    }
//http://localhost:9001/api/video/yesterday
    @GetMapping("/yesterday")
    public ResponseEntity<List<byte[]>> getYesterdayVideos() {
        return getVideosByDateResponse(LocalDate.now().minusDays(1));
    }

    private ResponseEntity<List<byte[]>> getVideosByDateResponse(LocalDate date) {
        List<byte[]> videoDataList = videoFileService.getVideosByDate(date);
        if (videoDataList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/mp4"));
        // Note: This is simplified and may not correctly handle multiple files. Adjust accordingly.
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(videoDataList);
    }


    /*

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Video>> getVideosByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Video> videos = videoFileService.getVideosByDate(localDate);
        return ResponseEntity.ok(videos);
    }


    
    @GetMapping("/{id}")
    public ResponseEntity<?> downloadVideoFile(@PathVariable Long id) throws IOException {
        byte[] videoData = videoFileService.getVideoFileDataById(id);

		ByteArrayResource resource = new ByteArrayResource(videoData);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("video/mp4"));
		headers.setContentDispositionFormData("filename", "video.mp4");

		return ResponseEntity.ok()
		        .headers(headers)
		        .contentLength(videoData.length)
		        .body(resource);
    }
    */
    
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadVideo(@PathVariable Long id) {
        try {
            byte[] videoData = videoFileService.getVideoFileDataById(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("video/mp4"));
            headers.setContentDispositionFormData("attachment", "filename=\"video.mp4\"");

            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(videoData.length)
                .body(videoData);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    @GetMapping("/all")
    public ResponseEntity<StreamingResponseBody> streamAllVideos(HttpServletResponse response) {
        response.setContentType("video/mp4");

        StreamingResponseBody stream = out -> {
            var videos = videoFileService.getAllVideoData();
            var bufferedOut = new BufferedOutputStream(out);
            try {
                for (byte[] videoData : videos) {
                    bufferedOut.write(videoData);
                }
                bufferedOut.flush(); // Ensure all data is sent to the client
            } catch (IOException e) {
                e.printStackTrace(); // Proper error handling should be implemented
            } finally {
                try {
                    bufferedOut.close(); // Properly close the buffered output
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(stream);
    }
}