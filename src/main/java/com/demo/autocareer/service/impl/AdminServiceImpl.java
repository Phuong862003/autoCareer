package com.demo.autocareer.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.autocareer.dto.DistrictDTO;
import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.dto.RoleDTO;
import com.demo.autocareer.dto.SemesterDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.OrganizationDTORequest;
import com.demo.autocareer.dto.request.UserDTORequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.dto.response.UserDTOResponse;
import com.demo.autocareer.dto.response.UserStaticDTOResponse;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.mapper.OrganizationMapper;
import com.demo.autocareer.mapper.RoleMapper;
import com.demo.autocareer.mapper.UserMapper;
import com.demo.autocareer.model.District;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.Role;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.User;
import com.demo.autocareer.model.enums.AccountStatus;
import com.demo.autocareer.repository.DistrictRepository;
import com.demo.autocareer.repository.OrganizationRepository;
import com.demo.autocareer.repository.RoleRepository;
import com.demo.autocareer.repository.UserRepository;
import com.demo.autocareer.service.AdminService;
import com.demo.autocareer.specification.BaseSpecification;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;
import com.demo.autocareer.utils.PageUtils;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private final BaseSpecification<User> baseSpecification = new BaseSpecification<>();
    private final BaseSpecification<Organization> baseSpecificationOrg = new BaseSpecification<>();

    @Override
    public User getFromToken() {
        String email = jwtUtil.getCurrentUserEmail(); 
        return userRepository.findByEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.STUDENT_NOT_FOUND));
    }

    @Override
    public BasePageResponse<UserDTOResponse> getListUser(BaseFilterRequest request, Pageable pageable){
        if (request.getFilters() != null) {
            request.setFilters(new HashMap<>(request.getFilters()));
        }
        Specification<User> spec = baseSpecification
                .build(request, "username", "accountStatus", "createdAt", null, null, null);
        Sort sort = baseSpecification.buildSort(request, "username");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<User> page = userRepository.findAll(spec, sortPageable);
        Page<UserDTOResponse> result = page.map(userMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }

    @Override
    public UserDTOResponse updateUser(Long id, UserDTORequest request){
        User user = userRepository.findById(id)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.USER_NOT_FOUND));
        
        userMapper.partialUpdate(user, request);

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy role phù hợp"));
            user.setRole(role);
        }

        if (request.getDistrictId() != null) {
            District district = districtRepository.findById(request.getDistrictId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy district phù hợp"));
            user.setDistrict(district);
        }
        userRepository.save(user);
        return userMapper.mapEntityToResponse(user);
    }

    @Override
    public void deleteUser(Long id){
        User user = userRepository.findById(id)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.USER_NOT_FOUND));
        
        userRepository.delete(user);
    }

    @Override
    public List<RoleDTO> findAllRole(){
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toDo)
                .collect(Collectors.toList());
    }

    @Override
    public UserStaticDTOResponse getUserStatistics() {
        long total = userRepository.count();

        // count theo status
        long activeUser = userRepository.countByAccountStatus(AccountStatus.ACTIVE);
        long inActiveUser = userRepository.countByAccountStatus(AccountStatus.INACTIVE);
        long pendingUser = userRepository.countByAccountStatus(AccountStatus.PENDING);

        // count theo role
        long compUser = userRepository.countByRoleName("ADMIN_COMPANY");
        long uniUser = userRepository.countByRoleName("ADMIN_UNIVERSITY");
        long studUser = userRepository.countByRoleName("STUDENT");

        Map<String, Long> monthUser = new LinkedHashMap<>();
        List<Object[]> results = userRepository.countUserByMonth();
        for (Object[] row : results) {
            Integer year = (Integer) row[0];
            Integer month = (Integer) row[1];
            Long count = ((Number) row[2]).longValue();
            if (year != null && month != null) {
                String key = String.format("%04d-%02d", year, month);
                monthUser.put(key, count);
            }
        }

        Map<String, Map<String, Long>> monthRoleUser = new LinkedHashMap<>();
        List<Object[]> roleResults = userRepository.countUserByMonthAndRoleName();
        for (Object[] row : roleResults) {
            Integer year = ((Number) row[0]).intValue();
            Integer month = ((Number) row[1]).intValue();
            String roleName = (String) row[2];
            Long count = ((Number) row[3]).longValue();

            String key = String.format("%04d-%02d", year, month);
            monthRoleUser.putIfAbsent(key, new LinkedHashMap<>());
            monthRoleUser.get(key).put(roleName, count);
        }

        return UserStaticDTOResponse.builder()
                .total(total)
                .activeUser(activeUser)
                .inActiveUser(inActiveUser)
                .pendingUser(pendingUser)
                .compUser(compUser)
                .uniUser(uniUser)
                .studUser(studUser)
                .monthUser(monthUser)
                .monthRoleUser(monthRoleUser)
                .build();
    }

    @Override
    public BasePageResponse<OrganizationDTO> getListOrg(BaseFilterRequest request, Pageable pageable){
        if (request.getFilters() != null) {
            request.setFilters(new HashMap<>(request.getFilters()));
        }
        Specification<Organization> spec = baseSpecificationOrg
                .build(request, "organizationName", "organizationType", "createdAt", null, null, null);
        Sort sort = baseSpecification.buildSort(request, "organizationName");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Organization> page = organizationRepository.findAll(spec, sortPageable);
        Page<OrganizationDTO> result = page.map(organizationMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }

    @Override
    public OrganizationDTO getDetailOrg(Long id){
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.COMPANY_NOT_FOUND));
        return organizationMapper.mapEntityToResponse(org);
    }

    @Override
    public OrganizationDTO updateOrg(OrganizationDTORequest request){
        Organization org = organizationRepository.findById(request.getId())
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.COMPANY_NOT_FOUND));
        
        organizationMapper.partialUpdate(org, request);
        organizationRepository.save(org);
        return organizationMapper.mapEntityToResponse(org);
    }

    @Override
    public void deleteOrg(Long id){
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.COMPANY_NOT_FOUND));
        organizationRepository.delete(org);
    }

    @Override
    public UserDTOResponse getProfileById(){
        User user = getFromToken();
        UserDTOResponse response = userMapper.mapEntityToResponse(user);
        return response;

    }
}
