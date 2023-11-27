import useCourseEntities from "../course/useCourseEntities.ts";
import {Attendance} from "../../entities/attendance/Attendance.ts";

const useCourseAttendances = (courseId: number | string) =>
    useCourseEntities<Attendance>(courseId, "attendances");

export default useCourseAttendances
