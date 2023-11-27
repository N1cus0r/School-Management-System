import * as Yup from "yup";
import {AttendanceType} from "../entities/attendance/AttendanceType.ts";
import {AttendancePeriod} from "../entities/attendance/AttendancePeriod.ts";

export const UPDATE_ATTENDANCE_VALIDATION_SCHEMA = {
  type: Yup.string()
    .oneOf([
      AttendanceType.LATE,
      AttendanceType.ABSENT,
      AttendanceType.MOTIVATED,
    ])
    .required("This field is required"),
  period: Yup.string()
    .oneOf([
      AttendancePeriod.LESSON_1,
      AttendancePeriod.LESSON_2,
      AttendancePeriod.LESSON_3,
      AttendancePeriod.LESSON_4,
      AttendancePeriod.LESSON_5,
      AttendancePeriod.LESSON_6,
      AttendancePeriod.LESSON_7,
    ])
    .required("This field is required"),
};

export const CREATE_ATTENDANCE_VALIDATION_SCHEMA = {
  ...UPDATE_ATTENDANCE_VALIDATION_SCHEMA,
  studentId: Yup.number().required("This field is required").min(1),
};
