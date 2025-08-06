// package com.demo.autocareer.service.impl;

// package service

// import (
// 	"fmt"
// 	"github.com/casbin/casbin/v2/config"
// 	"github.com/xuri/excelize/v2"
// 	"gorm.io/gorm"
// 	"log"
// 	"os"
// 	"q-cdio/internal/infrastructure"
// 	"q-cdio/internal/model"
// 	"q-cdio/repository"
// 	"sort"
// 	"strconv"
// 	"strings"
// 	"time"
// )

// const (
// 	EXPORT_SHEET_NAME      = "exportLisOutlineInfor"
// 	EXPORT_FOLDER          = "storage/export_excel"
// 	EXPORT_PERMISSION_FILE = 0755
// 	EXPORT_HEIGHT_ROW      = 20
// 	COLUMN_CODE            = 1
// 	COLUMN_OUTLINE_CODE    = 2
// 	COLUMN_STATUS          = 3
// 	COLUMN_YEAR            = 4
// 	COLUMN_KTHP            = 5
// 	COLUMN_TIME            = 6
// 	COLUMN_COURSE_NAME     = 7
// 	COLUMN_XCTD            = 8 // xâu công thức điểm
// 	COLUMN_ERROR           = 9
// 	SEMESTER_2021_ID       = 1
// 	SEMESTER_2024_ID       = 2
// 	PROGRAM_2021           = "Chương trình 2021"
// 	PROGRAM_2024           = "Chương trình 2024"
// 	STATUS_DONE            = "DONE"
// 	STATUS_DEAN_APPROVED   = "DEAN_APPROVED"
// 	END_LESSION            = "TKTHP"
// 	UNKNOWN_ERROR          = "Lỗi không xác định"
// 	COLUMN_OUTLINE_ID      = 0

// 	EXPERIMENTAL_PRACTICE                   = "THTN" // Thực hành thí nghiệm
// 	EXPERIMENTAL_PRACTICE_POINT             = "#THI#*1.000"
// 	GRADUATION_THESIS_PROJECT_1             = "DAKLTN" // Đồ án khóa luận tốt nghiêp(bảo vệ)
// 	GRADUATION_THESIS_PROJECT_1_POINT       = "#GVHD#*0.250+#GVPB#*0.250+#HD#*0.500"
// 	GRADUATION_THESIS_PROJECT_2             = "DA/KLTN 2" // Đồ án khóa luận tốt nghiêp(chấm quyển)
// 	GRADUATION_THESIS_PROJECT_2_POINT       = "#GVHD#*0.330+#GVPB#*0.330+#BC#*0.330"
// 	GRADUATE_INTERNSHIP                     = "TTTN" // Thực tập tôt nghiệp
// 	GRADUATE_INTERNSHIP_POINT               = "#QTTT#*0.500+#BC#*0.500"
// 	PRACTICAL_ENTERPRISE_INTERNSHIP         = "TTTTDN" // Thực tập thực tế doanh nghiệp
// 	PRACTICAL_ENTERPRISE_INTERNSHIP_POINT   = "#BC#*1.000"
// 	PRACTICAL_ENTERPRISE_INTERNSHIP_POINT_2 = "#BC1#*0.500 + #BC2#*0.500"
// 	CLINICAL_PRACTICE                       = "THBV" // Thực hành lâm sàng
// 	CLINICAL_PRACTICE_POINT                 = "#THI1#*1.000"
// 	PRACTICE_LANGUAGE                       = "LT"     // Thực hành tiếng
// 	SPECIALIZED_COURSE_PROJECTS             = "DAMHCN" // Đồ án môn học/ chuyên ngành
// 	SPECIALIZED_COURSE_PROJECTS_POINT       = "#BC#*1.000"
// 	SPECIALIZED_COURSE_PROJECTS_POINT_1     = "#THI1#*1.00"
// 	SPECIALIZED_COURSE_PROJECTS_POINT_2     = "#QTTT#*0.500 + #BC#*0.500"
// 	SPECIALIZED_COURSE_PROJECTS_POINT_3     = "#QTTT#*0.500 + #THI1#*0.500"
// 	THEORY_COMBINED_PRACTICE                = "LTTH" // Lý thuyết kết hợp thực hành

// 	GRADUATE_INTERNSHIP_1     = "Báo cáo thực tập tốt nghiệp"
// 	GRADUATION_THESIS_PROJECT = "Chấm đồ án/ KLTN"

// 	PROCESS_POINT   = "ĐQT" // Điểm quá trình
// 	DILIGENCE       = "CC"  // Chuyên cần
// 	REPORTING_POINT = "BCTH"
// 	PROJECT_REPORT  = "BCDA"

// 	//evaluateForm
// 	FORM_1 = 1 // HPLT, LTTH, LT
// 	FORM_2 = 2 // THBV, TTTTDN, THTN
// 	FORM_3 = 3 // DAMHCN
// 	FORM_4 = 4 // TTTN
// 	FORM_5 = 5 // DAKLTN
// 	FORM_6 = 6 // DA/KLTN 2
// )

// type ExportListOutlineService interface {
// 	GetWorkingDir() string
// 	CreateFolder(folderName string)
// 	WriteHeader() *exportListOutlineService
// 	Save(filename string) (string, error)
// 	WriteExportOutline(outlines []model.Outline)
// 	ExportOutline() string
// }
// type exportListOutlineService struct {
// 	db                *gorm.DB
// 	exportOutlineRepo repository.ExportOutlineRepository
// 	cursor            *excelize.File
// 	FileName          string
// 	Header            []interface{}
// 	CurrentIndex      int
// }

// type outlineTemp struct {
// 	outline model.Outline
// 	version int
// }

// func (s *exportListOutlineService) GetWorkingDir() string {
// 	workingDir, err := os.Getwd()
// 	if err != nil {
// 		panic(err)
// 	}
// 	return workingDir
// }

// func (s *exportListOutlineService) CreateFolder(folderName string) {
// 	workingDir := s.GetWorkingDir()
// 	folderQor := fmt.Sprintf("%s%s/%s", workingDir, config.Config{}, folderName)
// 	if _, err := os.Stat(folderQor); err == nil && os.IsNotExist(err) {
// 		err = os.MkdirAll(folderQor, EXPORT_PERMISSION_FILE)
// 		if err != nil {
// 			panic(err)
// 		}
// 	}
// }

// func (s *exportListOutlineService) WriteHeader() *exportListOutlineService {
// 	style, err := s.cursor.NewStyle(&excelize.Style{
// 		Font: &excelize.Font{
// 			Bold:  true,
// 			Size:  12,
// 			Color: "#FFFFFF",
// 		},
// 		Fill: excelize.Fill{
// 			Type:    "pattern",
// 			Color:   []string{"#063970"},
// 			Pattern: 1,
// 		},
// 		Alignment: &excelize.Alignment{
// 			Horizontal: "center",
// 			Vertical:   "center",
// 		},
// 	})
// 	if err != nil {
// 		panic(err)
// 	}

// 	if err := s.cursor.MergeCell(EXPORT_SHEET_NAME, "A1", "J1"); err != nil {
// 		panic(err)
// 	}
// 	newHeader := "EXPORT ĐỀ CƯƠNG"
// 	err = s.cursor.SetSheetRow(EXPORT_SHEET_NAME, "A1", &[]interface{}{newHeader})
// 	if err != nil {
// 		return nil
// 	}
// 	err = s.cursor.SetCellStyle(EXPORT_SHEET_NAME, "A1", fmt.Sprintf("J1"), style)
// 	if err != nil {
// 		return nil
// 	}
// 	err = s.cursor.SetSheetRow(EXPORT_SHEET_NAME, "A2", &s.Header)
// 	if err != nil {
// 		return nil
// 	}
// 	err = s.cursor.SetCellStyle(EXPORT_SHEET_NAME, "A2", fmt.Sprintf("J2"), style)
// 	if err != nil {
// 		return nil
// 	}
// 	return s
// }

// func NewExportOutlineService() ExportListOutlineService {
// 	db := infrastructure.GetDB()
// 	excel := excelize.NewFile()
// 	// Setup sheet name
// 	sheetName := EXPORT_SHEET_NAME
// 	if _, err := excel.NewSheet(sheetName); err != nil {
// 		log.Fatalf("Unable to create new sheet: %v", err)
// 		return nil
// 	}
// 	if err := excel.DeleteSheet("Sheet1"); err != nil {
// 		log.Fatalf("Unable to delete default sheet: %v", err)
// 		return nil
// 	}

// 	// Khai báo chiều rộng, chiều cao của ô
// 	if err := excel.SetRowHeight(sheetName, 1, 30); err != nil {
// 		log.Fatalf("Unable to set row height: %v", err)
// 		return nil
// 	}

// 	// Thiết lập chiều rộng cho các cột
// 	for col := 'A'; col <= 'I'; col++ {
// 		if err := excel.SetColWidth(sheetName, string(col), string(col), 15); err != nil {
// 			log.Fatalf("Unable to set column width for %s: %v", string(col), err)
// 			return nil
// 		}
// 	}

// 	// Khai báo sheet header
// 	sheetHeader := []interface{}{
// 		"ID", "Mã Học Phần", "Mã Đề Cương", "Tình Trạng", "Bộ Chương Trình",
// 		"Hình Thức Thi KTHP", "Thời Gian Thi", "Tên Học Phần",
// 		"Xâu Công Thức Điểm", "Lỗi",
// 	}

// 	exportOutlineRepo := repository.NewExportOutlineRepository()

// 	return &exportListOutlineService{
// 		db:                db,
// 		exportOutlineRepo: exportOutlineRepo,
// 		cursor:            excel,
// 		FileName:          fmt.Sprintf("Danh_Sach_De_Cuong_%v", time.Now().Unix()),
// 		Header:            sheetHeader,
// 		CurrentIndex:      2,
// 	}
// }

// func (s *exportListOutlineService) Save(filename string) (string, error) {
// 	workingDir := s.GetWorkingDir()
// 	folderPath := fmt.Sprintf("%s/%s", workingDir, EXPORT_FOLDER)

// 	// Kiểm tra xem thư mục da tồn tại chưa
// 	if _, err := os.Stat(folderPath); os.IsNotExist(err) {
// 		if err := os.MkdirAll(folderPath, EXPORT_PERMISSION_FILE); err != nil {
// 			return "", err
// 		}
// 	}

// 	filePath := fmt.Sprintf("%s/%s.xlsx", folderPath, filename)
// 	if err := s.cursor.SaveAs(filePath); err != nil {
// 		return "", err
// 	}
// 	return filePath, nil
// }

// func (s *exportListOutlineService) WriteExportOutline(outlines []model.Outline) {
// 	outlineMap := make(map[string]outlineTemp)
// 	for _, outline := range outlines {
// 		if outline.Status != STATUS_DONE && outline.Status != STATUS_DEAN_APPROVED {
// 			continue
// 		}

// 		var courseCode string
// 		if outline.Course != nil {
// 			courseCode = outline.Course.Code
// 		} else {
// 			continue
// 		}
// 		if existingOutline, exists := outlineMap[courseCode]; exists {
// 			if outline.DeanApproved != nil && *outline.DeanApproved {
// 				if existingOutline.outline.DeanApprovedDate != nil {
// 					if outline.DeanApprovedDate.After(*existingOutline.outline.DeanApprovedDate) {
// 						outlineMap[courseCode] = outlineTemp{
// 							outline: outline,
// 							version: extractVersion(outline.Code),
// 						}
// 					}
// 				} else {
// 					outlineMap[courseCode] = outlineTemp{
// 						outline: outline,
// 						version: extractVersion(outline.Code),
// 					}
// 				}
// 			}
// 		} else {
// 			outlineMap[courseCode] = outlineTemp{
// 				outline: outline,
// 				version: extractVersion(outline.Code),
// 			}
// 		}
// 	}

// 	for _, tempOutline := range outlineMap {
// 		outline := tempOutline.outline

// 		err := s.cursor.SetRowHeight(EXPORT_SHEET_NAME, s.CurrentIndex+1, EXPORT_HEIGHT_ROW)
// 		if err != nil {
// 			return
// 		}
// 		value := make([]interface{}, 12)
// 		value[COLUMN_OUTLINE_ID] = outline.ID
// 		if outline.Course != nil {
// 			value[COLUMN_CODE] = outline.Course.Code
// 		} else {
// 			value[COLUMN_ERROR] = UNKNOWN_ERROR + "Mã Khoá học"
// 		}

// 		value[COLUMN_OUTLINE_CODE] = outline.Code
// 		value[COLUMN_STATUS] = outline.Status

// 		if outline.Status == "DONE" {
// 			value[COLUMN_STATUS] = "Đã biên soạn xong"
// 		} else if outline.Status == "DEAN_APPROVED" {
// 			value[COLUMN_STATUS] = "Khoa đã duyệt"
// 		}

// 		if outline.ScholasticSemesterID == SEMESTER_2021_ID {
// 			value[COLUMN_YEAR] = PROGRAM_2021
// 		} else if outline.ScholasticSemesterID == SEMESTER_2024_ID {
// 			value[COLUMN_YEAR] = PROGRAM_2024
// 		} else {
// 			value[COLUMN_ERROR] = UNKNOWN_ERROR + "năm học"
// 		}
// 		if outline.ResultEvaluates != nil {
// 			var kthpTitles []string
// 			var kthpTimes []string
// 			for _, result := range outline.ResultEvaluates {
// 				if result.Type == END_LESSION {
// 					if len(result.EvaluateForms) == 1 {
// 						evaluateForm := result.EvaluateForms[0]
// 						if evaluateForm.ExamType != nil && evaluateForm.ExamType.Title != "" {
// 							kthpTitles = append(kthpTitles, evaluateForm.ExamType.Title)
// 						}
// 						if result.ExamTime > 0 {
// 							kthpTimes = append(kthpTimes, fmt.Sprintf("%d", result.ExamTime))
// 						} else {
// 							kthpTimes = append(kthpTimes, "0")
// 						}
// 					} else if len(result.EvaluateForms) > 1 {
// 						for _, evaluateForm := range result.EvaluateForms {
// 							if evaluateForm.ExamType != nil && evaluateForm.ExamType.Title != "" {
// 								kthpTitles = append(kthpTitles, evaluateForm.ExamType.Title)
// 							}
// 							if result.ExamTime > 0 {
// 								kthpTimes = append(kthpTimes, fmt.Sprintf("%d", result.ExamTime))
// 							} else {
// 								kthpTimes = append(kthpTimes, "0")
// 							}
// 							break
// 						}
// 					}
// 				}
// 			}

// 			if len(kthpTitles) > 0 && len(kthpTimes) > 0 {
// 				value[COLUMN_KTHP] = strings.Join(kthpTitles, "+")
// 				value[COLUMN_TIME] = strings.Join(kthpTimes, "+")
// 			} else if len(kthpTitles) > 0 {
// 				value[COLUMN_KTHP] = strings.Join(kthpTitles, "+")
// 				value[COLUMN_TIME] = "0"
// 			} else if len(kthpTimes) > 0 {
// 				value[COLUMN_KTHP] = ""
// 				value[COLUMN_TIME] = strings.Join(kthpTimes, "+")
// 			}
// 		}

// 		if outline.Course != nil {
// 			value[COLUMN_COURSE_NAME] = outline.Course.Title
// 		} else {
// 			value[COLUMN_COURSE_NAME] = UNKNOWN_ERROR + "tên khóa học"
// 		}
// 		if outline.Course != nil && outline.Course.CourseType != nil {
// 			if outline.Course.CourseType.EvaluateForm != 0 {
// 				switch outline.Course.CourseType.EvaluateForm {
// 				case FORM_1:
// 					if outline.ResultEvaluates != nil {
// 						var ccPoints []string
// 						var ltPoints []string
// 						var thPoints []string
// 						var kthpPoints []string
// 						var ccCount, ltCount, thCount, kthpCount int
// 						var processPointResults []model.ResultEvaluate
// 						var endLessionResults []model.ResultEvaluate

// 						ltProcessed := false
// 						thProcessed := false

// 						for _, result := range outline.ResultEvaluates {
// 							switch result.Type {
// 							case DILIGENCE:
// 								ccCount++
// 								ccPoints = append(ccPoints, fmt.Sprintf("#CC%d#*%.3f", ccCount, result.CoursePointWeight/100))

// 							case PROCESS_POINT:
// 								processPointResults = append(processPointResults, result)

// 							case END_LESSION:
// 								endLessionResults = append(endLessionResults, result)
// 							}
// 						}

// 						switch outline.Course.CourseType.Code {
// 						case THEORY_COMBINED_PRACTICE:
// 							for _, result := range endLessionResults {
// 								hasBTL := false
// 								for _, evalForm := range result.EvaluateForms {
// 									if evalForm.ExamType != nil && (evalForm.ExamType.Code == "BTL1" || evalForm.ExamType.Code == "BC") {
// 										hasBTL = true
// 										break
// 									}
// 								}
// 								if hasBTL {
// 									kthpPoints = append(kthpPoints, fmt.Sprintf("#BC#*%.3f", result.CoursePointWeight/100))
// 								} else {
// 									kthpCount++
// 									kthpPoints = append(kthpPoints, fmt.Sprintf("#THI%d#*%.3f", kthpCount, result.CoursePointWeight/100))
// 								}
// 							}
// 						case PRACTICES_LANGUAGE:
// 							if len(endLessionResults) == 1 {
// 								kthpPoints = append(kthpPoints, fmt.Sprintf("#THI#*%.3f", endLessionResults[0].CoursePointWeight/100))
// 							} else {
// 								for i, res := range endLessionResults {
// 									kthpPoints = append(kthpPoints, fmt.Sprintf("#THI%d#*%.3f", i+1, res.CoursePointWeight/100))
// 								}
// 							}
// 						}
// 						numLt := 0
// 						if outline.Course.NumOfTestLt != nil {
// 							numLt = int(*outline.Course.NumOfTestLt)
// 						}
// 						if !ltProcessed && numLt > 0 {
// 							for i := 1; i <= numLt; i++ {
// 								if len(processPointResults) > 0 {
// 									currentWeight := processPointResults[0].CoursePointWeight
// 									ltPoints = append(ltPoints, fmt.Sprintf("#LT_BAI%d#*%.3f", ltCount+1, currentWeight/100))
// 									ltCount++
// 									processPointResults = processPointResults[1:]
// 								}
// 							}
// 							ltProcessed = true
// 						}
// 						numTh := 0
// 						if outline.Course.NumOfTestTh != nil {
// 							numTh = int(*outline.Course.NumOfTestTh)
// 						}
// 						if !thProcessed && numTh > 0 {
// 							for i := 1; i <= numTh; i++ {
// 								if len(processPointResults) > 0 {
// 									currentWeight := processPointResults[0].CoursePointWeight
// 									thPoints = append(thPoints, fmt.Sprintf("#TH_BAI%d#*%.3f", thCount+1, currentWeight/100))
// 									thCount++
// 									processPointResults = processPointResults[1:]
// 								}
// 							}
// 							thProcessed = true
// 						}

// 						resultPoints := append(ccPoints, ltPoints...)
// 						resultPoints = append(resultPoints, thPoints...)
// 						resultPoints = append(resultPoints, kthpPoints...)
// 						value[COLUMN_XCTD] = strings.Join(resultPoints, "+")
// 					}
// 				case FORM_2:
// 					var kthpCount, thCount int
// 					var thPoints, kthpPoints []string
// 					var reportingPoints []model.ResultEvaluate

// 					if outline.ResultEvaluates != nil {
// 						for _, result := range outline.ResultEvaluates {
// 							if result.Type == REPORTING_POINT {
// 								reportingPoints = append(reportingPoints, result)
// 							}
// 						}

// 						switch outline.Course.CourseType.Code {
// 						case CLINICAL_PRACTICE: // THBV
// 							if len(reportingPoints) == 1 {
// 								kthpCount++
// 								kthpPoints = append(kthpPoints, fmt.Sprintf("#THI%d#*%.3f", kthpCount, reportingPoints[0].CoursePointWeight/100))
// 							} else {
// 								sort.SliceStable(reportingPoints, func(i, j int) bool {
// 									return reportingPoints[i].CoursePointWeight < reportingPoints[j].CoursePointWeight
// 								})

// 								for _, result := range reportingPoints {
// 									if result.Type == END_LESSION {
// 										kthpCount++
// 										kthpPoints = append(kthpPoints, fmt.Sprintf("#THI%d#*%.3f", kthpCount, result.CoursePointWeight/100))
// 									} else {
// 										thCount++
// 										thPoints = append(thPoints, fmt.Sprintf("#TH_BAI%d#*%.3f", thCount, result.CoursePointWeight/100))
// 									}
// 								}
// 							}

// 						case EXPERIMENTAL_PRACTICE: // THTN
// 							if len(reportingPoints) == 1 {
// 								kthpCount++
// 								kthpPoints = append(kthpPoints, fmt.Sprintf("#THI%d#*%.3f", kthpCount, reportingPoints[0].CoursePointWeight/100))
// 							} else {
// 								sort.SliceStable(reportingPoints, func(i, j int) bool {
// 									return reportingPoints[i].CoursePointWeight < reportingPoints[j].CoursePointWeight
// 								})
// 								for i, result := range reportingPoints {
// 									thPoints = append(thPoints, fmt.Sprintf("#TH_BAI%d#*%.3f", i+1, result.CoursePointWeight/100))
// 								}
// 							}

// 						case PRACTICAL_ENTERPRISE_INTERNSHIP: // TTTTDN
// 							if len(reportingPoints) == 1 {
// 								value[COLUMN_XCTD] = PRACTICAL_ENTERPRISE_INTERNSHIP_POINT
// 							} else {
// 								value[COLUMN_XCTD] = PRACTICAL_ENTERPRISE_INTERNSHIP_POINT_2
// 							}
// 						}
// 					}

// 					if outline.Course.CourseType.Code == CLINICAL_PRACTICE || outline.Course.CourseType.Code == EXPERIMENTAL_PRACTICE {
// 						finalPoints := append(thPoints, kthpPoints...)
// 						if len(finalPoints) > 0 {
// 							value[COLUMN_XCTD] = strings.Join(finalPoints, "+")
// 						}
// 					}
// 					//}
// 				case FORM_3:
// 					var qtttWeight, otherWeight float32
// 					var hasBC bool
// 					var examCount int
// 					var kthpTitle string

// 					if outline.ResultEvaluates != nil {
// 						for _, result := range outline.ResultEvaluates {
// 							if result.Type == PROJECT_REPORT {
// 								examCount++

// 								for _, evalForm := range result.EvaluateForms {
// 									if evalForm.ExamType == nil {
// 										continue
// 									}

// 									code := evalForm.ExamType.Code
// 									title := evalForm.ExamType.Title

// 									if code == "BTL2" || code == "BC1" {
// 										hasBC = true
// 										otherWeight = result.CoursePointWeight / 100
// 										if kthpTitle == "" {
// 											kthpTitle = title
// 										}
// 										break
// 									} else if code == "BTL1" || code == "BC" {
// 										otherWeight = result.CoursePointWeight / 100
// 										if kthpTitle == "" {
// 											kthpTitle = title
// 										}
// 									} else {
// 										qtttWeight = result.CoursePointWeight / 100
// 									}
// 								}
// 							}
// 						}
// 					}

// 					if examCount == 1 {
// 						if hasBC {
// 							value[COLUMN_XCTD] = SPECIALIZED_COURSE_PROJECTS_POINT_1
// 						} else {
// 							value[COLUMN_XCTD] = SPECIALIZED_COURSE_PROJECTS_POINT
// 						}
// 						value[COLUMN_KTHP] = kthpTitle
// 					} else if examCount == 2 {
// 						if hasBC {
// 							value[COLUMN_XCTD] = fmt.Sprintf("#QTTT#*%.3f+#THI1#*%.3f", qtttWeight, otherWeight)
// 						} else {
// 							value[COLUMN_XCTD] = fmt.Sprintf("#QTTT#*%.3f+#BC#*%.3f", qtttWeight, otherWeight)
// 						}
// 						value[COLUMN_KTHP] = kthpTitle
// 					}

// 				case FORM_4:
// 					if outline.Course.CourseType.Code == GRADUATE_INTERNSHIP {
// 						value[COLUMN_XCTD] = GRADUATE_INTERNSHIP_POINT
// 						value[COLUMN_KTHP] = GRADUATE_INTERNSHIP_1
// 					}
// 				case FORM_5:
// 					if outline.Course.CourseType.Code == GRADUATION_THESIS_PROJECT_1 {
// 						value[COLUMN_XCTD] = GRADUATION_THESIS_PROJECT_1_POINT
// 						value[COLUMN_KTHP] = GRADUATION_THESIS_PROJECT
// 					}
// 				case FORM_6:
// 					if outline.Course.CourseType.Code == GRADUATION_THESIS_PROJECT_2 {
// 						value[COLUMN_XCTD] = GRADUATION_THESIS_PROJECT_2_POINT
// 						value[COLUMN_KTHP] = GRADUATION_THESIS_PROJECT
// 					}
// 				default:
// 				}
// 			} else {
// 				switch outline.Course.CourseType.Code {
// 				case GRADUATION_THESIS_PROJECT_2:
// 					value[COLUMN_XCTD] = GRADUATION_THESIS_PROJECT_2_POINT
// 					value[COLUMN_KTHP] = GRADUATION_THESIS_PROJECT
// 				case GRADUATE_INTERNSHIP:
// 					value[COLUMN_XCTD] = GRADUATE_INTERNSHIP_POINT
// 					value[COLUMN_KTHP] = GRADUATE_INTERNSHIP_1
// 				case GRADUATION_THESIS_PROJECT_1:
// 					value[COLUMN_XCTD] = GRADUATION_THESIS_PROJECT_1_POINT
// 					value[COLUMN_KTHP] = GRADUATION_THESIS_PROJECT
// 				case EXPERIMENTAL_PRACTICE:
// 					//value[COLUMN_XCTD] = EXPERIMENTAL_PRACTICE_POINT
// 					var kthpCount int
// 					var thPoints, kthpPoints []string
// 					var reportingPoints []model.ResultEvaluate
// 					if outline.ResultEvaluates != nil {
// 						for _, result := range outline.ResultEvaluates {
// 							if result.Type == REPORTING_POINT {
// 								reportingPoints = append(reportingPoints, result)
// 							}
// 						}
// 						if len(reportingPoints) == 1 {
// 							kthpCount++
// 							kthpPoints = append(kthpPoints, fmt.Sprintf("#THI%d#*%.3f", kthpCount, reportingPoints[0].CoursePointWeight/100))
// 						} else {
// 							sort.SliceStable(reportingPoints, func(i, j int) bool {
// 								return reportingPoints[i].CoursePointWeight < reportingPoints[j].CoursePointWeight
// 							})
// 							for i, result := range reportingPoints {
// 								thPoints = append(thPoints, fmt.Sprintf("#TH_BAI%d#*%.3f", i+1, result.CoursePointWeight/100))
// 							}
// 						}
// 					}
// 				case PRACTICAL_ENTERPRISE_INTERNSHIP:
// 					if outline.ResultEvaluates != nil {
// 						var bcthCount = 1
// 						for _, result := range outline.ResultEvaluates {
// 							if result.Type == "BCTH" {
// 								bcthCount += bcthCount
// 							}
// 						}
// 						if bcthCount == 1 {
// 							value[COLUMN_XCTD] = "#BC#*1.000"
// 						} else {
// 							value[COLUMN_XCTD] = "#BC1#*0.500+#BC2#*0.500"
// 						}
// 					}
// 				case CLINICAL_PRACTICE:
// 					if outline.ResultEvaluates != nil {
// 						var kthpCount, thCount int
// 						var thPoints, kthpPoints []string
// 						var reportingPoints []model.ResultEvaluate
// 						for _, result := range outline.ResultEvaluates {
// 							if result.Type == REPORTING_POINT {
// 								reportingPoints = append(reportingPoints, result)
// 							}
// 						}

// 						if len(reportingPoints) == 1 {
// 							kthpCount++
// 							kthpPoints = append(kthpPoints, fmt.Sprintf("#THI%d#*%.3f", kthpCount, reportingPoints[0].CoursePointWeight/100))
// 						} else {
// 							sort.SliceStable(reportingPoints, func(i, j int) bool {
// 								return reportingPoints[i].CoursePointWeight < reportingPoints[j].CoursePointWeight
// 							})

// 							for _, result := range reportingPoints {
// 								if result.Type == END_LESSION {
// 									kthpCount++
// 									kthpPoints = append(kthpPoints, fmt.Sprintf("#THI%d#*%.3f", kthpCount, result.CoursePointWeight/100))
// 								} else {
// 									thCount++
// 									thPoints = append(thPoints, fmt.Sprintf("#TH_BAI%d#*%.3f", thCount, result.CoursePointWeight/100))
// 								}
// 							}
// 						}
// 					}

// 				case PRACTICE_LANGUAGE:
// 					if outline.ResultEvaluates != nil && outline.Course != nil {
// 						var ccPoints []string
// 						var ltPoints []string
// 						var thPoints []string
// 						var otherPoints []string
// 						var kthpPoint string
// 						var endLessionResults []model.ResultEvaluate
// 						testLTAdded := make(map[int]bool)
// 						testTHAdded := make(map[int]bool)
// 						ccCount := 1
// 						ltCount := 0
// 						thCount := 0
// 						//kthpCount := 0
// 						numOfTestLt := outline.Course.NumOfTestLt
// 						numOfTestTh := outline.Course.NumOfTestTh

// 						for _, result := range outline.ResultEvaluates {
// 							switch result.Type {
// 							case END_LESSION:
// 								//kthpCount++
// 								//kthpPoint = fmt.Sprintf("#KTHP%d#*%.3f", kthpCount, result.CoursePointWeight/100)
// 								endLessionResults = append(endLessionResults, result)
// 							case PROCESS_POINT:
// 								if numOfTestLt != nil && *numOfTestLt > 0 {
// 									for i := 1; i <= int(*numOfTestLt); i++ {
// 										if !testLTAdded[i] {
// 											ltPoints = append(ltPoints, fmt.Sprintf("#LT_BAI%d#*%.3f", i, result.CoursePointWeight/100))
// 											testLTAdded[i] = true
// 											ltCount++
// 										}
// 									}
// 								}
// 								if numOfTestTh != nil && *numOfTestTh > 0 {
// 									for i := 1; i <= int(*numOfTestTh); i++ {
// 										if !testTHAdded[i] {
// 											thPoints = append(thPoints, fmt.Sprintf("#TH_BAI%d#*%.3f", i, result.CoursePointWeight/100))
// 											testTHAdded[i] = true
// 											thCount++
// 										}
// 									}
// 								}

// 							case DILIGENCE:
// 								ccPoints = append(ccPoints, fmt.Sprintf("#CC%d#*%.3f", ccCount, result.CoursePointWeight/100))
// 								ccCount++

// 							default:
// 								otherPoints = append(otherPoints, fmt.Sprintf("%s#*%.3f", result.Type, result.CoursePointWeight/100))
// 							}
// 						}

// 						resultPoints := append(ccPoints, ltPoints...)
// 						resultPoints = append(resultPoints, thPoints...)
// 						resultPoints = append(resultPoints, otherPoints...)
// 						if kthpPoint != "" {
// 							resultPoints = append(resultPoints, kthpPoint)
// 						}

// 						value[COLUMN_XCTD] = strings.Join(resultPoints, "+")
// 					}
// 				case SPECIALIZED_COURSE_PROJECTS:
// 					//value[COLUMN_XCTD] = SPECIALIZED_COURSE_PROJECTS_POINT
// 					var qtttWeight, otherWeight float32
// 					var hasBC bool
// 					var examCount int
// 					var kthpTitle string

// 					if outline.ResultEvaluates != nil {
// 						for _, result := range outline.ResultEvaluates {
// 							if result.Type == PROJECT_REPORT {
// 								examCount++
// 								qtttWeight = result.CoursePointWeight / 100

// 								for _, evalForm := range result.EvaluateForms {
// 									if evalForm.ExamType == nil {
// 										continue
// 									}

// 									code := evalForm.ExamType.Code
// 									title := evalForm.ExamType.Title

// 									if code == "BTL2" || code == "BC1" {
// 										hasBC = true
// 										otherWeight = result.CoursePointWeight / 100
// 										if kthpTitle == "" {
// 											kthpTitle = title
// 										}
// 										break
// 									} else if code == "BTL1" || code == "BC" {
// 										otherWeight = result.CoursePointWeight / 100
// 										if kthpTitle == "" {
// 											kthpTitle = title
// 										}
// 									} else {
// 										qtttWeight = result.CoursePointWeight / 100
// 									}
// 								}
// 							}
// 						}
// 					}

// 					if examCount == 1 {
// 						if hasBC {
// 							value[COLUMN_XCTD] = SPECIALIZED_COURSE_PROJECTS_POINT_1
// 						} else {
// 							value[COLUMN_XCTD] = SPECIALIZED_COURSE_PROJECTS_POINT
// 						}
// 						value[COLUMN_KTHP] = kthpTitle
// 					} else if examCount == 2 {
// 						if hasBC {
// 							value[COLUMN_XCTD] = fmt.Sprintf("#QTTT#*%.3f+#THI1#*%.3f", qtttWeight, otherWeight)
// 						} else {
// 							value[COLUMN_XCTD] = fmt.Sprintf("#QTTT#*%.3f+#BC#*%.3f", qtttWeight, otherWeight)
// 						}
// 						value[COLUMN_KTHP] = kthpTitle
// 					}
// 				case THEORY_COMBINED_PRACTICE:
// 					if outline.ResultEvaluates != nil && outline.Course != nil {
// 						var ccPoints []string
// 						var ltPoints []string
// 						var thPoints []string
// 						var otherPoints []string
// 						var kthpPoints []string
// 						totalPoints := 0.00
// 						ccCount := 1
// 						kthpCount := 1
// 						//endLessionCount := 1

// 						var processPointResults []model.ResultEvaluate
// 						for _, result := range outline.ResultEvaluates {
// 							if result.Type == END_LESSION {
// 								//kthpPoints = append(kthpPoints, fmt.Sprintf("#KTHP#*%.3f", result.CoursePointWeight/100))
// 								//totalPoints += float64(result.CoursePointWeight) / 100
// 								//endLessionResults = append(endLessionResults, result)
// 								hasBTL := false
// 								for _, evalForm := range result.EvaluateForms {
// 									if evalForm.ExamType != nil && (evalForm.ExamType.Code == "BTL1" || evalForm.ExamType.Code == "BC") {
// 										hasBTL = true
// 										break
// 									}
// 								}
// 								if hasBTL {
// 									kthpPoints = append(kthpPoints, fmt.Sprintf("#BC#*%.3f", result.CoursePointWeight/100))
// 								} else {
// 									kthpPoints = append(kthpPoints, fmt.Sprintf("#THI%d#*%.3f", kthpCount, result.CoursePointWeight/100))
// 									totalPoints += float64(result.CoursePointWeight) / 100
// 									kthpCount++
// 								}
// 							}
// 							if result.Type == PROCESS_POINT {
// 								processPointResults = append(processPointResults, result)
// 							} else if result.Type == DILIGENCE {
// 								ccPoints = append(ccPoints, fmt.Sprintf("#CC%d#*%.3f", ccCount, result.CoursePointWeight/100))
// 								totalPoints += float64(result.CoursePointWeight) / 100
// 								ccCount++
// 							} else {
// 								otherPoints = append(otherPoints, fmt.Sprintf("%s#*%.3f", result.Type, result.CoursePointWeight/100))
// 								totalPoints += float64(result.CoursePointWeight) / 100
// 							}
// 						}

// 						if outline.Course.NumOfTestLt != nil && *outline.Course.NumOfTestLt > 0 {
// 							for i := 1; i <= int(*outline.Course.NumOfTestLt); i++ {
// 								if len(processPointResults) > 0 {
// 									currentWeight := processPointResults[0].CoursePointWeight
// 									ltPoints = append(ltPoints, fmt.Sprintf("#LT_BAI%d#*%.3f", i, currentWeight/100))
// 									totalPoints += float64(currentWeight) / 100
// 									processPointResults = processPointResults[1:]
// 								}
// 							}
// 						}

// 						if outline.Course.NumOfTestTh != nil && *outline.Course.NumOfTestTh > 0 {
// 							for i := 1; i <= int(*outline.Course.NumOfTestTh); i++ {
// 								if len(processPointResults) > 0 {
// 									currentWeight := processPointResults[0].CoursePointWeight
// 									thPoints = append(thPoints, fmt.Sprintf("#TH_BAI%d#*%.3f", i, currentWeight/100))
// 									totalPoints += float64(currentWeight) / 100
// 									processPointResults = processPointResults[1:]
// 								}
// 							}
// 						}

// 						resultPoints := append(ccPoints, ltPoints...)
// 						resultPoints = append(resultPoints, thPoints...)
// 						resultPoints = append(resultPoints, otherPoints...)
// 						resultPoints = append(resultPoints, kthpPoints...)

// 						value[COLUMN_XCTD] = strings.Join(resultPoints, "+")
// 					}
// 				}
// 			}
// 		} else {
// 			value[COLUMN_ERROR] = UNKNOWN_ERROR
// 		}
// 		value[COLUMN_ERROR] = ""

// 		err = s.cursor.SetSheetRow(EXPORT_SHEET_NAME, fmt.Sprintf("A%d", s.CurrentIndex+1), &value)
// 		if err != nil {
// 			return
// 		}
// 		s.CurrentIndex++
// 	}
// }

// func (s *exportListOutlineService) ExportOutline() string {
// 	// khoi tao file excel
// 	excelFile := NewExportOutlineService()

// 	// tao folder luu file
// 	folderInfo := EXPORT_FOLDER
// 	excelFile.CreateFolder(folderInfo)

// 	// viet header
// 	excelFile.WriteHeader()
// 	// tim tat ca cac nganh
// 	listOutline, err := s.exportOutlineRepo.GetOutlineInfo()
// 	if err != nil {
// 		panic(err)
// 	}
// 	excelFile.WriteExportOutline(listOutline)
// 	_, err = excelFile.Save(s.FileName)
// 	if err != nil {
// 		panic(err)
// 	}
// 	return fmt.Sprintf("/%s/%s.xlsx", EXPORT_FOLDER, s.FileName)
// }

// func extractVersion(code string) int {
// 	parts := strings.Split(code, ".")
// 	if len(parts) > 1 {
// 		version, err := strconv.Atoi(parts[len(parts)-1])
// 		if err == nil {
// 			return version
// 		}
// 	}
// 	return 1
// }
