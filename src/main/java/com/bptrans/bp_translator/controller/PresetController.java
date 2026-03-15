package com.bptrans.bp_translator.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api/preset")
public class PresetController {

	private static final String FILE_PATH = "presets.json";
	private final ObjectMapper mapper = new ObjectMapper();

	private List<Map<String, String>> load() {
		File f = new File(FILE_PATH);
		if (!f.exists()) return new ArrayList<>();
		try {
			return mapper.readValue(f, new TypeReference<>() {});
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	private void save(List<Map<String, String>> list) {
		try {
			mapper.writeValue(new File(FILE_PATH), list);
		} catch (Exception ignored) {}
	}

	@GetMapping
	public List<Map<String, String>> getAll() {
		return load();
	}

	@PostMapping
	public Map<String, String> add(@RequestBody Map<String, String> preset) {
		List<Map<String, String>> list = load();
		preset.put("id", UUID.randomUUID().toString());
		list.add(preset);
		save(list);
		return preset;
	}

	@PutMapping("/{id}")
	public void update(@PathVariable String id, @RequestBody Map<String, String> preset) {
		List<Map<String, String>> list = load();
		for (Map<String, String> p : list) {
			if (p.get("id").equals(id)) {
				p.put("label", preset.get("label"));
				p.put("text", preset.get("text"));
				break;
			}
		}
		save(list);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable String id) {
		List<Map<String, String>> list = load();
		list.removeIf(p -> p.get("id").equals(id));
		save(list);
	}
}