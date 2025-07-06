package com.hyewon.wiseowl_backend.domain.user.service;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.user.dto.ProfileUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorRequest;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserMajor;
import com.hyewon.wiseowl_backend.domain.user.repository.ProfileRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserMajorRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRepository;
import com.hyewon.wiseowl_backend.global.exception.MajorNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.ProfileNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final UserMajorRepository userMajorRepository;
    private final MajorRepository majorRepository;



    @Transactional
    public void updateUserProfile(Long userId, ProfileUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(ProfileNotFoundException::new);

        user.updateUsername(request.name());
        profile.updateEntranceYear(request.entranceYear());

        for (UserMajorRequest majorRequest : request.majors()) {
            Major major = majorRepository.findById(majorRequest.majorId())
                    .orElseThrow(() -> new MajorNotFoundException(majorRequest.majorId()));

            UserMajor userMajor = UserMajor.of(user, major, majorRequest.majorType());
            userMajorRepository.save(userMajor);
        }


    }
}
