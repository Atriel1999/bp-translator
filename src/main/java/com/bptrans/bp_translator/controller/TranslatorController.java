package com.bptrans.bp_translator.controller;

import java.util.Map;

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
	public Map<String, String> translate(@RequestBody Map<String, String> body) {
		String input = body.get("text");
		String result = translatorService.translate(input);
		boolean inputIsJp = translatorService.isJapanese(input);
		return Map.of(
			"result", result,
			"jpText", inputIsJp ? input : result
		);
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