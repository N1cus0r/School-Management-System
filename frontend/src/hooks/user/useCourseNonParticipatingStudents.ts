import useCourseEntities from "../course/useCourseEntities.ts";
import { User } from "../../entities/user/User.ts";

const useCourseNonParticipatingStudents = (courseId: number | string) =>
  useCourseEntities<User>(courseId, "non-participating-students");

export default useCourseNonParticipatingStudents

