import {CACHE_KEY_COURSES} from "../../utils/constants.ts";
import {useFormik} from "formik";
import * as Yup from "yup";
import {CREATE_ATTENDANCE_VALIDATION_SCHEMA} from "../../schemas/attendance-schemas.ts";
import {AttendanceType} from "../../entities/attendance/AttendanceType.ts";
import {AttendancePeriod} from "../../entities/attendance/AttendancePeriod.ts";
import useCreateAttendance from "./useCreateAttendance.ts";

const useCreateAttendanceForm = (courseId: string, onSuccess: () => void) => {
  const createAttendance = useCreateAttendance(
    [...CACHE_KEY_COURSES, courseId, "attendances"],
    onSuccess,
  );

  return useFormik({
    initialValues: {
      type: AttendanceType.ABSENT,
      period: AttendancePeriod.LESSON_1,
      studentId: 0,
    },
    validationSchema: Yup.object(CREATE_ATTENDANCE_VALIDATION_SCHEMA),
    onSubmit: (values, { resetForm }) => {
      createAttendance.mutate({ ...values, courseId });
      resetForm();
    },
  });
};

export default useCreateAttendanceForm;
