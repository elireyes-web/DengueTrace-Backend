package com.example.denguetracebackend.user.service;

import com.example.denguetracebackend.common.exception.ResourceNotFoundException;
import com.example.denguetracebackend.district.entity.District;
import com.example.denguetracebackend.district.repository.DistrictRepository;
import com.example.denguetracebackend.user.dto.UserResponseDTO;
import com.example.denguetracebackend.user.dto.UserUpdateRequestDTO;
import com.example.denguetracebackend.user.entity.User;
import com.example.denguetracebackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;

    public UserResponseDTO getById(Long id) {
        return UserMapper.toResponse(findUser(id));
    }

    public UserResponseDTO getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserMapper.toResponse(user);
    }

    @Transactional
    public UserResponseDTO update(Long id, UserUpdateRequestDTO request) {
        User user = findUser(id);

        if (request.fullName() != null) user.setFullName(request.fullName());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.preferredChannel() != null) user.setPreferredChannel(request.preferredChannel());
        if (request.alertRadiusKm() != null) user.setAlertRadiusKm(request.alertRadiusKm());
        if (request.districtId() != null) {
            District district = districtRepository.findById(request.districtId())
                    .orElseThrow(() -> ResourceNotFoundException.of("District", request.districtId()));
            user.setDistrict(district);
        }

        return UserMapper.toResponse(userRepository.save(user));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }
}
