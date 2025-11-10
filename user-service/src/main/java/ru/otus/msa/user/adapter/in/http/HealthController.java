package ru.otus.msa.user.adapter.in.http;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.otus.msa.user.api.http.dto.HealthStateDto;

@RestController
@RequestMapping("/")
public class HealthController {

    @GetMapping("health")
    public HealthStateDto healthState() {
        return new HealthStateDto("OK");
    }

}

