package com.demo.autocareer.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.dto.StudentDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.InternRequestApprovedDTORequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternDeclareRequestDTOResponse;
import com.demo.autocareer.dto.response.InternRequestApprovedDTOReponse;
import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.excel.WriteStudentExcel;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.filter.StudentInternFilter;
import com.demo.autocareer.mapper.InternDeclareApprovedMapper;
import com.demo.autocareer.mapper.InternDeclareRequestMapper;
import com.demo.autocareer.mapper.OrganizationMapper;
import com.demo.autocareer.mapper.StudentMapper;
import com.demo.autocareer.model.District;
import com.demo.autocareer.model.InternDeclareRequest;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.OrganizationFaculty;
import com.demo.autocareer.model.Role;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.SubField;
import com.demo.autocareer.model.User;
import com.demo.autocareer.model.enums.AccountStatus;
import com.demo.autocareer.model.enums.Gender;
import com.demo.autocareer.model.enums.OrganizationType;
import com.demo.autocareer.model.enums.StatusIntern;
import com.demo.autocareer.repository.DistrictRepository;
import com.demo.autocareer.repository.InternDeclareRequestRepository;
import com.demo.autocareer.repository.OrganizationFacultyRepository;
import com.demo.autocareer.repository.OrganizationRepository;
import com.demo.autocareer.repository.RoleRepository;
import com.demo.autocareer.repository.StudentRepository;
import com.demo.autocareer.repository.SubFieldRepository;
import com.demo.autocareer.repository.UserRepository;
import com.demo.autocareer.service.UniversityService;
import com.demo.autocareer.specification.BaseSpecification;
import com.demo.autocareer.specification.StudentSpecification;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;
import com.demo.autocareer.utils.PageUtils;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UniversityServiceImpl implements UniversityService{
    private static final Logger log = LoggerFactory.getLogger(UniversityServiceImpl.class);

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OrganizationFacultyRepository organizationFacultyRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private SubFieldRepository subFieldRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private InternDeclareRequestRepository internDeclareRequestRepository;
    @Autowired
    private InternDeclareRequestMapper internDelcareRequestMapper;
    @Autowired
    private InternDeclareApprovedMapper internDeclareApprovedMapper;
    @Autowired
    private WriteStudentExcel writeStudentExcel;


    private final BaseSpecification<Student> baseSpecification = new BaseSpecification<>();
    private final StudentSpecification studentSpecification = new StudentSpecification();
    private final BaseSpecification<Organization> baseSpecificationUni = new BaseSpecification<>();


    @Override
    public Organization getUniFromToken() {
        String email = jwtUtil.getCurrentUserEmail(); 
        return organizationRepository.findByUserEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.COMPANY_NOT_FOUND));
    }

    @Override
    public void bulkCreateStudents(MultipartFile file) {
        List<StudentDTO> students = parseFile(file);
        if (students.isEmpty()) {
            log.warn("⚠️ No valid student data found in the uploaded file.");
            throw new RuntimeException("Không có dòng dữ liệu nào hợp lệ trong file.");
        }

        Role studentRole = roleRepository.findByRoleName("STUDENT")
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.ROLE_NOT_FOUND));
        log.info("🔑 Found 'STUDENT' role with ID: {}", studentRole.getId());

        int count = 0;
        for (StudentDTO dto : students) {
            count++;
            try {
                if (userRepository.existsByEmail(dto.getEmail())) {
                    log.warn("⚠️ Email {} đã tồn tại. Bỏ qua sinh viên này.", dto.getEmail());
                    continue;
                }

                User user = new User();
                user.setEmail(dto.getEmail());
                user.setUsername(dto.getName());
                user.setPhoneNumber(dto.getPhoneNumber());
                String rawPassword = generateRandomPassword();
                user.setPassword(passwordEncoder.encode(rawPassword));
                user.setAccountStatus(AccountStatus.PENDING);
                user.setRole(studentRole);
                user.setEnabled(false);
                userRepository.save(user);

                Student student = new Student();
                student.setUser(user);
                student.setName(dto.getName());
                student.setStudentCode(dto.getStudentCode());
                student.setDob(dto.getDob());
                student.setPhoneNumber(dto.getPhoneNumber());
                student.setEmail(dto.getEmail());
                student.setGraduatedYear(dto.getGraduatedYear());
                student.setGender(dto.getGender());

                if (dto.getDistrictId() != null) {
                    District district = districtRepository.findById(dto.getDistrictId())
                            .orElseThrow(() -> new RuntimeException("District not found with ID: " + dto.getDistrictId()));
                    student.setDistrict(district);
                }

                if (dto.getOrganizationFacultyId() != null) {
                    OrganizationFaculty orgFaculty = organizationFacultyRepository.findById(dto.getOrganizationFacultyId())
                            .orElseThrow(() -> new RuntimeException("OrganizationFaculty not found with ID: " + dto.getOrganizationFacultyId()));
                    student.setOrganizationFaculty(orgFaculty);
                }

                studentRepository.save(student);
                try {
                    sendAccountCreatedEmail(dto.getEmail(), dto.getName(), rawPassword);
                } catch (Exception e) {
                    log.error("❌ Gửi email thất bại cho {}: {}", dto.getEmail(), e.getMessage(), e);
                }

            } catch (Exception e) {
                log.error("❌❌❌ Lỗi khi xử lý sinh viên {}: {}", dto.getEmail(), e.getMessage(), e);
            }

        }
    }


    private void sendAccountCreatedEmail(String email, String username, String password) {
        if (email == null || email.trim().isEmpty() || !email.contains("@") || email.trim().split("@").length != 2) {
            log.warn("❌ Email không hợp lệ, bỏ qua gửi mail: '{}'", email);
            return;
        }

        try {
            String token = jwtUtil.generateVerificationToken(email);  // thời hạn ngắn 15 phút nếu có cấu hình
            String verifyLink = "http://localhost:8080/auth/verify?token=" + token;

            String messageBody = "Xin chào " + username + ",\n\n"
                    + "Tài khoản sinh viên của bạn đã được tạo thành công.\n\n"
                    + "📌 Tên đăng nhập: " + email + "\n"
                    + "🔐 Mật khẩu tạm thời: " + password + "\n\n"
                    + "👉 Vui lòng xác minh tài khoản bằng cách nhấn vào đường link dưới đây:\n"
                    + verifyLink + "\n\n"
                    + "Sau khi xác minh, bạn có thể đăng nhập tại: http://localhost:8080/auth/login và đổi mật khẩu.\n\n"
                    + "Trân trọng.";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email.trim());
            message.setSubject("Xác nhận tài khoản sinh viên");
            message.setText(messageBody);

            mailSender.send(message);

            log.info("✅ Đã gửi email xác nhận thành công đến: {}", email);
        } catch (Exception e) {
            log.error("❌ Gửi email thất bại đến {}: {}", email, e.getMessage(), e);
        }
    }


    private List<StudentDTO> parseFile(MultipartFile file) {
        List<StudentDTO> students = new ArrayList<>();
        List<Integer> errorRows = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            // int rowNum = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if(row.getRowNum() == 0) continue;
                // if (rowNum++ == 0) continue; // Skip header
                // if (row == null || isRowEmpty(row)) {
                //     log.warn("⚠️ Bỏ qua dòng rỗng số {}", row.getRowNum());
                //     continue;
                // }

                // if (row.getLastCellNum() < 9) {
                //     log.warn("❗ Dòng {} có số lượng cột không đủ ({}). Bỏ qua.", row.getRowNum(), row.getLastCellNum());
                //     continue;
                // }
                StudentDTO dto = new StudentDTO();
                dto.setName(getSafeStringCell(row, 0, formatter, evaluator));
                dto.setStudentCode(getSafeStringCell(row, 1, formatter, evaluator));
                dto.setEmail(getSafeStringCell(row, 2, formatter, evaluator));
                dto.setPhoneNumber(getSafeStringCell(row, 3, formatter, evaluator));
                dto.setDob(getSafeDate(row.getCell(4)));
                String graduatedYearStr = getSafeStringCell(row, 5, formatter, evaluator);
                try {
                    dto.setGraduatedYear(Integer.parseInt(graduatedYearStr));
                } catch (NumberFormatException e) {
                    log.warn("⚠️ Không thể parse graduatedYear tại dòng {}: '{}'", i + 1, graduatedYearStr);
                    dto.setGraduatedYear(null);
                }
                String genderStr = getSafeStringCell(row, 6, formatter, evaluator);
                try {
                    dto.setGender(Gender.valueOf(genderStr.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    log.warn("⚠️ Giá trị gender không hợp lệ tại dòng {}: '{}'", i + 1, genderStr);
                    dto.setGender(null);
                }
                String districtIdStr = getSafeStringCell(row, 7, formatter, evaluator);
                    dto.setDistrictId(districtIdStr.isEmpty() ? null : Long.parseLong(districtIdStr));

                String orgFacultyIdStr = getSafeStringCell(row, 8, formatter, evaluator);
                dto.setOrganizationFacultyId(orgFacultyIdStr.isEmpty() ? null : Long.parseLong(orgFacultyIdStr));

                students.add(dto);


            }

        } catch (IOException e) {
            log.error("❌ Lỗi khi đọc file Excel", e);
            throw new RuntimeException("Lỗi khi đọc file: " + e.getMessage());
        }

        // if (!errorRows.isEmpty()) {
        // }

        return students;
    }

    private String getSafeStringCell(Row row, int colIndex, DataFormatter formatter, FormulaEvaluator evaluator) {
        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        if (cell.getCellType() == CellType.FORMULA) {
            CellValue evaluated = evaluator.evaluate(cell);
            return formatter.formatCellValue(cell, evaluator); // dùng evaluator để lấy giá trị thực
        }
        return formatter.formatCellValue(cell);
    }


    private Date getSafeDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                boolean isDate = DateUtil.isCellDateFormatted(cell);
                if (isDate) {
                    return cell.getDateCellValue();
                } else {
                    Date fallback = DateUtil.getJavaDate(cell.getNumericCellValue());
                    return fallback;
                }
            }

            if (cell.getCellType() == CellType.STRING) {
                log.info("🔤 Cell is STRING: '{}'", cell.getStringCellValue());
            }
        } catch (Exception e) {
            log.error("❌ Exception trong getSafeDate: {}", e.getMessage(), e);
        }

        return null;
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
    
    public static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public BasePageResponse<StudentDTOResponse> getStudent(StudentInternFilter request, Pageable pageable){
        Organization uni = getUniFromToken();
        Specification<Student> spec = studentSpecification.buildSpec(request, uni);

        Sort sort = baseSpecification.buildSort(request, "name");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Student> page = studentRepository.findAll(spec, sortPageable);
        Page<StudentDTOResponse> result = page.map(studentMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }

    @Override
    public InternDeclareRequestDTOResponse getDetailInternDeclare(Long studentId){
        InternDeclareRequest request = internDeclareRequestRepository.findTopByStudentIdOrderByCreatedAtDesc(studentId)
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.INTERN_DECLARE_NOT_FOUND));

        return internDelcareRequestMapper.mapEntityToResponse(request);
    }

    // @Override
    // public BasePageResponse<StudentDTOResponse> getStudent(StudentInternFilter request, Pageable pageable){
    //     Organization uni = getUniFromToken();
    //     Specification<Student> spec = studentSpecification.buildSpec(request, uni);

    //     Sort sort = baseSpecification.buildSort(request, "name");
    //     Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

    //     Page<Student> page = studentRepository.findAll(spec, sortPageable);
    //     Page<StudentDTOResponse> result = page.map(studentMapper::mapEntityToResponse);
    //     return PageUtils.fromPage(result);
    // }

    @Override
    public InternRequestApprovedDTOReponse handelAppoved(Long id, InternRequestApprovedDTORequest request){
        Organization uni = getUniFromToken();
        
        InternDeclareRequest internRequest = internDeclareRequestRepository.findById(id)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.INTERN_DECLARE_NOT_FOUND));
        
        if (internRequest.getStatusIntern() != StatusIntern.WAITING) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.INTERN_DECLARE_WATITING);
        }

        if(request.getStatusIntern().equals(StatusIntern.REJECTED) && !StringUtils.hasText(request.getNote())){
            throw ExceptionUtil.fromErrorCode(ErrorCode.INTERN_DECLARE_NOTE);
        }

        String approvedBy = SecurityContextHolder.getContext().getAuthentication().getName();

        internRequest.setStatusIntern(request.getStatusIntern());
        internRequest.setNote(request.getNote());
        internRequest.setApprovedBy(approvedBy);

        return internDeclareApprovedMapper.mapEntityToResponse(internRequest);
    }

    @Override
    public BasePageResponse<OrganizationDTO> getAllUni(BaseFilterRequest request, Pageable pageable){
        Specification<Organization> spec = baseSpecificationUni
                    .build(request, "organizationName", null, null, null, null)
                    .and((root, query, cb) -> cb.equal(root.get("organizationType"), OrganizationType.UNIVERSITY));
        Sort sort = baseSpecificationUni.buildSort(request, "organizationName");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Organization> page = organizationRepository.findAll(spec, sortPageable);
        Page<OrganizationDTO> result = page.map(organizationMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }

    @Override
    public String exportStudentListByFilter(StudentInternFilter request) {
        Organization uni = getUniFromToken();
        Specification<Student> spec = studentSpecification.buildSpec(request, uni);

        List<Student> students = studentRepository.findAll(spec, Sort.by("name"));
        List<StudentDTOResponse> dtoList = students.stream()
            .map(studentMapper::mapEntityToResponse)
            .collect(Collectors.toList());

        return writeStudentExcel.writeStudentExcel(dtoList); // Không cần try-catch IOException
    }


}
