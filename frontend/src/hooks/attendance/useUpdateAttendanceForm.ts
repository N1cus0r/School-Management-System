import {Attendance} from "../../entities/attendance/Attendance.ts";
import {CACHE_KEY_COURSES} from "../../utils/constants.ts";
import useUpdateAttendance from "./useUpdateAttendance.ts";
import {useFormik} from "formik";
import * as Yup from "yup";
import {UPDATE_ATTENDANCE_VALIDATION_SCHEMA} from "../../schemas/attendance-schemas.ts";

const useUpdateAttendanceForm = (
  courseId: string,
  attendance: Attendance,
  onSuccess: () => void,
) => {
  const updateAttendance = useUpdateAttendance(
    [...CACHE_KEY_COURSES, courseId, "attendances"],
    onSuccess,
  );

  return useFormik({
    initialValues: { type: attendance.type, period: attendance.period },
    validationSchema: Yup.object(UPDATE_ATTENDANCE_VALIDATION_SCHEMA),
    onSubmit: (values) => updateAttendance.mutate([attendance.id, values]),
  });
};

export default useUpdateAttendanceForm;
