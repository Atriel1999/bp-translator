package com.bptrans.bp_translator.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TranslatorService {

	@Value("${anthropic.api.key}")
	private String apiKey;

	@Value("${anthropic.api.url}")
	private String apiUrl;

	private String systemPrompt;
	private String defaultPrompt;

	@PostConstruct
	public void init() throws IOException {
		String promptTemplate = new String(
			new ClassPathResource("config/prompt.txt").getInputStream().readAllBytes(),
			StandardCharsets.UTF_8
		);

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> dictionary = mapper.readValue(
			new ClassPathResource("config/dictionary.json").getInputStream(),
			new TypeReference<>() {}
		);

		String dictStr = dictionary.entrySet().stream()
			.map(e -> e.getKey() + ": " + e.getValue())
			.collect(Collectors.joining(" / "));

		defaultPrompt = promptTemplate.replace("{DICTIONARY}", dictStr);
		systemPrompt = defaultPrompt;
	}

	public String translate(String text) {
		RestClient restClient = RestClient.create();
		Map<String, Object> requestBody = Map.of(
			"model", "claude-sonnet-4-20250514",
			"max_tokens", 1024,
			"system", systemPrompt,
			"messages", List.of(Map.of("role", "user", "content", text))
		);

		Map response = restClient.post()
			.uri(apiUrl)
			.header("x-api-key", apiKey)
			.header("anthropic-version", "2023-06-01")
			.header("Content-Type", "application/json")
			.body(requestBody)
			.retrieve()
			.body(Map.class);

		List<Map> content = (List<Map>) response.get("content");
		return (String) content.get(0).get("text");
	}

	public boolean isJapanese(String text) {
		return text.chars().anyMatch(c ->
			Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HIRAGANA ||
				Character.UnicodeBlock.of(c) == Character.UnicodeBlock.KATAKANA ||
				Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
		);
	}

	public String getSystemPrompt() { return systemPrompt; }
	public void setSystemPrompt(String prompt) { systemPrompt = prompt; }
	public String getDefaultPrompt() { return defaultPrompt; }
}