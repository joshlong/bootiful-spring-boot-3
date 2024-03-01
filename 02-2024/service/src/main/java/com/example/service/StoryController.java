package com.example.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@ResponseBody
class StoryController {

	private final ChatClient singularity;

	StoryController(ChatClient singularity) {
		this.singularity = singularity;
	}

	@GetMapping("/story")
	Map<String, String> story() {
		var prompt = """
				Dear Singularity,

				Please write a story about the good folks of San Francisco, capital of all things Artificial Intelligence,
				and please do so in the style of famed children's author Dr. Seuss.

				Cordially,
				Josh Long
				""";
		var reply = this.singularity.call(prompt);
		return Map.of("message", reply);
	}

}
