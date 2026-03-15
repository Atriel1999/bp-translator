package com.bptrans.bp_translator.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api/preset")
public class PresetController {

	private static final List<String> USERS = List.of("Atriel", "kuku");
	private final ObjectMapper mapper = new ObjectMapper();

	private String filePath(String user) {
		return "src/main/resources/config/presets_" + user.toLowerCase() + ".json";
	}

	private List<Map<String, String>> load(String user) {
		File f = new File(filePath(user));
		if (!f.exists()) return new ArrayList<>();
		try {
			return mapper.readValue(f, new TypeReference<>() {});
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	private void save(String user, List<Map<String, String>> list) {
		try {
			mapper.writeValue(new File(filePath(user)), list);
		} catch (Exception ignored) {}
	}

	@GetMapping("/{user}")
	public List<Map<String, String>> getAll(@PathVariable String user) {
		return load(user);
	}

	@PostMapping("/{user}")
	public Map<String, String> add(@PathVariable String user, @RequestBody Map<String, String> preset) {
		List<Map<String, String>> list = load(user);
		preset.put("id", UUID.randomUUID().toString());
		list.add(preset);
		save(user, list);
		return preset;
	}

	@PutMapping("/{user}/{id}")
	public void update(@PathVariable String user, @PathVariable String id, @RequestBody Map<String, String> preset) {
		List<Map<String, String>> list = load(user);
		for (Map<String, String> p : list) {
			if (p.get("id").equals(id)) {
				p.put("label", preset.get("label"));
				p.put("text", preset.get("text"));
				break;
			}
		}
		save(user, list);
	}

	@DeleteMapping("/{user}/{id}")
	public void delete(@PathVariable String user, @PathVariable String id) {
		List<Map<String, String>> list = load(user);
		list.removeIf(p -> p.get("id").equals(id));
		save(user, list);
	}

	@PostMapping("/{user}/reorder")
	public void reorder(@PathVariable String user, @RequestBody List<String> ids) {
		List<Map<String, String>> list = load(user);
		List<Map<String, String>> reordered = new ArrayList<>();
		for (String id : ids) {
			list.stream().filter(p -> p.get("id").equals(id)).findFirst().ifPresent(reordered::add);
		}
		save(user, reordered);
	}
}