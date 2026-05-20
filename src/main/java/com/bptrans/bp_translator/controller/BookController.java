package com.bptrans.bp_translator.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

	private static final String BOOK_PATH = "src/main/resources/static/book_data/book.json";

	@GetMapping("/api/book/progress")
	public ResponseEntity<String> getProgress() throws IOException {
		Path path = Paths.get(BOOK_PATH);
		if (!Files.exists(path)) return ResponseEntity.ok("[]");
		return ResponseEntity.ok(Files.readString(path));
	}

	@PostMapping("/api/book/progress")
	public ResponseEntity<String> saveProgress(@RequestBody String body) throws IOException {
		Path path = Paths.get(BOOK_PATH);
		Files.createDirectories(path.getParent());
		Files.writeString(path, body);
		return ResponseEntity.ok("{\"ok\":true}");
	}
}