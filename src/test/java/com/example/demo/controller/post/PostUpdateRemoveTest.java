package com.example.demo.controller.post;

import static com.example.demo.controller.TestUtil.*;
import static org.assertj.core.api.Assertions.*;

import java.security.Principal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.controller.PostController;
import com.example.demo.dto.post.PostUpdateRequestDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Music;
import com.example.demo.model.post.Post;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;

@WithMockUser
@SpringBootTest
class PostUpdateRemoveTest {

	@Autowired
	private PostController postController;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	void setup() {
		Member member = createMember();
		memberRepository.save(member);
		Post post = createPost(
			member,
			new Music("1", "url", "singer", "title", Genre.EDM, "url"));
		postRepository.save(post);
	}

	@Test
	@Transactional
	void 게시글을_수정할_수_있다() {
		// given
		String changeContent = "change";
		Member member = memberRepository.findAll().get(0);
		Post post = postRepository.findAll().get(0);
		MemberDetails userDetails = new MemberDetails(member.getId().toString());
		Principal principal = new TestAuthentication(userDetails);
		PostUpdateRequestDto requestDto = PostUpdateRequestDto.builder()
			.content(changeContent)
			.build();

		// when
		ResponseEntity<Void> updatePostResponse = postController.updatePost(principal, post.getId(), requestDto);
		Optional<Post> updatedPost = postRepository.findById(post.getId());

		// then
		assertThat(updatePostResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(updatedPost).isPresent();
		assertThat(updatedPost.get().getContent()).isEqualTo(changeContent);
	}
}
