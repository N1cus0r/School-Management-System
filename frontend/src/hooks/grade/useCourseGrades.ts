import useCourseEntities from "../course/useCourseEntities.ts";
import {Grade} from "../../entities/Grade.ts";

const useCourseGrades = (courseId: number | string) =>
  useCourseEntities<Grade>(courseId, "grades");

export default useCourseGrades;
