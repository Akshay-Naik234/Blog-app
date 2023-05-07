package com.springboot.blog.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;

@Service
public class PostServiceImpl implements PostService {

	private PostRepository postRepository;
	private ModelMapper mapper;

	@Autowired
	public PostServiceImpl(PostRepository postRepository,ModelMapper mapper) {
		this.postRepository = postRepository;
		this.mapper=mapper;
	}

	public PostDto createPost(PostDto postDto) {
		Post post = mapToEntity(postDto);
		Post newPost = postRepository.save(post);
		PostDto postRespose = mapToDTO(newPost);
		return postRespose;
	}

	@Override
	public PostResponse getAllPosts(int pageNo,int PageSize,String sortBy,String sortDir) {
		
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
				
				
		Pageable pageable = PageRequest.of(pageNo,PageSize,sort);
		
		Page<Post> posts = postRepository.findAll(pageable);

		//Get the content for page object
		List<Post> listOfPosts = posts.getContent();
		
		List<PostDto> content = listOfPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

		PostResponse postResponse = new PostResponse();
		postResponse.setContent(content);
		postResponse.setPageNo(posts.getNumber());
		postResponse.setPageSize(posts.getSize());
		postResponse.setTotalElements(posts.getTotalElements());
		postResponse.setTotalPages(posts.getTotalPages());
		postResponse.setLast(posts.isLast());
		return postResponse;
	}

	// Entity to DTO
	private PostDto mapToDTO(Post post) {
		
		PostDto postDto = mapper.map(post, PostDto.class);
		
//		PostDto postDto = new PostDto();
		
//		postDto.setId(post.getId());
//		postDto.setContent(post.getContent());
//		postDto.setDescription(post.getDescription());
//		postDto.setTitle(post.getTitle());
		return postDto;
	}

	// Converting DTO to entity
	private Post mapToEntity(PostDto postDto) {
		Post post = mapper.map(postDto, Post.class);
//		Post post = new Post();
//		post.setId(postDto.getId());
//		post.setContent(postDto.getContent());
//		post.setDescription(postDto.getDescription());
//		post.setTitle(postDto.getTitle());
		return post;
	}

	@Override
	public PostDto getPostById(long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
		return mapToDTO(post);
	}

	@Override
	public PostDto updatePost(PostDto postDto, long id) {
		// get post by id from the database
		Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
		post.setTitle(postDto.getTitle());
		post.setContent(postDto.getContent());
		post.setDescription(postDto.getDescription());
		Post updatedPost = postRepository.save(post);

		return mapToDTO(updatedPost);
	}

	@Override
	public void deletePostById(long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
		postRepository.delete(post);
	}

}
