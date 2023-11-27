import { AttendancePeriod } from "./AttendancePeriod.ts";
import { AttendanceType } from "./AttendanceType.ts";

export interface Attendance {
  id: number;
  type: AttendanceType;
  period: AttendancePeriod;
  datePublished: string;
  courseName: string;
  courseTeacher: string;
  studentName: string;
}
