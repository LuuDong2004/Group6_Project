package group6.cinema_project.controller;

import group6.cinema_project.repository.DiscountCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/discount")
public class DiscountCodeApiController {
    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    @GetMapping("/check")
    public ResponseEntity<?> checkDiscount(@RequestParam String code) {
        return discountCodeRepository.findValidCode(code, LocalDate.now())
            .map(dc -> {
                if (dc.getDiscountPercent() > 0) {
                    return ResponseEntity.ok(Map.of("valid", true, "type", "percent", "value", dc.getDiscountPercent()));
                } else if (dc.getDiscountAmount() > 0) {
                    return ResponseEntity.ok(Map.of("valid", true, "type", "amount", "value", dc.getDiscountAmount()));
                } else {
                    return ResponseEntity.ok(Map.of("valid", false));
                }
            })
            .orElse(ResponseEntity.ok(Map.of("valid", false)));
    }
} 