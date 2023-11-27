import useCourseEntities from "../course/useCourseEntities.ts";
import {Homework} from "../../entities/Homework.ts";


const useCourseHomeworks = (courseId: number | string) =>
    useCourseEntities<Homework>(courseId, "homeworks");

export default useCourseHomeworks;
