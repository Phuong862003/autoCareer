package com.demo.autocareer.model.enums;

public enum ApplyJobStatus {
    PENDING,        // Đang chờ xử lý (vừa mới apply)
    REVIEWED,       // Nhà tuyển dụng đã xem hồ sơ
    INTERVIEW,      // Đã lên lịch/phỏng vấn
    ACCEPTED,       // Đã nhận (trúng tuyển)
    REJECTED,       // Bị từ chối
    CANCELLED
}
