import useCourseEntities from "../course/useCourseEntities.ts";
import {User} from "../../entities/user/User.ts";

const useCourseStudents = (courseId: number | string) =>
    useCourseEntities<User>(courseId, "students");

export default useCourseStudents
