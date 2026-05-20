package com.bptrans.bp_translator.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookController {

	private static final String BASE_PATH = "src/main/resources/static/book_data/";

	@GetMapping("/api/book/progress/{user}")
	public ResponseEntity<String> getProgress(@PathVariable String user) throws IOException {
		Path path = Paths.get(BASE_PATH + "book_" + user + ".json");
		if (!Files.exists(path)) return ResponseEntity.ok("[]");
		return ResponseEntity.ok(Files.readString(path));
	}

	@PostMapping("/api/book/progress/{user}")
	public ResponseEntity<String> saveProgress(
		@PathVariable String user,
		@RequestBody String body) throws IOException {
		Path path = Paths.get(BASE_PATH + "book_" + user + ".json");
		Files.createDirectories(path.getParent());
		Files.writeString(path, body);
		return ResponseEntity.ok("{\"ok\":true}");
	}
}