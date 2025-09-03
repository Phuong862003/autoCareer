package com.demo.autocareer.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 400 - Bad Request
    EMAIL_ALREADY_EXISTS("EMAIL_EXISTS", "Email đã tồn tại", 400),
    INVALID_PASSWORD("INVALID_PASSWORD", "Mật khẩu cũ không đúng", 400),
    DUPLICATE_PASSWORD("DUPLICATE_PASSWORD", "Mật khẩu mới không được trùng với mật khẩu cũ", 400),
    INVALID_PASSWORD_FORMAT("INVALID_PASSWORD_FORMAT", "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và ký tự đặc biệt", 400),
    INVALID_FILE_FORMAT("INVALID_FILE_FORMAT", "Lỗi file upload", 400),
    INTERN_DECLARE_WATITING("INTERN_DECLARE_WATITING", "Chỉ được duyệt yêu cầu ở trạng thái chờ", 400),
    INTERN_DECLARE_NOTE("INTERN_DECLARE_NOTE", "Yêu cầu nhập lý do từ chối", 400),
    INTERN_REQUEST_PENDING("INTERN_REQUEST_PENDING", "Chỉ được duyệt yêu cầu ở trạng thái chờ", 400),
    INTERN_REQUEST_APPROVED("INTERN_REQUEST_APPROVED", "Yêu cầu thực tập chưa được doanh nghiệp chấp nhận", 400),
    PASSWORDS_DO_NOT_MATCH("PASSWORDS_DO_NOT_MATCH", "Mật khẩu không khớp", 400),
    DISTRICT_NOT_IN_PROVINCE("DISTRICT_NOT_IN_PROVINCE", "Khong ton tai district ung voi provice", 400),
    STATUS_REQUIRED("STATUS_REQUIRED", "Yêu cầu chọn trạng thái", 400),
    INVALID_STATUS_TRANSITION("INVALID_STATUS_TRANSITION", "Không thể chuyển trạng thái", 400),

    // 403 - Forbidden
    ACCESS_DENIED("ACCESS_DENIED", "Bạn không có quyền truy cập", 403),
    REFRESH_TOKEN_NOT_FOUND("REFRESH_TOKEN_NOT_FOUND", "Refresh token không tồn tại trong hệ thống", 403),
    INVALID_TOKEN("INVALID_TOKEN","Refresh token không hợp lệ hoặc đã hết hạn", 403),
    ACCOUNT_NOT_ACTIVATED("ACCOUNT_NOT_ACTIVATED", "Tài khoản chưa được kích hoạt", 403),
    ROLE_NOT_ALLOWED("ROLE_NOT_ALLOWED", "Không được sử dụng role là STUDENT", 403),

    // 404 - Not Found
    USER_NOT_FOUND("USER_NOT_FOUND", "Người dùng không tồn tại", 404),
    EMAIL_NOT_FOUND("EMAIL_NOT_FOUND", "Email không tồn tại trong hệ thống", 404),
    ROLE_NOT_FOUND("ROLE_NOT_FOUND", "Vai trò không hợp lệ", 404),
    STUDENT_NOT_FOUND("STUDENT_NOT_FOUND", "Sinh viên không tồn tại", 404),
    DISTRICT_NOT_FOUND("DISTRICT_NOT_FOUND", "District không tồn tại", 404),
    ORG_FACULTY_NOT_FOUND("ORG_FACULTY_NOT_FOUND", "Orgnization faculty không tồn tại", 404),
    SUBFIELD_NOT_FOUND("SUBFIELD_NOT_FOUND", "SubField không tồn tại", 404),
    JOB_NOT_FOUND("JOB_NOT_FOUND", "Job không tồn tại", 404),
    COMPANY_NOT_FOUND("COMPANY_NOT_FOUND", "Không tìm thấy công ty", 404),
    UNIVERSITY_NOT_FOUND("UNIVERSITY_NOT_FOUND", "Không tìm thấy trường đại học", 404),
    INTERN_DECLARE_NOT_FOUND("INTERN_DECLARE_NOT_FOUND", "khong tim thay du lieu cua intern declare", 404),
    INTERN_REQUEST_NOT_FOUND("INTERN_REQUEST_NOT_FOUND", "khong tim thay internship request hop le", 404),
    PROVINCE_NOT_FOUND("PROVINCE_NOT_FOUND", "Khong tim thay province", 404),
    ORGANIZATION_NAME_REQUIRED("ORGANIZATION_NAME_REQUIRED", "Yeu cau nhap organizationName", 404),
    FIELD_NOT_FOUND("FIELD_NOT_FOUND", "Không tìm thấy field", 404),
    APPLY_JOB_NOT_FOUND("APPLY_JOB_NOT_FOUND", "Không tìm thấy apply job", 404),
    INTERNSHIP_SEMESTER_NOT_FOUND("INTERNSHIP_SEMESTER_NOT_FOUND", "Không tìm thấy sinh viên hợp lệ", 404),

    // 500 - Internal Server Error
    EMAIL_SEND_FAIL("EMAIL_SEND_FAIL", "Không thể gửi email", 500);

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
