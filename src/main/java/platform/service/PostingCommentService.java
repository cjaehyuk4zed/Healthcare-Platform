package platform.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import platform.domain.Posting_Comment;
import platform.dto.postdto.PostingCommentDTO;
import platform.repository.PostCommentRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class PostingCommentService {

    private final PostCommentRepository postCommentRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    public PostingCommentDTO.Response saveNewComment(PostingCommentDTO.Request request, String postCreatorId, String postId) throws IllegalArgumentException, BadRequestException {

        String userId = authService.getUserPrincipalOrThrow();
        UUID postUuid = UUID.fromString(postId);
        LocalDateTime currentTime = LocalDateTime.now();

        Posting_Comment comment = modelMapper.map(request, Posting_Comment.class);
        comment.setLikes(0);
        comment.setUserId(userId);
        comment.setPostingId(postUuid);
        comment.setPostingCreatorId(postCreatorId);
        comment.setTimestamp(currentTime);
        comment.setDateModified(currentTime);

        // Flush changes immediately in order to map the ResponseDTO
        postCommentRepository.saveAndFlush(comment);

        return modelMapper.map(comment, PostingCommentDTO.Response.class);
    }

    public PostingCommentDTO.Response updateComment(PostingCommentDTO.Edit request, Long commentId) throws BadRequestException {
        String userId = authService.getUserPrincipalOrThrow();

        Posting_Comment comment = postCommentRepository.findByUserIdAndId(userId, commentId)
                .orElseThrow(() -> new BadRequestException("Comment does not exist"));

        comment.setCommentContent(request.getCommentContent());
        comment.setDateModified(LocalDateTime.now());

        postCommentRepository.saveAndFlush(comment);

        return modelMapper.map(comment, PostingCommentDTO.Response.class);
    }

    public PostingCommentDTO.Response deleteComment(Long commentId) throws BadRequestException, AccessDeniedException {
        String userId = authService.getUserPrincipalOrThrow();
        Posting_Comment comment = postCommentRepository.findByUserIdAndId(userId, commentId)
                .orElseThrow(() -> new BadRequestException("Comment does not exist"));

        comment.setCommentContent("This post has been deleted by the user");
        comment.setDateModified(LocalDateTime.now());

        postCommentRepository.saveAndFlush(comment);
        return modelMapper.map(comment, PostingCommentDTO.Response.class);
    }

    public PostingCommentDTO.Response getComment(String userId, Long commentId) throws BadRequestException{

        Posting_Comment comment = postCommentRepository.findByUserIdAndId(userId, commentId)
                .orElseThrow(() -> new BadRequestException("Comment does not exist"));

        return modelMapper.map(comment, PostingCommentDTO.Response.class);
    }

    public List<List<PostingCommentDTO.Response>> getComments(String postId){
        UUID postUuid = UUID.fromString(postId);
        List<Posting_Comment> listPostComments = postCommentRepository.findAllByPostingId(postUuid);
        log.info("PostingCommentService getComments");

        // Construct a deque of DTOs instead of the entity
        Deque<PostingCommentDTO.Response> deque = new ArrayDeque<PostingCommentDTO.Response>(listPostComments.size());
        for(Posting_Comment pc : listPostComments){
            deque.add(modelMapper.map(pc, PostingCommentDTO.Response.class));
        }

        listPostComments.clear();
        log.info("PostingCommentService getComments mapped by modelMapper");

        // Deque of deques of DTOs for the ResponseEntity<>
        List<List<PostingCommentDTO.Response>> listList = new ArrayList<>();

        // Pop comments from deque, and push comments w/out a parentId into listList. Push comments w/ a parentId back into deque
        int count = deque.size();
        for(int i = 0; i < count; i++){
            PostingCommentDTO.Response pc = deque.pop();
//            log.info("pc : " + pc.getPostId() + " | " + pc.getParentId() + " | " + pc.getCommentContent());
            if(pc.getParentId()==null){
                ArrayList<PostingCommentDTO.Response> tmp = new ArrayList<>();
                tmp.add(pc);
                listList.add(tmp);
//                log.info("pc : " + pc.getPostId() + " | commentId : " + pc.getCommentId() + " | parentId : " + pc.getParentId() + " | " + pc.getCommentContent());
            } else{
//                log.info("arrayList add : " + pc.getPostId() + " | commentId : " + pc.getCommentId() + " | parentId : " + pc.getParentId() + " | " + pc.getCommentContent());
                deque.add(pc);
            }
        }
        log.info("PostingCommentService getComments : separated comments with and without parentIds");

        // Now we are left with comments which need to be set to the appropriate parentId comments
        while(!deque.isEmpty()){
            PostingCommentDTO.Response pc = deque.pop();
            Long parentId = pc.getParentId();
            for(List<PostingCommentDTO.Response> responses : listList){
                if(responses.get(0).getCommentId().equals(parentId)){
                    responses.add(pc);
//                    log.info("pc : " + pc.getPostId() + " | commentId " + pc.getCommentId() + " | parentId : " + pc.getParentId() + " | " + pc.getCommentContent());
                    break;
                }
            }
        }
        log.info("PostingCommentService getComments : complete while() traversal");
//        log.info("PostingCommentService - LOGGING EACH COMMENT WITH TRAVERSAL");
//        for(List<PostingCommentDTO.Response> responses : listList){
//            log.info("Next Response List");
//            for(PostingCommentDTO.Response pc : responses){
//                log.info("pc : " + pc.getPostId() + " | commentId " + pc.getCommentId() + " | parentId : " + pc.getParentId() + " | " + pc.getCommentContent());
//            }
//        }

        return listList;
    }
}
