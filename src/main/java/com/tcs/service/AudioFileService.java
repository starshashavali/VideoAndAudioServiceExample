package com.tcs.service;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tcs.domain.AudioFile;
import com.tcs.repo.AudioFileRepository;
@Service
public class AudioFileService {
    @Autowired
    private AudioFileRepository audioFileRepository;
    
    
    private Iterator<AudioFile> audioFileIterator;


    public byte[] saveAudioFile(String name, byte[] data) {
        AudioFile audioFile = new AudioFile();
        audioFile.setName(name);
        audioFile.setData(data);
        AudioFile savedAudioFile = audioFileRepository.save(audioFile);
        return savedAudioFile.getData();
    }
    @Scheduled(fixedRate = 3600000)
    public void deleteOldFiles() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        audioFileRepository.deleteAllOlderThan(threshold);
    }
    
    
   /*

    public byte[] getAudioFileDataById(Long id) throws IOException {
        AudioFile audioFile = audioFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Audio file not found with id: " + id));

        byte[] audioData = audioFile.getData();
        if (audioData != null) {
            return audioData;
        } else {
            throw new IllegalArgumentException("Audio file data is null");
        }
    }
    
    */
    
    public byte[] getNextAudioFileData(Long albumId) throws IOException {
        if (audioFileIterator == null || !audioFileIterator.hasNext()) {
            List<AudioFile> audioFiles = audioFileRepository.findByAlbumIdOrderByIdAsc(albumId);
            audioFileIterator = audioFiles.iterator();
        }
        if (audioFileIterator.hasNext()) {
            AudioFile audioFile = audioFileIterator.next();
            return audioFile.getData();
        } else {
            throw new IllegalStateException("No more audio files in the album.");
        }
    }
}




