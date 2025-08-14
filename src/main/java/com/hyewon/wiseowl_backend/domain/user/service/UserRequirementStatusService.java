package com.hyewon.wiseowl_backend.domain.user.service;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.service.MajorRequirementQueryService;
import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserRequirementStatus;
import com.hyewon.wiseowl_backend.domain.user.repository.ProfileRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserMajorRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRequirementStatusRepository;
import com.hyewon.wiseowl_backend.global.exception.ProfileNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRequirementStatusService {
    private final UserMajorRepository userMajorRepository;
    private final MajorRequirementQueryService majorRequirementQueryService;
    private final UserRepository userRepository;
    private final UserRequirementStatusRepository userRequirementStatusRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void replaceUserRequirementStatus(Long userId, List<UserMajorUpdateRequest> requests) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Profile profile = profileRepository.findByUserId(userId).orElseThrow(ProfileNotFoundException::new);

        requests.forEach(req -> {
            userMajorRepository.deleteAllByUserIdAndMajorType(userId, req.majorType());

            List<MajorRequirement> applicable =
                    majorRequirementQueryService.getApplicableRequirements(
                            req.majorId(),
                            req.majorType(),
                            profile.getEntranceYear()
                    );
            List<UserRequirementStatus> toSave = applicable.stream()
                    .map(ar -> UserRequirementStatus.of(user, ar))
                    .toList();

            userRequirementStatusRepository.saveAll(toSave);
        });
    }
}
