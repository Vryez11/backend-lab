package com.vryez.backendlab.lab28;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoTransferService {

    private final VideoTransferRepository repository;

    public void transferAll(long fromChannelId, long toChannelId, List<Long> videoIds)
            throws LockedVideoException {
        for (Long videoId : videoIds) {
            transferOne(videoId, fromChannelId, toChannelId);
        }
    }

    @Transactional(rollbackFor = LockedVideoException.class)
    public void transferOne(long videoId, long fromChannelId, long toChannelId)
            throws LockedVideoException {
        String status = repository.findStatus(videoId);
        if ("LOCKED".equals(status)) {
            throw new LockedVideoException(videoId);
        }
        repository.updateChannelId(videoId, toChannelId);
        repository.addVideoCount(fromChannelId, -1);
        repository.addVideoCount(toChannelId, +1);
        repository.insertTransferLog(videoId, fromChannelId, toChannelId);
    }
}
