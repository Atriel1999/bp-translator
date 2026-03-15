package com.bptrans.bp_translator.controller;

import com.bptrans.bp_translator.service.TranslatorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TranslatorController {

	private final TranslatorService translatorService;

	public TranslatorController(TranslatorService translatorService) {
		this.translatorService = translatorService;
	}

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@PostMapping("/translate")
	@ResponseBody
	public String translate(@RequestBody java.util.Map<String, String> body) {
		return translatorService.translate(body.get("text"));
	}

	@PostMapping("/prompt")
	@ResponseBody
	public void updatePrompt(@RequestBody java.util.Map<String, String> body) {
		translatorService.setSystemPrompt(body.get("prompt"));
	}

	@GetMapping("/prompt")
	@ResponseBody
	public java.util.Map<String, String> getPrompt() {
		return java.util.Map.of("prompt", translatorService.getSystemPrompt());
	}
}