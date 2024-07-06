package com.jonathanfoucher.redistopicexample.controllers;

import com.jonathanfoucher.redistopicexample.data.JobDto;
import com.jonathanfoucher.redistopicexample.services.JobQueuePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobQueuePublisher jobQueuePublisher;

    @PostMapping("/start")
    public void startJob(@RequestBody JobDto job) {
        jobQueuePublisher.publish(job);
    }
}
