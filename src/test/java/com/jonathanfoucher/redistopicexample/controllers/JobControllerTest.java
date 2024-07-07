package com.jonathanfoucher.redistopicexample.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonathanfoucher.redistopicexample.data.JobDto;
import com.jonathanfoucher.redistopicexample.services.JobPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobController.class)
@SpringJUnitConfig(JobController.class)
class JobControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JobPublisher jobPublisher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String START_JOB_PATH = "/v1/jobs/start";
    private static final Long JOB_ID = 15L;
    private static final String JOB_NAME = "some job name";

    @Test
    void startJob() throws Exception {
        // GIVEN
        JobDto job = initJobDto();

        // WHEN / THEN
        mockMvc.perform(post(START_JOB_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(job))
                )
                .andExpect(status().isOk());

        ArgumentCaptor<JobDto> capturedJob = ArgumentCaptor.forClass(JobDto.class);
        verify(jobPublisher, times(1))
                .publish(capturedJob.capture());

        assertEquals(1, capturedJob.getAllValues().size());
        checkJob(capturedJob.getValue());
    }

    private JobDto initJobDto() {
        JobDto job = new JobDto();
        job.setId(JOB_ID);
        job.setName(JOB_NAME);
        return job;
    }

    private void checkJob(JobDto job) {
        assertNotNull(job);
        assertEquals(JOB_ID, job.getId());
        assertEquals(JOB_NAME, job.getName());
    }
}
