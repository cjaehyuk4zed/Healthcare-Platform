package platform.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import platform.domain.*;
import platform.dto.ImageDTO;
import platform.dto.postdto.*;
import platform.dto.postdto.PostingImageDTO;
import platform.dto.userdto.UserInfoDTO;
import platform.dto.userdto.UserInterestDTO;
import platform.dto.userdto.UserTabDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

// Add ModelMapper as a Spring Bean so that it can be used anywhere
// But do not use ModelMapper where a type conversion is needed!!
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Indicates that methods should be eligible for matching at the given {@code accessLevel}.
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // Sets whether deep copy should be enabled. Merge matching elements, and keep the rest w/out setting null or default values
        modelMapper.getConfiguration().setSkipNullEnabled(true);



        modelMapper.addMappings(new PropertyMap<PostingImageDTO, Posting_Image>() {
            @Override
            protected void configure() {
                map().getPostingImageCompositeKey().setImageName(source.getImageName());
                map().getPostingImageCompositeKey().setUserId(source.getUserId());
            }
        });

        // Delete this once migration to PostingImageDTO is complete?
        // Delete this once migration to PostingImageDTO is complete?
        // Delete this once migration to PostingImageDTO is complete?
        modelMapper.addMappings(new PropertyMap<Posting_Image, PostingImageDTO>() {
            @Override
            protected void configure() {
                map().setImageName(source.getPostingImageCompositeKey().getImageName());
                map().setUserId(source.getPostingImageCompositeKey().getUserId());
            }
        });

        modelMapper.addMappings(new PropertyMap<PostingImageDTO.Save, Posting_Image>() {
            @Override
            protected void configure() {
                map().getPostingImageCompositeKey().setImageName(source.getImageName());
                map().getPostingImageCompositeKey().setUserId((source.getUserId()));
            }
        });

        modelMapper.addMappings(new PropertyMap<PostingAttachmentDTO, Posting_Attachment>() {
            @Override
            protected void configure() {
                map().getPostingAttachmentCompositeKey().modelMapperToUUID(source.getAttachmentId());
                map().getPostingAttachmentCompositeKey().setUserId(source.getUserId());
            }
        });

        modelMapper.addMappings(new PropertyMap<Posting_Attachment, PostingAttachmentDTO>() {
            @Override
            protected void configure() {
                map().setAttachmentId(source.getPostingAttachmentCompositeKey().modelMapperToString());
                map().setUserId(source.getPostingAttachmentCompositeKey().getUserId());
            }
        });

        modelMapper.addMappings(new PropertyMap<Posting_Attachment, PostingAttachmentDTO.Response>() {
            @Override
            protected void configure(){
                map().setAttachmentId(source.getPostingAttachmentCompositeKey().modelMapperToString());
                map().setUserId(source.getPostingAttachmentCompositeKey().getUserId());
            }
        });

        modelMapper.addMappings(new PropertyMap<Posting_Info, PostingPreviewDTO>() {
            @Override
            protected void configure(){
                map().formatDateCreated(source.getTimestamp());
            }
        });

        modelMapper.addMappings(new PropertyMap<Posting_Info, PostInfoResponseDTO>() {
            @Override
            protected void configure() {
                map().formatTimestamp(source.getTimestamp());
            }
        });

        modelMapper.addMappings(new PropertyMap<Posting_Comment, PostingCommentDTO.Response>() {
            @Override
            protected void configure() {
                map().setCommentId(source.getId());
                map().formatDateCreated(source.getTimestamp());
                map().setDateModifiedFormatted(source.getDateModified());
            }
        });

        modelMapper.addMappings(new PropertyMap<UserInterestDTO, User_Interest>() {
            @Override
            protected void configure() {
                map().getUserInterestCompositeKey().setUserId(source.getUserId());
                map().getUserInterestCompositeKey().setCategory(source.getCategory());
                map().getUserInterestCompositeKey().setSubcategory(source.getSubcategory());
                map().setTimestamp(LocalDateTime.now());
            }
        });

        modelMapper.addMappings(new PropertyMap<UserInterestDTO.Save, User_Interest>() {
            @Override
            protected void configure() {
                map().getUserInterestCompositeKey().setUserId(source.getUserId());
                map().getUserInterestCompositeKey().setCategory(source.getCategory());
                map().getUserInterestCompositeKey().setSubcategory(source.getSubcategory());
                map().setTimestamp(LocalDateTime.now());
            }
        });

        modelMapper.addMappings(new PropertyMap<Posting_Info, PostInfoResponseDTO.Draft>() {
            @Override
            protected void configure(){
                map().formatTimestamp(source.getTimestamp());
            }
        });

        modelMapper.addMappings(new PropertyMap<User_Info, UserInfoDTO.Response>() {
            @Override
            protected void configure(){
                map().formatTimestamp(source.getTimestamp());
                map().formatDateRegistered(source.getDateRegistered());
                map().formatDateOfBirth(source.getDateOfBirth());
            }
        });

        modelMapper.addMappings(new PropertyMap<ImageDTO, Image>() {
            @Override
            protected void configure() {
                map().modelMapperToUUID(source.getImageId());
            }
        });

        modelMapper.addMappings(new PropertyMap<User_Tab, UserTabDTO.Response>() {
            @Override
            protected void configure() {
                map().formatTimestamp(source.getTimestamp());
            }
        });

        return modelMapper;
    }
}