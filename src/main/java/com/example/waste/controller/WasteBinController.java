package com.example.waste.controller;

import com.example.waste.model.WasteBin;
import com.example.waste.service.WasteBinService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@AllArgsConstructor
public class WasteBinController {

    WasteBinService service;

    @PostMapping("/wastebin")
    WasteBin postWasteBin(@RequestBody WasteBin wasteBin){
        if(wasteBin.getLastUpdate() == null)
            wasteBin.setLastUpdate(Instant.now());
        return service.createWasteBin(wasteBin);
    }

    @GetMapping("/wastebin/filled")
    List<WasteBin> getFilledWasteBins(){
        return service.getFilledWasteBins();
    }

    @GetMapping("/wastebin/{id}")
    WasteBin getWasteBin(@PathVariable Long id) {
        return service.getWasteBin(id);
    }
}
