package com.springboot.blog.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostDto2;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.service.PostService;
import com.springboot.blog.utils.AppConstants;

@RestController
@RequestMapping("")
public class PostController {

	private PostService postService;

	@Autowired
	public PostController(PostService postService) {
		this.postService = postService;
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/api/v1/posts")
	public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto){
		return new ResponseEntity<>(postService.createPost(postDto),HttpStatus.CREATED);
	}
	
	//get all posts rest api
	@GetMapping("/api/v1/posts")
	public PostResponse getAllPosts(
			@RequestParam(value="pageNo",defaultValue= AppConstants.DEFAULT_PAGE_NUMBER,required=false) int pageNo,
			@RequestParam(value="pageSize",defaultValue= AppConstants.DEFAULT_PAGE_SIZE,required=false) int pageSize,
			@RequestParam(value="sortBy",defaultValue = AppConstants.DEFAULT_SORT_BY,required=false) String sortBy,
			@RequestParam(value="sortDir",defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,required=false) String sortDir
			){
		return postService.getAllPosts(pageNo,pageSize,sortBy,sortDir);
	}
	
	//Versioning through URI
	
	@GetMapping(value = "/api/v1/posts/{id}")
	public ResponseEntity<PostDto> getPostByIdv1(@PathVariable("id") long id){
		return ResponseEntity.ok(postService.getPostById(id));
	}
	
	@GetMapping(value = "/api/v2/posts/{id}")
	public ResponseEntity<PostDto2> getPostByIdv2(@PathVariable("id") long id){
		PostDto postDto = postService.getPostById(id);
		PostDto2 postDto2 = new PostDto2();
		postDto2.setId(postDto.getId());
		postDto2.setTitle(postDto.getTitle());
		postDto2.setDescription(postDto.getDescription());
		postDto2.setContent(postDto.getContent());
		
		List<String> tags = new ArrayList<>();
		tags.add("Java");
		tags.add("Spring Boot");
		tags.add("AWS");
		postDto2.setTags(tags);

		return ResponseEntity.ok(postDto2);
	}
	
	//Get Post By id rest API
	//Versioning through query parameters
	// http://localhost:8080/api/posts/1?version=1
	/*
	@GetMapping(value = "/api/posts/{id}",params="version=1")
	public ResponseEntity<PostDto> getPostByIdv1(@PathVariable("id") long id){
		return ResponseEntity.ok(postService.getPostById(id));
	}
	
	@GetMapping(value = "/api/posts/{id}",params="version=2")
	public ResponseEntity<PostDto2> getPostByIdv2(@PathVariable("id") long id){
		PostDto postDto = postService.getPostById(id);
		PostDto2 postDto2 = new PostDto2();
		postDto2.setId(postDto.getId());
		postDto2.setTitle(postDto.getTitle());
		postDto2.setDescription(postDto.getDescription());
		postDto2.setContent(postDto.getContent());
		
		List<String> tags = new ArrayList<>();
		tags.add("Java");
		tags.add("Spring Boot");
		tags.add("AWS");
		postDto2.setTags(tags);

		return ResponseEntity.ok(postDto2);
	}
	
	*/
	//Versioning through Headers
	
	// Here in this headers we need to pass key as X-API-VERSION and the value is 1
	/*
	@GetMapping(value = "/api/posts/{id}",headers="X-API-VERSION=1")
	public ResponseEntity<PostDto> getPostByIdv1(@PathVariable("id") long id){
		return ResponseEntity.ok(postService.getPostById(id));
	}
	
	@GetMapping(value = "/api/posts/{id}",headers="X-API-VERSION=2")
	public ResponseEntity<PostDto2> getPostByIdv2(@PathVariable("id") long id){
		PostDto postDto = postService.getPostById(id);
		PostDto2 postDto2 = new PostDto2();
		postDto2.setId(postDto.getId());
		postDto2.setTitle(postDto.getTitle());
		postDto2.setDescription(postDto.getDescription());
		postDto2.setContent(postDto.getContent());
		
		List<String> tags = new ArrayList<>();
		tags.add("Java");
		tags.add("Spring Boot");
		tags.add("AWS");
		postDto2.setTags(tags);

		return ResponseEntity.ok(postDto2);
	}
	*/

	//Versioning through content negotiation
	// accept=application/vnd.bugbusters.v1+json
	/*
	@GetMapping(value = "/api/posts/{id}",produces="application/vnd.bugbusters.v1+json")
	public ResponseEntity<PostDto> getPostByIdv1(@PathVariable("id") long id){
		return ResponseEntity.ok(postService.getPostById(id));
	}
	
	@GetMapping(value = "/api/posts/{id}",produces="application/vnd.bugbusters.v2+json")
	public ResponseEntity<PostDto2> getPostByIdv2(@PathVariable("id") long id){
		PostDto postDto = postService.getPostById(id);
		PostDto2 postDto2 = new PostDto2();
		postDto2.setId(postDto.getId());
		postDto2.setTitle(postDto.getTitle());
		postDto2.setDescription(postDto.getDescription());
		postDto2.setContent(postDto.getContent());
		
		List<String> tags = new ArrayList<>();
		tags.add("Java");
		tags.add("Spring Boot");
		tags.add("AWS");
		postDto2.setTags(tags);

		return ResponseEntity.ok(postDto2);
	}
	*/
	
	//update post by id rest api
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/api/v1/posts/{id}")
	public ResponseEntity<PostDto> updatePost(@Valid @RequestBody PostDto postDto,@PathVariable("id") long id){
		PostDto postResponse = postService.updatePost(postDto, id);
		return new ResponseEntity<>(postResponse,HttpStatus.OK);
	}
	
	//delete post rest api
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/api/v1/posts/{id}")
	public ResponseEntity<String> deletePost(@PathVariable("id") long id){
		postService.deletePostById(id);
		return new ResponseEntity<>("Post entity deleted successfully",HttpStatus.OK);
	}
}
